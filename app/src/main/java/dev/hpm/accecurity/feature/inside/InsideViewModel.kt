package dev.hpm.accecurity.feature.inside

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
 * ViewModel para gestionar el estado y eventos de personas Adentro bajo arquitectura MVI.
 */
class InsideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InsideState())
    val uiState: StateFlow<InsideState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AppGraph.repository.observeInside().collect { personas ->
                _uiState.update {
                    it.copy(
                        personas = personas,
                        totalAdentro = personas.size,
                        isLoading = false
                    )
                }
            }
        }
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

            InsideEvent.RefrescarPersonasAdentro -> {
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