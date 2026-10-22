package dev.hpm.accecurity.feature.solicitudes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para gestionar el estado y eventos de Solicitudes bajo arquitectura MVI.
 */
class SolicitudesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SolicitudesState())
    val uiState: StateFlow<SolicitudesState> = _uiState.asStateFlow()

    fun onEvent(event: SolicitudesEvent) {
        when (event) {
            is SolicitudesEvent.AprobarSolicitud -> {
                // Lógica de aprobación de solicitud
            }
            is SolicitudesEvent.RechazarSolicitud -> {
                // Lógica de rechazo de solicitud
            }
        }
    }
}
