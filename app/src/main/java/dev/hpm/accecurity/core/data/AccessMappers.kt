package dev.hpm.accecurity.core.data

import dev.hpm.accecurity.core.database.entity.MovementEntity
import dev.hpm.accecurity.core.database.entity.PassEntity
import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.displayVisitWindow
import dev.hpm.accecurity.core.model.ScannedPass
import dev.hpm.accecurity.core.model.PaseData
import dev.hpm.accecurity.core.model.PersonaAdentroData
import dev.hpm.accecurity.core.model.SolicitudData
import dev.hpm.accecurity.core.model.TipoActividad
import dev.hpm.accecurity.core.ui.theme.StatusExpected
import dev.hpm.accecurity.core.ui.theme.StatusExpectedContainer
import dev.hpm.accecurity.core.ui.theme.StatusInside
import dev.hpm.accecurity.core.ui.theme.StatusInsideContainer
import androidx.compose.ui.graphics.Color

fun PassEntity.toPaseData(): PaseData {
    val (label, container, dot) = statusColors(status)
    val time = entryTime.ifBlank { exitTime.ifBlank { schedule } }
    return PaseData(
        iniciales = initialsOf(visitorName),
        nombre = visitorName,
        descripcion = description,
        estado = label,
        hora = time,
        colorEstado = container,
        colorPunto = dot,
        fechaVisita = visitDate,
        horarioAutorizado = displayVisitWindow(visitDate, startTime, endTime, schedule),
        ubicacion = location,
        solicitadoPor = requestedBy,
        id = id,
        codigoQr = qrCode.ifBlank { id },
        estadoAcceso = status
    )
}

fun PassEntity.toScannedPass(): ScannedPass = ScannedPass(
    id = id,
    visitorName = visitorName,
    description = description,
    location = location,
    requestedBy = requestedBy,
    visitDate = visitDate,
    startTime = startTime,
    endTime = endTime,
    status = status,
    qrCode = qrCode,
    entryTime = entryTime
)

fun PassEntity.toSolicitud(): SolicitudData = SolicitudData(
    id = id,
    nombre = visitorName,
    descripcion = description,
    horario = displayVisitWindow(visitDate, startTime, endTime, schedule),
    ubicacion = location,
    solicitadoPor = requestedBy,
    esUrgente = isUrgent
)

fun PassEntity.toPersonaAdentro(): PersonaAdentroData = PersonaAdentroData(
    id = id,
    nombre = visitorName,
    descripcion = description,
    destino = location,
    horaEntrada = entryTime.ifBlank { schedule }
)

fun MovementEntity.toActividad(): ActividadData = ActividadData(
    id = id,
    titulo = title,
    descripcion = description,
    hora = time,
    tipo = when (type) {
        "ENTRY" -> TipoActividad.ENTRADA
        "EXIT" -> TipoActividad.SALIDA
        "REJECTION" -> TipoActividad.RECHAZO
        else -> TipoActividad.PASE_CREADO
    }
)

private fun initialsOf(name: String): String {
    return name.split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "?" }
}

private fun statusColors(status: String): Triple<String, Color, Color> {
    return when (status) {
        "INSIDE" -> Triple("Dentro", StatusInsideContainer, StatusInside)
        "LEFT" -> Triple("Salió", Color(0xFFEFEFEF), Color(0xFF6B7280))
        else -> Triple("Esperado", StatusExpectedContainer, StatusExpected)
    }
}

fun normalizeStatus(raw: String?): String {
    return when (raw?.trim()?.uppercase()) {
        "INSIDE", "DENTRO" -> "INSIDE"
        "LEFT", "SALIO", "SALIÓ" -> "LEFT"
        "PENDING", "PENDIENTE" -> "PENDING"
        "REJECTED", "RECHAZADO" -> "REJECTED"
        "EXPECTED", "ESPERADO", "APPROVED", "APROBADO" -> "EXPECTED"
        "EXPIRED", "VENCIDO", "EXPIRADO" -> "EXPIRED"
        else -> "UNKNOWN"
    }
}

fun normalizeMovementType(raw: String?): String {
    return when (raw?.trim()?.uppercase()) {
        "ENTRY", "ENTRADA" -> "ENTRY"
        "EXIT", "SALIDA" -> "EXIT"
        "REJECTION", "RECHAZO" -> "REJECTION"
        "PASS_CREATED", "PASE_CREADO" -> "PASS_CREATED"
        else -> "PASS_CREATED"
    }
}
