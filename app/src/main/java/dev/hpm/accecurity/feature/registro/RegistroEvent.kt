package dev.hpm.accecurity.feature.registro

/**
 * Eventos e intenciones del usuario en la pantalla de Registro de garita.
 */
sealed interface RegistroEvent {
    data object ActualizarRegistro : RegistroEvent
}