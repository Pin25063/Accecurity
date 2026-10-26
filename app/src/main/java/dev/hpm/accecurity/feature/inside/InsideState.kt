package dev.hpm.accecurity.feature.inside

/**
 * Estado inmutable de la pantalla de personas Adentro.
 */
data class InsideState(
    val totalAdentro: Int = 5,
    val numeroSolicitudesJefe: Int = 0,
    val isLoading: Boolean = false
)
