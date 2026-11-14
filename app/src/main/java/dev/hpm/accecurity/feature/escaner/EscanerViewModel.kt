package dev.hpm.accecurity.feature.escaner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos del Escáner bajo arquitectura MVI.
 */
class EscanerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EscanerState())
    val uiState: StateFlow<EscanerState> = _uiState.asStateFlow()

    fun onEvent(event: EscanerEvent) {
        when (event) {
            is EscanerEvent.CodigoEscaneado -> {
                _uiState.update {
                    it.copy(
                        ultimoCodigoEscaneado = event.codigo,
                        estaEscaneando = false,
                        mensajeError = null,
                        mensajeExito = "Código QR detectado: ${event.codigo}"
                    )
                }
            }
            is EscanerEvent.ReiniciarEscaner -> {
                _uiState.update {
                    it.copy(
                        ultimoCodigoEscaneado = null,
                        estaEscaneando = true,
                        mensajeError = null,
                        mensajeExito = null
                    )
                }
            }
            is EscanerEvent.ErrorOcurrido -> {
                _uiState.update {
                    it.copy(
                        mensajeError = event.mensaje,
                        mensajeExito = null
                    )
                }
            }
            is EscanerEvent.LimpiarMensajes -> {
                _uiState.update {
                    it.copy(
                        mensajeError = null,
                        mensajeExito = null
                    )
                }
            }
            is EscanerEvent.PermisoCamaraActualizado -> {
                _uiState.update {
                    it.copy(
                        tienePermisoCamara = event.concedido,
                        mensajeError = if (!event.concedido) {
                            "Se requiere permiso de cámara para escanear códigos QR."
                        } else null
                    )
                }
            }
        }
    }
}
