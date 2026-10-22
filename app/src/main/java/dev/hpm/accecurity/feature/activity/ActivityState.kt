package dev.hpm.accecurity.feature.activity

import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Estado inmutable de la pantalla de Actividad.
 */
data class ActivityState(
    val tipoUsuario: TipoUsuario = TipoUsuario.ADMIN,
    val numeroSolicitudesJefe: Int = 0,
    val isLoading: Boolean = false
)
