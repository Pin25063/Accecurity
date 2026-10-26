package dev.hpm.accecurity.feature.activity

/**
 * Eventos e intenciones del usuario en la pantalla de Actividad.
 */
sealed interface ActivityEvent {
    data object ActualizarActividad : ActivityEvent
}
