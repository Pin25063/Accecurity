package dev.hpm.accecurity.feature.pases
import dev.hpm.accecurity.core.model.PaseData

/**
 * Eventos e intenciones del usuario para la funcionalidad de Pases.
 */
sealed interface PasesEvent {
    data class CambiarFiltro(val filtro: String) : PasesEvent
    data object CrearPase : PasesEvent
    data object CancelarCreacion : PasesEvent
    data class CambiarNombre(val nombre: String) : PasesEvent
    data class CambiarDescripcion(val descripcion: String) : PasesEvent
    data class CambiarHora(val hora: String) : PasesEvent
    data object GuardarPase : PasesEvent
    data class VerDetalle(val pase: PaseData) : PasesEvent
    data object CerrarDetalle : PasesEvent
}