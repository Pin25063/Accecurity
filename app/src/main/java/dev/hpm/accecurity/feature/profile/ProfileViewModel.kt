package dev.hpm.accecurity.feature.profile

import androidx.lifecycle.viewModelScope
import dev.hpm.accecurity.core.data.AppGraph
import dev.hpm.accecurity.core.data.avisosDeMisPases
import dev.hpm.accecurity.core.data.avisosDeSolicitudes
import dev.hpm.accecurity.core.data.avisosDeGarita
import dev.hpm.accecurity.core.model.TipoUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import dev.hpm.accecurity.core.auth.AuthRepository
import dev.hpm.accecurity.core.data.obtenerAyudaPerfil
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
            ayuda = obtenerAyudaPerfil()
        )
    )
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private var notificacionesJob: Job? = null
    private var rolObservado: TipoUsuario? = null

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
                observarNotificaciones(event.tipoUsuario)
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

    private fun observarNotificaciones(rol: TipoUsuario) {
        if (rolObservado == rol && notificacionesJob?.isActive == true) return
        rolObservado = rol
        notificacionesJob?.cancel()
        _uiState.update {
            it.copy(notificaciones = emptyList(), cargandoNotificaciones = true, errorNotificaciones = null)
        }
        val avisos = when (rol) {
            TipoUsuario.ADMIN, TipoUsuario.RESIDENTE -> AppGraph.repository.observeMyPasses().map(::avisosDeMisPases)
            TipoUsuario.JEFE -> AppGraph.repository.observePending().map(::avisosDeSolicitudes)
            TipoUsuario.GUARDIA -> AppGraph.repository.observeMovements().map(::avisosDeGarita)
        }
        notificacionesJob = viewModelScope.launch {
            avisos.catch { error ->
                if (error is CancellationException) throw error
                _uiState.update {
                    it.copy(cargandoNotificaciones = false, errorNotificaciones = "No se pudieron cargar las notificaciones.")
                }
            }.collect { notificaciones ->
                _uiState.update {
                    it.copy(notificaciones = notificaciones, cargandoNotificaciones = false, errorNotificaciones = null)
                }
            }
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