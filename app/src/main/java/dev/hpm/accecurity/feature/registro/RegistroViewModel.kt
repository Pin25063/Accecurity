package dev.hpm.accecurity.feature.registro

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
 * ViewModel para gestionar el estado y eventos del Registro de garita bajo arquitectura MVI.
 */
class RegistroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegistroState())
    val uiState: StateFlow<RegistroState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AppGraph.repository.observeMovements().collect { movimientos ->
                _uiState.update {
                    it.copy(movimientos = movimientos, isLoading = false)
                }
            }
        }
    }

    fun onEvent(event: RegistroEvent) {
        when (event) {
            RegistroEvent.ActualizarRegistro -> {
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