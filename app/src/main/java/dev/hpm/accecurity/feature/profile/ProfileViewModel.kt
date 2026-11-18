package dev.hpm.accecurity.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.hpm.accecurity.core.auth.AuthRepository
import dev.hpm.accecurity.core.auth.resolveUserDisplay
import dev.hpm.accecurity.core.data.AppGraph
import dev.hpm.accecurity.core.data.avisosDeGarita
import dev.hpm.accecurity.core.data.avisosDeMisPases
import dev.hpm.accecurity.core.data.avisosDeSolicitudes
import dev.hpm.accecurity.core.data.obtenerAyudaPerfil
import dev.hpm.accecurity.core.model.TipoUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private var usuarioJob: Job? = null
    private var rolObservado: TipoUsuario? = null

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.ConfigurarPerfil -> {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val resolved = resolveUserDisplay(
                    displayName = firebaseUser?.displayName,
                    email = firebaseUser?.email
                )
                val nombre = event.nombre.ifBlank { resolved.fullName }
                val iniciales = event.iniciales.ifBlank {
                    resolveUserDisplay(displayName = nombre, email = firebaseUser?.email).initials
                }
                _uiState.update {
                    it.copy(
                        nombre = nombre,
                        iniciales = iniciales,
                        tipoUsuario = event.tipoUsuario,
                        numeroSolicitudesJefe = event.numeroSolicitudesJefe
                    )
                }
                if (event.nombre.isBlank() || event.iniciales.isBlank()) {
                    observarUsuario()
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

    private fun observarUsuario() {
        if (usuarioJob?.isActive == true) return
        usuarioJob = viewModelScope.launch {
            AppGraph.repository.observeCurrentUser().collect { userEntity ->
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val resolved = resolveUserDisplay(
                    displayName = firebaseUser?.displayName,
                    email = userEntity?.email?.takeIf { it.isNotBlank() } ?: firebaseUser?.email
                )
                _uiState.update {
                    it.copy(
                        nombre = resolved.fullName,
                        iniciales = resolved.initials
                    )
                }
            }
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
     * Cierra la sesión, limpia la caché local de Room y expone el resultado para navegar.
     */
    private fun cerrarSesion() {
        if (_uiState.value.sesionCerrada) return

        viewModelScope.launch {
            try {
                notificacionesJob?.cancel()
                usuarioJob?.cancel()
                authRepository.signOut()
                AppGraph.repository.clearLocalCache()

                _uiState.update {
                    it.copy(
                        sesionCerrada = true,
                        errorMessage = null
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "No se pudo cerrar sesión. Intenta de nuevo."
                    )
                }
            }
        }
    }
}