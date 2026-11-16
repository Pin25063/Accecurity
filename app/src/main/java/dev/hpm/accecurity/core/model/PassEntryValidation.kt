package dev.hpm.accecurity.core.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Datos que verá el guardia antes de confirmar una entrada. */
data class ScannedPass(
    val id: String,
    val visitorName: String,
    val description: String,
    val location: String,
    val requestedBy: String,
    val visitDate: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val qrCode: String,
    val entryTime: String
)

sealed interface PassLookupResult {
    data class Valid(val pass: ScannedPass) : PassLookupResult
    data class Invalid(val message: String) : PassLookupResult
}

sealed interface RegisterEntryResult {
    data class Success(val visitorName: String, val time: String) : RegisterEntryResult
    data class Failure(val message: String) : RegisterEntryResult
}

/** Inicio inclusivo y fin exclusivo. No autoriza pases antiguos sin fecha y rango válidos. */
fun validatePassEntry(
    pass: ScannedPass,
    scannedCode: String,
    now: LocalDateTime = LocalDateTime.now()
): String? {
    if (scannedCode.isBlank() || scannedCode != pass.qrCode.ifBlank { pass.id }) {
        return "El código QR no corresponde a este pase."
    }
    when (pass.status) {
        "PENDING" -> return "El pase todavía está pendiente de aprobación."
        "REJECTED" -> return "La solicitud fue rechazada. No se permite el ingreso."
        "INSIDE" -> return "Este pase ya se utilizó. El visitante está adentro."
        "LEFT" -> return "Este pase ya se utilizó y el visitante registró su salida."
        "EXPIRED" -> return "El pase está vencido."
        "EXPECTED" -> Unit
        else -> return "El pase no tiene un estado autorizado para ingresar."
    }
    if (pass.entryTime.isNotBlank()) return "Este pase ya tiene una entrada registrada."
    if (pass.visitorName.isBlank()) return "El pase no tiene los datos del visitante."

    val date = runCatching { LocalDate.parse(pass.visitDate) }.getOrNull()
        ?: return "El pase no tiene fecha válida. Solicitá uno nuevo."
    val timePattern = Regex("([01][0-9]|2[0-3]):[0-5][0-9]")
    if (!timePattern.matches(pass.startTime) || !timePattern.matches(pass.endTime)) {
        return "El pase no tiene un horario válido. Solicitá uno nuevo."
    }
    val start = LocalTime.parse(pass.startTime)
    val end = LocalTime.parse(pass.endTime)
    if (!end.isAfter(start)) return "El horario del pase no es válido."
    if (now.isBefore(date.atTime(start))) return "Todavía no comienza el horario autorizado."
    if (!now.isBefore(date.atTime(end))) return "El pase está vencido: terminó el horario autorizado."
    return null
}
