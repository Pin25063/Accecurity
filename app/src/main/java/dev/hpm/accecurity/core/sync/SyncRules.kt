package dev.hpm.accecurity.core.sync

import dev.hpm.accecurity.core.database.entity.MovementEntity
import dev.hpm.accecurity.core.database.entity.PassEntity
import dev.hpm.accecurity.core.database.entity.PendingMovementEntity
import dev.hpm.accecurity.core.model.ScannedPass
import dev.hpm.accecurity.core.model.validatePassEntry
import dev.hpm.accecurity.core.model.validatePassExit
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

sealed interface SyncDecision {
    data class Apply(val pass: PassEntity) : SyncDecision
    data class AlreadyApplied(val pass: PassEntity) : SyncDecision
    data class Conflict(val reason: String) : SyncDecision
}

/** La hora que se valida es la del escaneo, aunque la sincronización ocurra mucho después. */
fun decideMovementSync(
    operation: PendingMovementEntity,
    remotePass: PassEntity?,
    remoteOperationId: String?,
    guardAuthorized: Boolean
): SyncDecision {
    if (remoteOperationId == operation.operationId) {
        return SyncDecision.AlreadyApplied(remotePass ?: operation.passSnapshot)
    }
    if (remoteOperationId != null) return SyncDecision.Conflict("El comprobante remoto no coincide con este movimiento.")
    if (!guardAuthorized) return SyncDecision.Conflict("La cuenta ya no tiene permiso de guardia.")
    val pass = remotePass ?: return SyncDecision.Conflict("El pase ya no existe en el servidor.")
    val saved = operation.passSnapshot
    if (pass.id != saved.id) return SyncDecision.Conflict("El pase no coincide con el registro local.")
    return when (operation.action) {
        "ENTRY" -> {
            if (pass.visitDate != saved.visitDate || pass.startTime != saved.startTime || pass.endTime != saved.endTime ||
                pass.qrCode.ifBlank { pass.id } != saved.qrCode.ifBlank { saved.id } || pass.visitorName != saved.visitorName) {
                return SyncDecision.Conflict("Los datos o el horario del pase cambiaron después de descargarse.")
            }
            val at = runCatching {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(operation.occurredAtMillis), ZoneId.of(operation.zoneId))
            }.getOrNull() ?: return SyncDecision.Conflict("La hora original del movimiento no es válida.")
            val error = validatePassEntry(ScannedPass(
                id = pass.id, visitorName = pass.visitorName, description = pass.description,
                location = pass.location, requestedBy = pass.requestedBy, visitDate = pass.visitDate,
                startTime = pass.startTime, endTime = pass.endTime, status = pass.status,
                qrCode = pass.qrCode, entryTime = pass.entryTime
            ), saved.qrCode.ifBlank { saved.id }, at)
            if (error != null) SyncDecision.Conflict(error)
            else SyncDecision.Apply(pass.copy(status = "INSIDE", entryTime = operation.time))
        }
        "EXIT" -> {
            val error = validatePassExit(pass.status, pass.entryTime, pass.exitTime)
            when {
                error != null -> SyncDecision.Conflict(error)
                pass.entryTime != saved.entryTime -> SyncDecision.Conflict("La entrada del servidor no coincide con la visita registrada en este teléfono.")
                else -> SyncDecision.Apply(pass.copy(status = "LEFT", exitTime = operation.time))
            }
        }
        else -> SyncDecision.Conflict("Tipo de movimiento no reconocido.")
    }
}

/** Nunca reemplaza una admisión local por un snapshot anterior de EXPECTED. */
fun mergeDownloadedPasses(remote: List<PassEntity>, ledger: List<PendingMovementEntity>): List<PassEntity> {
    val result = remote.associateBy { it.id }.toMutableMap()
    ledger.sortedBy { it.sequence }.groupBy { it.passSnapshot.id }.forEach { (id, operations) ->
        val latest = operations.last()
        val server = result[id]
        val local = latest.passSnapshot
        result[id] = when {
            latest.state != "SYNCED" -> local
            server == null -> local
            local.status == "LEFT" && server.status != "LEFT" -> local
            local.status == "INSIDE" && server.status !in setOf("INSIDE", "LEFT") -> local
            else -> server
        }
    }
    return result.values.toList()
}

fun PendingMovementEntity.asMovement(): MovementEntity {
    val suffix = when (state) { "PENDING" -> " · Pendiente"; "CONFLICT" -> " · Por revisar"; else -> "" }
    return MovementEntity(
        id = operationId,
        passId = passSnapshot.id,
        title = (if (action == "ENTRY") "Entrada registrada" else "Salida registrada") + suffix,
        description = "${passSnapshot.visitorName} · ${passSnapshot.location.ifBlank { "Sin destino" }}",
        time = time,
        type = action
    )
}

fun mergeDownloadedMovements(remote: List<MovementEntity>, ledger: List<PendingMovementEntity>): List<MovementEntity> {
    val result = remote.associateBy { it.id }.toMutableMap()
    ledger.forEach { result[it.operationId] = it.asMovement() }
    return result.values.toList()
}
