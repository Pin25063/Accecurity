package dev.hpm.accecurity.feature.escaner

/**
 * Eventos e intenciones del usuario en el Escáner de garita bajo arquitectura MVI.
 */
sealed interface EscanerEvent {
    data class CodigoEscaneado(val codigo: String) : EscanerEvent
    data object ReiniciarEscaner : EscanerEvent
    data class ErrorOcurrido(val mensaje: String) : EscanerEvent
    data object LimpiarMensajes : EscanerEvent
    data class PermisoCamaraActualizado(val concedido: Boolean) : EscanerEvent
}
