package dev.hpm.accecurity.feature.solicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hpm.accecurity.core.data.AppGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para gestionar el estado y eventos de Solicitudes bajo arquitectura MVI.
 */
class SolicitudesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SolicitudesState())
    val uiState: StateFlow<SolicitudesState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AppGraph.repository.observePending().collect { solicitudes ->
                val ordered = solicitudes.sortedByDescending { it.esUrgente }
                _uiState.update {
                    it.copy(
                        solicitudes = ordered,
                        pendientes = ordered.size,
                        isLoading = if (ordered.isNotEmpty()) false else it.isLoading
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppGraph.repository.refresh()
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mensaje = "No se pudieron cargar las solicitudes."
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: SolicitudesEvent) {
        when (event) {
            is SolicitudesEvent.AprobarSolicitud -> {
                procesarSolicitud(id = event.id, aprobada = true)
            }

            is SolicitudesEvent.RechazarSolicitud -> {
                procesarSolicitud(id = event.id, aprobada = false)
            }
        }
    }

    /**
     * Cambia el estado del pase en Firestore. La lista se actualiza desde Room.
     */
    private fun procesarSolicitud(id: String, aprobada: Boolean) {
        if (_uiState.value.procesando) return
        val solicitud = _uiState.value.solicitudes.find { it.id == id } ?: return
        _uiState.update {
            it.copy(
                procesando = true,
                mensaje = if (aprobada) "Autorizando..." else "Rechazando..."
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            val saved = AppGraph.repository.updatePassStatus(
                passId = id,
                status = if (aprobada) "EXPECTED" else "REJECTED"
            )
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        procesando = false,
                        mensaje = if (!saved) {
                            "No se pudo actualizar la solicitud."
                        } else if (aprobada) {
                            "Solicitud de ${solicitud.nombre} aprobada."
                        } else {
                            "Solicitud de ${solicitud.nombre} rechazada."
                        }
                    )
                }
            }
        }
    }
}