package dev.hpm.accecurity.feature.pases

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos de Pases bajo arquitectura MVI.
 */
class PasesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasesState())
    val uiState: StateFlow<PasesState> = _uiState.asStateFlow()

    fun onEvent(event: PasesEvent) {
        when (event) {
            is PasesEvent.CambiarFiltro -> {
                _uiState.update { it.copy(filtroSeleccionado = event.filtro) }
            }
            is PasesEvent.CrearPase -> {
                // Acción de crear pase
            }
        }
    }
}
