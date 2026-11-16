package dev.hpm.accecurity.core.model

sealed interface RegisterExitResult {
    data class Success(val visitorName: String, val time: String) : RegisterExitResult
    data class Failure(val message: String) : RegisterExitResult
}

/** La ventana del pase limita el ingreso; no impide salir después de ese horario. */
fun validatePassExit(status: String, entryTime: String, exitTime: String): String? {
    if (status == "LEFT" || exitTime.isNotBlank()) return "Este visitante ya tiene una salida registrada."
    if (status != "INSIDE") return "Solo se puede registrar la salida de visitantes que están adentro."
    if (entryTime.isBlank()) return "El pase no tiene una entrada registrada. Revisá sus datos."
    return null
}
