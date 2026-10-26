package dev.hpm.accecurity.feature.activity

import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Estado inmutable de la pantalla de Actividad.
 */
data class ActivityState(
    val actividades: List<ActividadData> = emptyList(),
    val tipoUsuario: TipoUsuario = TipoUsuario.ADMIN,
    val numeroSolicitudesJefe: Int = 0,
    val isLoading: Boolean = false
)