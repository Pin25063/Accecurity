package dev.hpm.accecurity.feature.registro

import androidx.lifecycle.ViewModel
import dev.hpm.accecurity.core.data.obtenerRegistroGaritaFalso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos del Registro de garita bajo arquitectura MVI.
 */
class RegistroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegistroState())
    val uiState: StateFlow<RegistroState> = _uiState.asStateFlow()

    init {
        cargarRegistro()
    }

    fun onEvent(event: RegistroEvent) {
        when (event) {
            RegistroEvent.ActualizarRegistro -> cargarRegistro()
        }
    }

    /**
     * Carga los movimientos de prueba del registro de garita.
     */
    private fun cargarRegistro() {
        val movimientos = obtenerRegistroGaritaFalso()

        _uiState.update {
            it.copy(
                movimientos = movimientos,
                isLoading = false
            )
        }
    }
}