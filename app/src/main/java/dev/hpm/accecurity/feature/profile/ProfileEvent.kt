package dev.hpm.accecurity.feature.profile

/**
 * Eventos e intenciones del usuario en la pantalla de Perfil.
 */
sealed interface ProfileEvent {
    data object CerrarSesion : ProfileEvent
}
