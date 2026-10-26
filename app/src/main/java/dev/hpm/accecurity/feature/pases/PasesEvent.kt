package dev.hpm.accecurity.feature.pases

/**
 * Eventos e intenciones del usuario para la funcionalidad de Pases.
 */
sealed interface PasesEvent {
    data class CambiarFiltro(val filtro: String) : PasesEvent
    data object CrearPase : PasesEvent
}
