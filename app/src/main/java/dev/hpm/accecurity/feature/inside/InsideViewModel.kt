package dev.hpm.accecurity.feature.inside

import androidx.lifecycle.ViewModel
import dev.hpm.accecurity.core.data.obtenerPersonasAdentroFalsas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos de personas Adentro bajo arquitectura MVI.
 */
class InsideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InsideState())
    val uiState: StateFlow<InsideState> = _uiState.asStateFlow()

    init {
        cargarPersonasAdentro()
    }

    fun onEvent(event: InsideEvent) {
        when (event) {
            is InsideEvent.ConfigurarPantalla -> {
                _uiState.update {
                    it.copy(
                        numeroSolicitudesJefe = event.numeroSolicitudesJefe
                    )
                }
            }

            InsideEvent.RefrescarPersonasAdentro -> cargarPersonasAdentro()
        }
    }

    /**
     * Carga los visitantes de prueba y calcula el total de personas adentro.
     */
    private fun cargarPersonasAdentro() {
        val personas = obtenerPersonasAdentroFalsas()

        _uiState.update {
            it.copy(
                personas = personas,
                totalAdentro = personas.size,
                isLoading = false
            )
        }
    }
}