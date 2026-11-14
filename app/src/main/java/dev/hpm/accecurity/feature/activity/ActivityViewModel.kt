package dev.hpm.accecurity.feature.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hpm.accecurity.core.data.AppGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado y eventos de Actividad bajo arquitectura MVI.
 */
class ActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityState())
    val uiState: StateFlow<ActivityState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AppGraph.repository.observeMovements().collect { actividades ->
                _uiState.update {
                    it.copy(actividades = actividades, isLoading = false)
                }
            }
        }
    }

    fun onEvent(event: ActivityEvent) {
        when (event) {
            is ActivityEvent.ConfigurarPantalla -> {
                _uiState.update {
                    it.copy(
                        tipoUsuario = event.tipoUsuario,
                        numeroSolicitudesJefe = event.numeroSolicitudesJefe
                    )
                }
            }

            ActivityEvent.ActualizarActividad -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        AppGraph.repository.refresh()
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }
}