package dev.hpm.accecurity.feature.pases

import dev.hpm.accecurity.core.data.obtenerPasesFalsos
import dev.hpm.accecurity.core.model.PaseData

/**
 * Estado inmutable de la funcionalidad de Pases.
 */
data class PasesState(
    val pases: List<PaseData> = obtenerPasesFalsos(),
    val filtroSeleccionado: String = "Todos",
    val isLoading: Boolean = false
)
