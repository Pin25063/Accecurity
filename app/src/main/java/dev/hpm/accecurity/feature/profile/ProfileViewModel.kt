package dev.hpm.accecurity.feature.profile

import androidx.lifecycle.ViewModel
import dev.hpm.accecurity.core.auth.AuthRepository
import dev.hpm.accecurity.core.data.obtenerAyudaPerfil
import dev.hpm.accecurity.core.data.obtenerNotificacionesFalsas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos de Perfil bajo arquitectura MVI.
 */
class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ProfileState(
            notificaciones = obtenerNotificacionesFalsas(),
            ayuda = obtenerAyudaPerfil()
        )
    )
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.ConfigurarPerfil -> {
                _uiState.update {
                    it.copy(
                        nombre = event.nombre,
                        iniciales = event.iniciales,
                        tipoUsuario = event.tipoUsuario,
                        numeroSolicitudesJefe = event.numeroSolicitudesJefe
                    )
                }
            }

            is ProfileEvent.SeleccionarOpcion -> {
                _uiState.update {
                    it.copy(opcionSeleccionada = event.opcion)
                }
            }

            is ProfileEvent.CambiarNotificaciones -> {
                _uiState.update {
                    it.copy(mostrarNotificaciones = event.mostrar)
                }
            }

            ProfileEvent.CerrarOpcion -> {
                _uiState.update {
                    it.copy(opcionSeleccionada = null)
                }
            }

            ProfileEvent.CerrarSesion -> cerrarSesion()
        }
    }

    /**
     * Cierra la sesión y expone el resultado para que la pantalla solicite navegar.
     */
    private fun cerrarSesion() {
        if (_uiState.value.sesionCerrada) return

        try {
            authRepository.signOut()

            _uiState.update {
                it.copy(
                    sesionCerrada = true,
                    errorMessage = null
                )
            }
        } catch (error: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = "No se pudo cerrar sesión. Intenta de nuevo."
                )
            }
        }
    }
}