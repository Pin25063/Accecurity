package dev.hpm.accecurity.feature.pases

import dev.hpm.accecurity.core.model.PaseData

/**
 * Estado inmutable de la funcionalidad de Pases.
 */
data class PasesState(
    val pases: List<PaseData> = emptyList(),
    val pasesFiltrados: List<PaseData> = emptyList(),
    val filtroSeleccionado: String = "Todos",
    val totalEsperados: Int = 0,
    val totalAdentro: Int = 0,
    val totalSalieron: Int = 0,
    val mostrarFormulario: Boolean = false,
    val nombreVisitante: String = "",
    val descripcionVisita: String = "",
    val horaVisita: String = "",
    val errorFormulario: String? = null,
    val isLoading: Boolean = false,
    val paseSeleccionado: PaseData? = null
)