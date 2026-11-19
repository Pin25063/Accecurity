package dev.hpm.accecurity.feature.escaner

import dev.hpm.accecurity.core.model.PersonaAdentroData

/**
 * Eventos e intenciones del usuario en el Escáner de garita bajo arquitectura MVI.
 */
sealed interface EscanerEvent {
    data class CodigoEscaneado(val codigo: String) : EscanerEvent
    data object SincronizarPendientes : EscanerEvent
    data object ReintentarConflictos : EscanerEvent
    data object AbrirRegistroSalida : EscanerEvent
    data object ActualizarListaSalida : EscanerEvent
    data class CambiarBusquedaSalida(val texto: String) : EscanerEvent
    data class SeleccionarPersonaSalida(val persona: PersonaAdentroData) : EscanerEvent
    data object VolverListaSalida : EscanerEvent
    data object ConfirmarSalida : EscanerEvent
    data object CerrarRegistroSalida : EscanerEvent
    data object ConfirmarEntrada : EscanerEvent
    data object CerrarResultado : EscanerEvent
    data object ReiniciarEscaner : EscanerEvent
    data class ErrorOcurrido(val mensaje: String) : EscanerEvent
    data object LimpiarMensajes : EscanerEvent
    data class PermisoCamaraActualizado(val concedido: Boolean) : EscanerEvent
}
