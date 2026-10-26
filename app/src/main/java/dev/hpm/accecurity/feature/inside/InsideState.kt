package dev.hpm.accecurity.feature.inside

import dev.hpm.accecurity.core.model.PersonaAdentroData

/**
 * Estado inmutable de la pantalla de personas Adentro.
 */
data class InsideState(
    val personas: List<PersonaAdentroData> = emptyList(),
    val totalAdentro: Int = 0,
    val numeroSolicitudesJefe: Int = 0,
    val isLoading: Boolean = false
)