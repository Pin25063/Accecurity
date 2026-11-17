package dev.hpm.accecurity.core.data

import android.util.Log
import androidx.room.withTransaction
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
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
import dev.hpm.accecurity.core.model.PassLookupResult
import dev.hpm.accecurity.core.model.RegisterExitResult
import dev.hpm.accecurity.core.model.validatePassExit
import dev.hpm.accecurity.core.model.RegisterEntryResult
import dev.hpm.accecurity.core.model.validatePassEntry
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
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
    private var movementListener: ListenerRegistration? = null
    fun observePasses(): Flow<List<PaseData>> {
        return database.passDao().observeVisible().map { passes ->
            passes.map { it.toPaseData() }
        }
    }

    /** Cada pantalla de pases se crea dentro de la sesión autenticada actual. */
    fun observeMyPasses(): Flow<List<PaseData>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return database.passDao().observeByCreator(userId).map { passes ->
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
        return combine(database.movementDao().observeAll(), database.passDao().observeVisible()) { movements, passes ->
            val passesById = passes.associateBy { it.id }
            movements.sortedByDescending { movement ->
                listOf("dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm").firstNotNullOfOrNull { format ->
                    runCatching { LocalDateTime.parse(movement.time, DateTimeFormatter.ofPattern(format)) }.getOrNull()
                } ?: LocalDateTime.MIN
            }.map { movement ->
                // Los registros anteriores guardaban el ID como descripción.
                val description = if (movement.description.isBlank() || movement.description == movement.passId) {
                    passesById[movement.passId]?.visitorName?.takeIf { it.isNotBlank() }
                        ?.let { "Para $it" } ?: "Visitante no registrado"
                } else movement.description
                movement.copy(description = description).toActividad()
            }
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
        val movements = firestore.collection("movimientos").get().await().documents.map {
            it.toMovementEntity()
        }
        syncMutex.withLock {
            database.passDao().replaceAll(passes)
            database.movementDao().replaceAll(movements)
        }
    }

    private fun startPassListener() {
        startMovementListener()
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

    private fun startMovementListener() {
        if (movementListener != null) return
        movementListener = firestore.collection("movimientos").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val movements = snapshot.documents.map { it.toMovementEntity() }
            syncScope.launch {
                syncMutex.withLock {
                    database.movementDao().replaceAll(movements)
                }
            }
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toMovementEntity() = MovementEntity(
        id = id,
        passId = string("passId", "paseId"),
        title = string("title", "titulo"),
        description = string("description", "descripcion"),
        time = string("time", "hora"),
        type = normalizeMovementType(string("type", "tipo"))
    )

    private fun stopPassListener() {
        movementListener?.remove()
        movementListener = null
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

    /** Lee del servidor para no autorizar usando un estado anterior en caché. */
    suspend fun lookupPassForEntry(rawCode: String): PassLookupResult = withContext(Dispatchers.IO) {
        val code = rawCode.trim()
        if (!isSupportedPassCode(code)) {
            return@withContext PassLookupResult.Invalid("El QR no es un pase válido de Accecurity.")
        }
        val userId = auth.currentUser?.uid
            ?: return@withContext PassLookupResult.Invalid("Iniciá sesión nuevamente.")
        try {
            val role = firestore.collection("users").document(userId).get(Source.SERVER).await()
            if (!role.isGuard()) {
                return@withContext PassLookupResult.Invalid("Solo el guardia puede registrar entradas.")
            }
            val document = firestore.collection("pases").document(code).get(Source.SERVER).await()
            if (!document.exists()) {
                return@withContext PassLookupResult.Invalid("No existe un pase para este código QR.")
            }
            val pass = document.toPassEntity().toScannedPass()
            val error = validatePassEntry(pass, code)
            if (error == null) PassLookupResult.Valid(pass) else PassLookupResult.Invalid(error)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            PassLookupResult.Invalid(accessErrorMessage(error))
        }
    }

    /** Revisa otra vez el pase y guarda estado e historial en una sola transacción. */
    suspend fun registerEntry(rawCode: String): RegisterEntryResult = withContext(Dispatchers.IO) {
        val code = rawCode.trim()
        if (!isSupportedPassCode(code)) {
            return@withContext RegisterEntryResult.Failure("El código QR no es válido.")
        }
        val userId = auth.currentUser?.uid
            ?: return@withContext RegisterEntryResult.Failure("Iniciá sesión nuevamente.")
        val passRef = firestore.collection("pases").document(code)
        // El ID fijo y la comprobación del estado evitan movimientos duplicados por reintentos.
        val movementRef = firestore.collection("movimientos").document("entrada_$code")
        try {
            val recorded = firestore.runTransaction { transaction ->
                val role = transaction.get(firestore.collection("users").document(userId))
                if (!role.isGuard()) throw AccessDeniedException("Solo el guardia puede registrar entradas.")
                val snapshot = transaction.get(passRef)
                if (!snapshot.exists()) throw AccessDeniedException("El pase ya no existe.")
                val pass = snapshot.toPassEntity()
                val now = LocalDateTime.now()
                validatePassEntry(pass.toScannedPass(), code, now)?.let { throw AccessDeniedException(it) }
                val time = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                val movement = MovementEntity(
                    id = movementRef.id,
                    passId = pass.id,
                    title = "Entrada registrada",
                    description = "${pass.visitorName} · ${pass.location.ifBlank { "Sin destino" }}",
                    time = time,
                    type = "ENTRY"
                )
                transaction.update(passRef, mapOf(
                    "status" to "INSIDE",
                    "entryTime" to time,
                    "entryRegisteredBy" to userId
                ))
                transaction.set(movementRef, mapOf(
                    "passId" to movement.passId,
                    "title" to movement.title,
                    "description" to movement.description,
                    "time" to movement.time,
                    "type" to movement.type,
                    "registeredBy" to userId
                ))
                RecordedAccess(pass.copy(status = "INSIDE", entryTime = time), movement)
            }.await()

            cacheRecordedAccess(recorded)
            RegisterEntryResult.Success(recorded.pass.visitorName, recorded.pass.entryTime)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            RegisterEntryResult.Failure(accessErrorMessage(error))
        }
    }

    /** Actualiza los pases antes de ofrecer la lista de personas adentro. */
    suspend fun refreshPassesForExit(): String? = withContext(Dispatchers.IO) {
        if (auth.currentUser == null) return@withContext "Iniciá sesión nuevamente."
        try {
            val passes = firestore.collection("pases").get(Source.SERVER).await().documents.map {
                it.toPassEntity()
            }
            syncMutex.withLock { database.passDao().replaceAll(passes) }
            null
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            accessErrorMessage(error, "Tocá Reintentar para cargar la lista.")
        }
    }

    /** Confirma que sigue adentro y guarda la salida y su movimiento de forma atómica. */
    suspend fun registerExit(passId: String): RegisterExitResult = withContext(Dispatchers.IO) {
        if (!isSupportedPassCode(passId)) {
            return@withContext RegisterExitResult.Failure("No se pudo identificar el pase seleccionado.")
        }
        val userId = auth.currentUser?.uid
            ?: return@withContext RegisterExitResult.Failure("Iniciá sesión nuevamente.")
        val passRef = firestore.collection("pases").document(passId)
        val movementRef = firestore.collection("movimientos").document("salida_$passId")
        try {
            val recorded = firestore.runTransaction { transaction ->
                val role = transaction.get(firestore.collection("users").document(userId))
                if (!role.isGuard()) throw AccessDeniedException("Solo el guardia puede registrar salidas.")
                val snapshot = transaction.get(passRef)
                if (!snapshot.exists()) throw AccessDeniedException("El pase ya no existe.")
                val pass = snapshot.toPassEntity()
                validatePassExit(pass.status, pass.entryTime, pass.exitTime)?.let {
                    throw AccessDeniedException(it)
                }
                val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                val movement = MovementEntity(
                    id = movementRef.id,
                    passId = pass.id,
                    title = "Salida registrada",
                    description = "${pass.visitorName} · ${pass.location.ifBlank { "Sin destino" }}",
                    time = time,
                    type = "EXIT"
                )
                transaction.update(passRef, mapOf(
                    "status" to "LEFT",
                    "exitTime" to time,
                    "exitRegisteredBy" to userId
                ))
                transaction.set(movementRef, mapOf(
                    "passId" to movement.passId,
                    "title" to movement.title,
                    "description" to movement.description,
                    "time" to movement.time,
                    "type" to movement.type,
                    "registeredBy" to userId
                ))
                RecordedAccess(pass.copy(status = "LEFT", exitTime = time), movement)
            }.await()
            cacheRecordedAccess(recorded)
            RegisterExitResult.Success(recorded.pass.visitorName, recorded.pass.exitTime)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            RegisterExitResult.Failure(accessErrorMessage(error, "Volvé a cargar la lista antes de reintentar."))
        }
    }

    private suspend fun cacheRecordedAccess(recorded: RecordedAccess) {
        // La operación ya está en Firebase: un error local no debe sugerir registrarla otra vez.
        try {
            syncMutex.withLock {
                database.withTransaction {
                    database.passDao().upsertAll(listOf(recorded.pass))
                    database.movementDao().upsertAll(listOf(recorded.movement))
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Log.w("AccessRepository", "Movimiento guardado; caché pendiente de sincronización.", error)
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.isGuard(): Boolean {
        return (getString("role") ?: getString("rol"))?.trim()?.lowercase() == "guardia"
    }

    private fun isSupportedPassCode(code: String): Boolean {
        return code.isNotBlank() && code.length <= 128 && '/' !in code && code != "." && code != ".."
    }

    private fun accessErrorMessage(error: Exception, retryHint: String = "Volvé a escanear el pase."): String {
        val causes = generateSequence<Throwable>(error) { it.cause }.take(8).toList()
        causes.filterIsInstance<AccessDeniedException>().firstOrNull()?.let { return it.message.orEmpty() }
        return when (causes.filterIsInstance<FirebaseFirestoreException>().firstOrNull()?.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "Firebase no permitió la operación. Revisá los permisos de la cuenta de guardia."
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                "No se pudo confirmar con el servidor. Revisá internet. $retryHint"
            else -> "No se pudo completar la operación. $retryHint"
        }
    }

    private data class RecordedAccess(val pass: PassEntity, val movement: MovementEntity)
    private class AccessDeniedException(message: String) : Exception(message)

    /**
     * El jefe autoriza (EXPECTED) o rechaza (REJECTED) una solicitud pendiente.
     */
    suspend fun updatePassStatus(passId: String, status: String): Boolean = withContext(Dispatchers.IO) {
        if (status != "EXPECTED" && status != "REJECTED") return@withContext false
        if (!isSupportedPassCode(passId)) return@withContext false
        val userId = auth.currentUser?.uid ?: return@withContext false
        val passRef = firestore.collection("pases").document(passId)
        val movementRef = firestore.collection("movimientos").document()
        try {
            val recorded = firestore.runTransaction { transaction ->
                val user = transaction.get(firestore.collection("users").document(userId))
                val role = ((user.get("role") as? String) ?: (user.get("rol") as? String))?.trim()?.lowercase()
                if (role != "jefe") throw AccessDeniedException("Solo el jefe puede revisar solicitudes.")
                val snapshot = transaction.get(passRef)
                if (!snapshot.exists()) throw AccessDeniedException("La solicitud ya no existe.")
                val pass = snapshot.toPassEntity()
                if (pass.status != "PENDING") throw AccessDeniedException("La solicitud ya fue revisada.")
                val approved = status == "EXPECTED"
                val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                val movement = MovementEntity(
                    id = movementRef.id,
                    passId = pass.id,
                    title = if (approved) "Solicitud aprobada" else "Solicitud rechazada",
                    description = "Para ${pass.visitorName.ifBlank { "visitante no registrado" }}",
                    time = time,
                    type = if (approved) "PASS_CREATED" else "REJECTION"
                )
                transaction.update(passRef, "status", status)
                transaction.set(movementRef, mapOf(
                    "passId" to movement.passId,
                    "title" to movement.title,
                    "description" to movement.description,
                    "time" to movement.time,
                    "type" to movement.type
                ))
                RecordedAccess(pass.copy(status = status), movement)
            }.await()
            cacheRecordedAccess(recorded)
            true
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            false
        }
    }

}

private fun com.google.firebase.firestore.DocumentSnapshot.string(primary: String, fallback: String): String {
    return (getString(primary) ?: getString(fallback) ?: "").trim()
}

private fun com.google.firebase.firestore.DocumentSnapshot.bool(primary: String, fallback: String): Boolean {
    return (get(primary) as? Boolean) ?: (get(fallback) as? Boolean) ?: false
}
