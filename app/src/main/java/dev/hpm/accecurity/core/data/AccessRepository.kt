package dev.hpm.accecurity.core.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dev.hpm.accecurity.core.auth.AuthenticatedSession
import dev.hpm.accecurity.core.database.AccecurityDatabase
import dev.hpm.accecurity.core.database.entity.MovementEntity
import dev.hpm.accecurity.core.database.entity.PassEntity
import dev.hpm.accecurity.core.database.entity.UserEntity
import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.displayVisitWindow
import dev.hpm.accecurity.core.model.validateVisitWindow
import dev.hpm.accecurity.core.model.PaseData
import dev.hpm.accecurity.core.model.PersonaAdentroData
import dev.hpm.accecurity.core.model.SolicitudData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Lee Firestore, guarda en Room y expone los datos que pintan las pantallas.
 */
class AccessRepository(
    private val database: AccecurityDatabase,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var passListener: ListenerRegistration? = null
    fun observePasses(): Flow<List<PaseData>> {
        return database.passDao().observeVisible().map { passes ->
            passes.map { it.toPaseData() }
        }
    }

    fun observePending(): Flow<List<SolicitudData>> {
        return database.passDao().observePending().map { passes ->
            passes.map { it.toSolicitud() }
        }
    }

    fun observeInside(): Flow<List<PersonaAdentroData>> {
        return database.passDao().observeInside().map { passes ->
            passes.map { it.toPersonaAdentro() }
        }
    }

    fun observeMovements(): Flow<List<ActividadData>> {
        return database.movementDao().observeAll().map { movements ->
            movements.map { it.toActividad() }
        }
    }

    suspend fun saveSession(session: AuthenticatedSession) = withContext(Dispatchers.IO) {
        database.userDao().upsert(
            UserEntity(
                id = session.uid,
                email = session.email,
                role = session.tipoUsuario.name
            )
        )
    }

    /**
     * Mantiene la copia local al día mientras haya sesión. El jefe ve las
     * solicitudes nuevas sin recargar la pantalla a mano.
     */
    fun startSyncWithAuth() {
        if (authListener != null) return
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                stopPassListener()
            } else {
                startPassListener()
            }
        }
        authListener = listener
        auth.addAuthStateListener(listener)
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val passes = firestore.collection("pases").get().await().documents.map { it.toPassEntity() }
        val movements = firestore.collection("movimientos").get().await().documents.map { document ->
            MovementEntity(
                id = document.id,
                passId = document.string("passId", "paseId"),
                title = document.string("title", "titulo"),
                description = document.string("description", "descripcion"),
                time = document.string("time", "hora"),
                type = normalizeMovementType(document.string("type", "tipo"))
            )
        }
        syncMutex.withLock {
            database.passDao().replaceAll(passes)
            database.movementDao().replaceAll(movements)
        }
    }

    private fun startPassListener() {
        if (passListener != null) return
        passListener = firestore.collection("pases").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val documents = snapshot.documents
            syncScope.launch {
                val passes = documents.map { it.toPassEntity() }
                syncMutex.withLock {
                    database.passDao().replaceAll(passes)
                }
            }
        }
    }

    private fun stopPassListener() {
        passListener?.remove()
        passListener = null
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toPassEntity(): PassEntity {
        return PassEntity(
            id = id,
            visitorName = string("visitorName", "nombreVisitante"),
            description = string("description", "descripcion"),
            schedule = string("schedule", "horario"),
            location = string("location", "ubicacion"),
            requestedBy = string("requestedBy", "solicitadoPor"),
            createdById = string("createdById", "creadoPorId"),
            status = normalizeStatus(string("status", "estado")),
            isUrgent = bool("isUrgent", "esUrgente"),
            qrCode = string("qrCode", "codigoQr"),
            entryTime = string("entryTime", "horaEntrada"),
            exitTime = string("exitTime", "horaSalida"),
            visitDate = string("visitDate", "fechaVisita"),
            startTime = string("startTime", "horaInicio"),
            endTime = string("endTime", "horaFin")
        )
    }

    /**
     * Crea la solicitud en pendiente. El jefe la autoriza después.
     */
    suspend fun createAccessRequest(
        name: String, description: String, date: String, start: String, end: String, location: String
    ): Boolean {
        if (name.isBlank() || description.isBlank() || location.isBlank() || validateVisitWindow(date, start, end) != null) {
            return false
        }
        return withContext(Dispatchers.IO) {
            try {
                val user = auth.currentUser ?: return@withContext false
                val requesterEmail = user.email?.trim().orEmpty()
                if (requesterEmail.isBlank()) return@withContext false
                val passRef = firestore.collection("pases").document()
                passRef.set(
                    mapOf(
                        "visitorName" to name,
                        "description" to description,
                        "schedule" to displayVisitWindow(date, start, end, ""),
                        "visitDate" to date,
                        "startTime" to start,
                        "endTime" to end,
                        "location" to location.trim(),
                        "requestedBy" to requesterEmail,
                        "createdById" to user.uid,
                        "status" to "PENDING",
                        "isUrgent" to false,
                        "qrCode" to passRef.id,
                        "entryTime" to "",
                        "exitTime" to ""
                    )
                ).await()
                firestore.collection("movimientos").document().set(
                    mapOf(
                        "passId" to passRef.id,
                        "title" to "Solicitud de acceso",
                        "description" to "Para $name",
                        "time" to java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        ),
                        "type" to "PASS_CREATED"
                    )
                ).await()
                refresh()
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    /**
     * El jefe autoriza (EXPECTED) o rechaza (REJECTED) una solicitud pendiente.
     */
    suspend fun updatePassStatus(passId: String, status: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val approved = status == "EXPECTED"
                firestore.collection("pases").document(passId).update("status", status).await()
                firestore.collection("movimientos").document().set(
                    mapOf(
                        "passId" to passId,
                        "title" to if (approved) "Solicitud aprobada" else "Solicitud rechazada",
                        "description" to passId,
                        "time" to "",
                        "type" to if (approved) "PASS_CREATED" else "REJECTION"
                    )
                ).await()
                refresh()
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.string(primary: String, fallback: String): String {
    return (getString(primary) ?: getString(fallback) ?: "").trim()
}

private fun com.google.firebase.firestore.DocumentSnapshot.bool(primary: String, fallback: String): Boolean {
    return (get(primary) as? Boolean) ?: (get(fallback) as? Boolean) ?: false
}
