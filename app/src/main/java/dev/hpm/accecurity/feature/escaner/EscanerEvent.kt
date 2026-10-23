package dev.hpm.accecurity.feature.escaner

/**
 * Eventos e intenciones del usuario en el Escáner de garita.
 */
sealed interface EscanerEvent {
    data class CodigoEscaneado(val codigo: String) : EscanerEvent
    data object ReiniciarEscaner : EscanerEvent
}
