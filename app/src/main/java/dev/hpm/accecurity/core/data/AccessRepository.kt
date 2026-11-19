package dev.hpm.accecurity.core.data

import dev.hpm.accecurity.core.database.entity.PendingMovementEntity
import dev.hpm.accecurity.core.model.AccessSyncStatus
import dev.hpm.accecurity.core.model.TipoUsuario
import dev.hpm.accecurity.core.sync.NetworkMonitor
import dev.hpm.accecurity.core.sync.SyncDecision
import dev.hpm.accecurity.core.sync.asMovement
import dev.hpm.accecurity.core.sync.decideMovementSync
import dev.hpm.accecurity.core.sync.mergeDownloadedPasses
import dev.hpm.accecurity.core.sync.mergeDownloadedMovements
import java.time.ZonedDateTime
import java.util.UUID
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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
    private val network: NetworkMonitor,
    private val scheduleSync: () -> Unit,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    private val workerMutex = Mutex()
    private val synchronizing = MutableStateFlow(false)
    @Volatile private var listenerEpoch = 0L

    init {
        syncScope.launch {
            network.online.collect { online -> if (online) requestSync() }
        }
    }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeCurrentUser(): Flow<UserEntity?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.uid)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged().flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf(null) else database.userDao().observeById(uid)
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
     * Limpia las vistas de la sesión, conservando la cola privada hasta que su guardia vuelva a entrar.
     */
    suspend fun clearLocalCache() = withContext(Dispatchers.IO) {
        stopPassListener()
        syncMutex.withLock {
            database.withTransaction {
                database.userDao().clear()
                database.passDao().clear()
                database.movementDao().clear()
            }
        }
    }

    /**
     * Mantiene la copia local al día mientras haya sesión. El jefe ve las
     * solicitudes nuevas sin recargar la pantalla a mano.
     */
    fun startSyncWithAuth() {
        if (authListener != null) return
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            stopPassListener()
            if (firebaseAuth.currentUser != null) {
                startPassListener()
                requestSync()
            }
        }
        authListener = listener
        auth.addAuthStateListener(listener)
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext
        val epoch = listenerEpoch
        val passes = firestore.collection("pases").get().await().documents.map { it.toPassEntity() }
        val movements = firestore.collection("movimientos").get().await().documents.map { it.toMovementEntity() }
        storeDownloadedPasses(passes, uid, epoch)
        storeDownloadedMovements(movements, uid, epoch)
    }

    private fun startPassListener() {
        val uid = auth.currentUser?.uid ?: return
        val epoch = listenerEpoch
        startMovementListener(uid, epoch)
        if (passListener != null) return
        passListener = firestore.collection("pases").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val passes = snapshot.documents.map { it.toPassEntity() }
            syncScope.launch { storeDownloadedPasses(passes, uid, epoch) }
        }
    }

    private fun startMovementListener(uid: String, epoch: Long) {
        if (movementListener != null) return
        movementListener = firestore.collection("movimientos").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val movements = snapshot.documents.map { it.toMovementEntity() }
            syncScope.launch { storeDownloadedMovements(movements, uid, epoch) }
        }
    }

    private suspend fun storeDownloadedPasses(passes: List<PassEntity>, uid: String, epoch: Long) {
        syncMutex.withLock {
            database.withTransaction {
                if (auth.currentUser?.uid != uid || listenerEpoch != epoch) return@withTransaction
                val ledger = database.pendingMovementDao().forActor(uid)
                database.passDao().replaceAll(mergeDownloadedPasses(passes, ledger))
            }
        }
    }

    private suspend fun storeDownloadedMovements(movements: List<MovementEntity>, uid: String, epoch: Long) {
        syncMutex.withLock {
            database.withTransaction {
                if (auth.currentUser?.uid != uid || listenerEpoch != epoch) return@withTransaction
                val ledger = database.pendingMovementDao().forActor(uid)
                database.movementDao().replaceAll(mergeDownloadedMovements(movements, ledger))
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
        listenerEpoch += 1
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

    /** Con señal intenta actualizar; sin señal valida exclusivamente pases ya descargados. */
    suspend fun lookupPassForEntry(rawCode: String): PassLookupResult = withContext(Dispatchers.IO) {
        val code = rawCode.trim()
        if (!isSupportedPassCode(code)) return@withContext PassLookupResult.Invalid("El QR no es un pase válido de Accecurity.")
        val uid = cachedGuardId()
            ?: return@withContext PassLookupResult.Invalid("Iniciá sesión como guardia con internet antes de trabajar sin conexión.")
        if (database.pendingMovementDao().foreignUnresolved(code, uid) > 0) {
            return@withContext PassLookupResult.Invalid("Este pase tiene movimientos pendientes de otra cuenta. Esa cuenta debe sincronizarlos primero.")
        }
        val known = database.passDao().findById(code)
        // Una admisión local no se revierte por un snapshot remoto desactualizado.
        if (known?.status !in setOf("INSIDE", "LEFT") && network.hasInternet()) {
            try {
                withTimeout(6000) {
                    val role = firestore.collection("users").document(uid).get(Source.SERVER).await()
                    if (!role.isGuard()) {
                        database.userDao().findById(uid)?.let { database.userDao().upsert(it.copy(role = "UNKNOWN")) }
                        throw AccessDeniedException("Solo el guardia puede registrar entradas. Volvé a iniciar sesión.")
                    }
                    val document = firestore.collection("pases").document(code).get(Source.SERVER).await()
                    if (!document.exists()) throw AccessDeniedException("No existe un pase para este código QR.")
                    val downloaded = document.toPassEntity()
                    syncMutex.withLock {
                        database.withTransaction {
                            if (auth.currentUser?.uid != uid) throw AccessDeniedException("La sesión cambió.")
                            val ledger = database.pendingMovementDao().forActor(uid).filter { it.passSnapshot.id == code }
                            database.passDao().upsertAll(mergeDownloadedPasses(listOf(downloaded), ledger))
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                // Se puede seguir con la copia previamente descargada.
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (!isTransientSyncError(error)) return@withContext PassLookupResult.Invalid(accessErrorMessage(error))
            }
        }
        if (auth.currentUser?.uid != uid) return@withContext PassLookupResult.Invalid("La sesión cambió.")
        val pass = database.passDao().findById(code)?.toScannedPass()
            ?: return@withContext PassLookupResult.Invalid("Este pase no está descargado. Conectate a internet para consultarlo.")
        val error = validatePassEntry(pass, code)
        if (error == null) PassLookupResult.Valid(pass) else PassLookupResult.Invalid(error)
    }

    suspend fun registerEntry(rawCode: String): RegisterEntryResult = withContext(Dispatchers.IO) {
        try {
            val recorded = saveLocalMovement(rawCode.trim(), "ENTRY")
            requestSync()
            RegisterEntryResult.Success(recorded.passSnapshot.visitorName, recorded.time)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            RegisterEntryResult.Failure(accessErrorMessage(error))
        }
    }

    /** La lista local sigue disponible cuando no hay señal. */
    suspend fun refreshPassesForExit(): String? = withContext(Dispatchers.IO) {
        if (cachedGuardId() == null) return@withContext "Iniciá sesión como guardia antes de registrar salidas."
        if (!network.hasInternet()) return@withContext null
        val uid = auth.currentUser?.uid ?: return@withContext "Iniciá sesión nuevamente."
        val epoch = listenerEpoch
        try {
            val passes = withTimeout(6000) {
                firestore.collection("pases").get(Source.SERVER).await().documents.map { it.toPassEntity() }
            }
            storeDownloadedPasses(passes, uid, epoch)
            null
        } catch (_: TimeoutCancellationException) {
            null
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            if (isTransientSyncError(error)) null else accessErrorMessage(error, "Volvé a cargar la lista.")
        }
    }

    suspend fun registerExit(passId: String): RegisterExitResult = withContext(Dispatchers.IO) {
        try {
            val recorded = saveLocalMovement(passId, "EXIT")
            requestSync()
            RegisterExitResult.Success(recorded.passSnapshot.visitorName, recorded.time)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            RegisterExitResult.Failure(accessErrorMessage(error, "Volvé a cargar la lista."))
        }
    }

    private suspend fun cachedGuardId(): String? {
        val uid = auth.currentUser?.uid ?: return null
        val user = database.userDao().findById(uid)
        return uid.takeIf { user?.role.equals("GUARDIA", ignoreCase = true) }
    }

    private suspend fun saveLocalMovement(passId: String, action: String): PendingMovementEntity {
        if (!isSupportedPassCode(passId)) throw AccessDeniedException("El pase no es válido.")
        return syncMutex.withLock {
            database.withTransaction {
                val uid = cachedGuardId() ?: throw AccessDeniedException("Iniciá sesión como guardia con internet antes de trabajar sin conexión.")
                if (database.pendingMovementDao().foreignUnresolved(passId, uid) > 0) {
                    throw AccessDeniedException("Este pase tiene movimientos pendientes de otra cuenta.")
                }
                val pass = database.passDao().findById(passId)
                    ?: throw AccessDeniedException("Este pase no está descargado en el teléfono.")
                val now = ZonedDateTime.now()
                val time = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                val error = if (action == "ENTRY") validatePassEntry(pass.toScannedPass(), passId, now.toLocalDateTime())
                else validatePassExit(pass.status, pass.entryTime, pass.exitTime)
                if (error != null) throw AccessDeniedException(error)
                val updated = if (action == "ENTRY") pass.copy(status = "INSIDE", entryTime = time)
                else pass.copy(status = "LEFT", exitTime = time)
                val previous = database.pendingMovementDao().latestForPass(uid, passId)
                val operation = PendingMovementEntity(
                    operationId = UUID.randomUUID().toString(), actorId = uid, action = action,
                    time = time, occurredAtMillis = now.toInstant().toEpochMilli(), zoneId = now.zone.id,
                    dependsOnOperationId = if (action == "EXIT" && previous?.action == "ENTRY") previous.operationId else null,
                    passSnapshot = updated
                )
                val sequence = database.pendingMovementDao().insert(operation)
                database.passDao().upsertAll(listOf(updated))
                database.movementDao().upsertAll(listOf(operation.asMovement()))
                operation.copy(sequence = sequence)
            }
        }
    }

    /** Sesión persistida de garita: permite reabrir la app sin pedir otro login con red. */
    suspend fun restoreGuardSession(): AuthenticatedSession? = withContext(Dispatchers.IO) {
        val uid = cachedGuardId() ?: return@withContext null
        val user = database.userDao().findById(uid) ?: return@withContext null
        if (auth.currentUser?.uid != uid) return@withContext null
        AuthenticatedSession(uid = uid, email = user.email, tipoUsuario = TipoUsuario.GUARDIA)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeSyncStatus(): Flow<AccessSyncStatus> = observeCurrentUser().flatMapLatest { user ->
        if (user == null) network.online.map { AccessSyncStatus(online = it) }
        else combine(network.online, database.pendingMovementDao().observeForActor(user.id), synchronizing) { online, operations, active ->
            val conflicts = operations.filter { it.state == "CONFLICT" }
            AccessSyncStatus(
                online = online,
                pending = operations.count { it.state == "PENDING" },
                conflicts = conflicts.size,
                syncing = active,
                issues = conflicts.map {
                    val action = if (it.action == "ENTRY") "Entrada" else "Salida"
                    "$action de ${it.passSnapshot.visitorName} · ${it.time}\n${it.error}"
                }
            )
        }
    }

    fun requestSync() {
        syncScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            if (database.pendingMovementDao().nextPending(uid) == null) return@launch
            enqueueIfPending()
            if (network.hasInternet()) {
                try { syncPendingMovements() } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    Log.w("AccessRepository", "Se reintentará la sincronización en segundo plano.", error)
                }
            }
        }
    }

    fun retrySyncConflicts() {
        val uid = auth.currentUser?.uid ?: return
        syncScope.launch {
            syncMutex.withLock {
                database.withTransaction {
                    database.pendingMovementDao().retryConflicts(uid)
                    if (auth.currentUser?.uid == uid) {
                        database.movementDao().upsertAll(database.pendingMovementDao().forActor(uid).map { it.asMovement() })
                    }
                }
            }
            requestSync()
        }
    }

    private suspend fun enqueueIfPending() {
        val uid = auth.currentUser?.uid ?: return
        if (database.pendingMovementDao().nextPending(uid) != null) enqueueSafely()
    }

    private fun enqueueSafely() {
        try { scheduleSync() } catch (error: Exception) {
            Log.w("AccessRepository", "La cola está guardada; se volverá a programar al abrir la app.", error)
        }
    }

    /** Devuelve false si WorkManager debe reintentar por red o error temporal. */
    suspend fun syncPendingMovements(): Boolean = withContext(Dispatchers.IO) {
        workerMutex.withLock {
            val uid = auth.currentUser?.uid ?: return@withLock true
            synchronizing.value = true
            try {
                repeat(100) {
                    if (auth.currentUser?.uid != uid) return@withLock true
                    val operation = database.pendingMovementDao().nextPending(uid) ?: return@withLock true
                    val dependency = operation.dependsOnOperationId?.let { database.pendingMovementDao().find(it) }
                    if (dependency?.state == "CONFLICT") {
                        markConflict(operation, "La entrada previa tiene un conflicto. Revisá ambos movimientos.")
                        return@repeat
                    }
                    if (dependency != null && dependency.state != "SYNCED") return@withLock false
                    val decision = try {
                        withTimeout(20000) { synchronizeOperation(operation) }
                    } catch (_: TimeoutCancellationException) {
                        return@withLock false
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        val permissionDenied = generateSequence<Throwable>(error) { it.cause }.take(8)
                            .filterIsInstance<FirebaseFirestoreException>()
                            .any { it.code == FirebaseFirestoreException.Code.PERMISSION_DENIED }
                        if (permissionDenied) {
                            markConflict(operation, "Firebase no permitió sincronizar este movimiento. Revisá los permisos de la cuenta.")
                            return@repeat
                        }
                        return@withLock false
                    }
                    when (decision) {
                        is SyncDecision.Conflict -> markConflict(operation, decision.reason)
                        is SyncDecision.Apply -> acknowledge(operation, decision.pass)
                        is SyncDecision.AlreadyApplied -> acknowledge(operation, decision.pass)
                    }
                }
                requestSync()
                true
            } finally { synchronizing.value = false }
        }
    }

    private suspend fun synchronizeOperation(operation: PendingMovementEntity): SyncDecision {
        val passRef = firestore.collection("pases").document(operation.passSnapshot.id)
        val movementRef = firestore.collection("movimientos").document(operation.operationId)
        return firestore.runTransaction { transaction ->
            if (auth.currentUser?.uid != operation.actorId) throw IllegalStateException("La sesión cambió.")
            val role = transaction.get(firestore.collection("users").document(operation.actorId))
            val pass = transaction.get(passRef)
            val movement = transaction.get(movementRef)
            val decision = decideMovementSync(
                operation = operation,
                remotePass = if (pass.exists()) pass.toPassEntity() else null,
                remoteOperationId = if (movement.exists()) movement.getString("operationId").orEmpty() else null,
                guardAuthorized = role.isGuard()
            )
            if (decision is SyncDecision.Apply) {
                val entry = operation.action == "ENTRY"
                transaction.update(passRef, mapOf(
                    "status" to decision.pass.status,
                    (if (entry) "entryTime" else "exitTime") to operation.time,
                    (if (entry) "entryRegisteredBy" else "exitRegisteredBy") to operation.actorId
                ))
                val record = operation.copy(state = "SYNCED").asMovement()
                transaction.set(movementRef, mapOf(
                    "passId" to record.passId, "title" to record.title,
                    "description" to record.description, "time" to record.time, "type" to record.type,
                    "operationId" to operation.operationId, "registeredBy" to operation.actorId,
                    "occurredAtMillis" to operation.occurredAtMillis, "zoneId" to operation.zoneId
                ))
            }
            decision
        }.await()
    }

    private suspend fun acknowledge(operation: PendingMovementEntity, server: PassEntity) {
        syncMutex.withLock {
            database.withTransaction {
                database.pendingMovementDao().setState(operation.operationId, "SYNCED", "")
                if (auth.currentUser?.uid == operation.actorId) {
                    val ledger = database.pendingMovementDao().forActor(operation.actorId).filter { it.passSnapshot.id == server.id }
                    database.passDao().upsertAll(mergeDownloadedPasses(listOf(server), ledger))
                    database.movementDao().upsertAll(listOf(operation.copy(state = "SYNCED").asMovement()))
                }
            }
        }
    }

    private suspend fun markConflict(operation: PendingMovementEntity, reason: String) {
        syncMutex.withLock {
            database.withTransaction {
                database.pendingMovementDao().setState(operation.operationId, "CONFLICT", reason)
                if (auth.currentUser?.uid == operation.actorId) {
                    database.movementDao().upsertAll(listOf(operation.copy(state = "CONFLICT", error = reason).asMovement()))
                }
            }
        }
    }

    private fun isTransientSyncError(error: Exception): Boolean {
        val firestoreError = generateSequence<Throwable>(error) { it.cause }.take(8)
            .filterIsInstance<FirebaseFirestoreException>().firstOrNull()
        return firestoreError?.code in setOf(
            FirebaseFirestoreException.Code.UNAVAILABLE, FirebaseFirestoreException.Code.DEADLINE_EXCEEDED,
            FirebaseFirestoreException.Code.ABORTED
        )
    }

    private suspend fun cacheRecordedAccess(recorded: RecordedAccess, ownerUid: String) {
        // La operación ya está en Firebase: un error local no debe sugerir registrarla otra vez.
        try {
            syncMutex.withLock {
                database.withTransaction {
                    if (auth.currentUser?.uid != ownerUid) return@withTransaction
                    val ledger = database.pendingMovementDao().forActor(ownerUid).filter { it.passSnapshot.id == recorded.pass.id }
                    database.passDao().upsertAll(mergeDownloadedPasses(listOf(recorded.pass), ledger))
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
            cacheRecordedAccess(recorded, userId)
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
