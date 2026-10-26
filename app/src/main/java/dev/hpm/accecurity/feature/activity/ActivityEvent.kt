package dev.hpm.accecurity.feature.activity

import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Eventos e intenciones del usuario en la pantalla de Actividad.
 */
sealed interface ActivityEvent {
    data class ConfigurarPantalla(
        val tipoUsuario: TipoUsuario,
        val numeroSolicitudesJefe: Int
    ) : ActivityEvent

    data object ActualizarActividad : ActivityEvent
}