package dev.hpm.accecurity.feature.solicitudes

import androidx.lifecycle.ViewModel
import dev.hpm.accecurity.core.data.obtenerSolicitudesFalsas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos de Solicitudes bajo arquitectura MVI.
 */
class SolicitudesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SolicitudesState())
    val uiState: StateFlow<SolicitudesState> = _uiState.asStateFlow()

    init {
        cargarSolicitudes()
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
     * Carga las solicitudes de prueba y calcula la cantidad de pendientes.
     */
    private fun cargarSolicitudes() {
        val solicitudes = obtenerSolicitudesFalsas()
            .sortedByDescending { it.esUrgente }

        _uiState.update { state ->
            state.copy(
                solicitudes = solicitudes,
                pendientes = solicitudes.size,
                isLoading = false
            )
        }
    }

    /**
     * Registra la decisión y actualiza el listado de solicitudes pendientes.
     */
    private fun procesarSolicitud(id: String, aprobada: Boolean) {
        _uiState.update { state ->
            val solicitud = state.solicitudes.find { it.id == id }
                ?: return@update state

            val pendientes = state.solicitudes.filterNot { it.id == id }

            state.copy(
                solicitudes = pendientes,
                solicitudesAprobadas = if (aprobada) {
                    state.solicitudesAprobadas + solicitud
                } else {
                    state.solicitudesAprobadas
                },
                solicitudesRechazadas = if (aprobada) {
                    state.solicitudesRechazadas
                } else {
                    state.solicitudesRechazadas + solicitud
                },
                pendientes = pendientes.size,
                mensaje = if (aprobada) {
                    "Solicitud de ${solicitud.nombre} aprobada."
                } else {
                    "Solicitud de ${solicitud.nombre} rechazada."
                }
            )
        }
    }
}