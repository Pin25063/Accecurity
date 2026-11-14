package dev.hpm.accecurity.core.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.hpm.accecurity.core.auth.AuthenticatedSession
import dev.hpm.accecurity.core.database.AccecurityDatabase
import dev.hpm.accecurity.core.database.entity.MovementEntity
import dev.hpm.accecurity.core.database.entity.PassEntity
import dev.hpm.accecurity.core.database.entity.UserEntity
import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.PaseData
import dev.hpm.accecurity.core.model.PersonaAdentroData
import dev.hpm.accecurity.core.model.SolicitudData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Lee Firestore, guarda en Room y expone los datos que pintan las pantallas.
 */
class AccessRepository(
    private val database: AccecurityDatabase,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
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

    suspend fun saveSession(session: AuthenticatedSession) {
        database.userDao().upsert(
            UserEntity(
                id = session.uid,
                email = session.email,
                role = session.tipoUsuario.name
            )
        )
    }

    suspend fun refresh() {
        val passes = firestore.collection("pases").get().await().documents.map { document ->
            PassEntity(
                id = document.id,
                visitorName = document.string("visitorName", "nombreVisitante"),
                description = document.string("description", "descripcion"),
                schedule = document.string("schedule", "horario"),
                location = document.string("location", "ubicacion"),
                requestedBy = document.string("requestedBy", "solicitadoPor"),
                createdById = document.string("createdById", "creadoPorId"),
                status = normalizeStatus(document.string("status", "estado")),
                isUrgent = document.bool("isUrgent", "esUrgente"),
                qrCode = document.string("qrCode", "codigoQr"),
                entryTime = document.string("entryTime", "horaEntrada"),
                exitTime = document.string("exitTime", "horaSalida")
            )
        }
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
        database.passDao().clear()
        database.passDao().upsertAll(passes)
        database.movementDao().clear()
        database.movementDao().upsertAll(movements)
    }

    suspend fun createExpectedPass(name: String, description: String, time: String): Boolean {
        return try {
            val passRef = firestore.collection("pases").document()
            passRef.set(
                mapOf(
                    "visitorName" to name,
                    "description" to description,
                    "schedule" to time,
                    "location" to "",
                    "requestedBy" to "",
                    "createdById" to (auth.currentUser?.uid ?: ""),
                    "status" to "EXPECTED",
                    "isUrgent" to false,
                    "qrCode" to passRef.id,
                    "entryTime" to "",
                    "exitTime" to ""
                )
            ).await()
            val movementRef = firestore.collection("movimientos").document()
            movementRef.set(
                mapOf(
                    "passId" to passRef.id,
                    "title" to "Digital pass created",
                    "description" to "For $name",
                    "time" to time,
                    "type" to "PASS_CREATED"
                )
            ).await()
            refresh()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun updatePassStatus(passId: String, status: String): Boolean {
        return try {
            firestore.collection("pases").document(passId).update("status", status).await()
            val movementRef = firestore.collection("movimientos").document()
            movementRef.set(
                mapOf(
                    "passId" to passId,
                    "title" to if (status == "EXPECTED") "Request approved" else "Request rejected",
                    "description" to passId,
                    "time" to "",
                    "type" to if (status == "REJECTED") "REJECTION" else "PASS_CREATED"
                )
            ).await()
            refresh()
            true
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
