package dev.hpm.accecurity.feature.activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para gestionar el estado y eventos de Actividad bajo arquitectura MVI.
 */
class ActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityState())
    val uiState: StateFlow<ActivityState> = _uiState.asStateFlow()

    fun onEvent(event: ActivityEvent) {
        when (event) {
            is ActivityEvent.ActualizarActividad -> {
                // Actualizar o recargar eventos de actividad
            }
        }
    }
}
