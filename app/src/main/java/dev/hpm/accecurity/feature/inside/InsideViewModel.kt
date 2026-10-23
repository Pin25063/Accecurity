package dev.hpm.accecurity.feature.inside

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para gestionar el estado y eventos de personas Adentro bajo arquitectura MVI.
 */
class InsideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InsideState())
    val uiState: StateFlow<InsideState> = _uiState.asStateFlow()

    fun onEvent(event: InsideEvent) {
        when (event) {
            is InsideEvent.RefrescarPersonasAdentro -> {
                // Refrescar lista
            }
        }
    }
}
