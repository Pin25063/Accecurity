package dev.hpm.accecurity.feature.solicitudes

/**
 * Eventos e intenciones del usuario en la pantalla de Solicitudes.
 */
sealed interface SolicitudesEvent {
    data class AprobarSolicitud(val nombre: String) : SolicitudesEvent
    data class RechazarSolicitud(val nombre: String) : SolicitudesEvent
}
