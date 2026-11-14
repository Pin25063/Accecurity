package dev.hpm.accecurity.feature.solicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hpm.accecurity.core.data.AppGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                        isLoading = false
                    )
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
        val solicitud = _uiState.value.solicitudes.find { it.id == id } ?: return
        viewModelScope.launch {
            val saved = AppGraph.repository.updatePassStatus(
                passId = id,
                status = if (aprobada) "EXPECTED" else "REJECTED"
            )
            _uiState.update {
                it.copy(
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