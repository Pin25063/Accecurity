package dev.hpm.accecurity.feature.registro

import dev.hpm.accecurity.core.model.ActividadData

/**
 * Estado inmutable de la pantalla de Registro de garita.
 */
data class RegistroState(
    val movimientos: List<ActividadData> = emptyList(),
    val isLoading: Boolean = false
)