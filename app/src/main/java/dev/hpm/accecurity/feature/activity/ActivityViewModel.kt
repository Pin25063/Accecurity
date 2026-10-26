package dev.hpm.accecurity.feature.activity

import androidx.lifecycle.ViewModel
import dev.hpm.accecurity.core.data.obtenerActividadFalsa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos de Actividad bajo arquitectura MVI.
 */
class ActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityState())
    val uiState: StateFlow<ActivityState> = _uiState.asStateFlow()

    init {
        cargarActividad()
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

            ActivityEvent.ActualizarActividad -> cargarActividad()
        }
    }

    /**
     * Carga los movimientos de prueba del registro de actividad.
     */
    private fun cargarActividad() {
        val actividades = obtenerActividadFalsa()

        _uiState.update {
            it.copy(
                actividades = actividades,
                isLoading = false
            )
        }
    }
}