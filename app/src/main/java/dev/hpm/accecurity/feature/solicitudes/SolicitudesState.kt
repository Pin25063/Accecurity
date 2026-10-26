package dev.hpm.accecurity.feature.solicitudes

import dev.hpm.accecurity.core.data.contarSolicitudesPendientes
import dev.hpm.accecurity.core.data.obtenerSolicitudesFalsas
import dev.hpm.accecurity.core.model.SolicitudData

/**
 * Estado inmutable de la pantalla de Solicitudes del Jefe de seguridad.
 */
data class SolicitudesState(
    val solicitudes: List<SolicitudData> = obtenerSolicitudesFalsas(),
    val pendientes: Int = contarSolicitudesPendientes(),
    val isLoading: Boolean = false
)
