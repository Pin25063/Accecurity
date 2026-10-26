package dev.hpm.accecurity.feature.profile

import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Eventos e intenciones del usuario en la pantalla de Perfil.
 */
sealed interface ProfileEvent {
    data class ConfigurarPerfil(
        val nombre: String,
        val iniciales: String,
        val tipoUsuario: TipoUsuario,
        val numeroSolicitudesJefe: Int
    ) : ProfileEvent

    data class SeleccionarOpcion(
        val opcion: OpcionPerfil
    ) : ProfileEvent

    data class CambiarNotificaciones(
        val mostrar: Boolean
    ) : ProfileEvent

    data object CerrarOpcion : ProfileEvent
    data object CerrarSesion : ProfileEvent
}