package dev.hpm.accecurity.feature.solicitudes

/**
 * Eventos e intenciones del usuario en la pantalla de Solicitudes.
 */
sealed interface SolicitudesEvent {
    data class AprobarSolicitud(val id: String) : SolicitudesEvent
    data class RechazarSolicitud(val id: String) : SolicitudesEvent
}