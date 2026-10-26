package dev.hpm.accecurity.feature.solicitudes

import dev.hpm.accecurity.core.model.SolicitudData

/**
 * Estado inmutable de la pantalla de Solicitudes del Jefe de seguridad.
 */
data class SolicitudesState(
    val solicitudes: List<SolicitudData> = emptyList(),
    val solicitudesAprobadas: List<SolicitudData> = emptyList(),
    val solicitudesRechazadas: List<SolicitudData> = emptyList(),
    val pendientes: Int = 0,
    val mensaje: String? = null,
    val isLoading: Boolean = false
)