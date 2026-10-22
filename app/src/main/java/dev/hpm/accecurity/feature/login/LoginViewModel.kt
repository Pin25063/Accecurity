package dev.hpm.accecurity.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.hpm.accecurity.core.auth.resolveLoginRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel de login con Firebase Authentication (correo y contraseña).
 */
class LoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.value, errorMessage = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.value, errorMessage = null) }
            }
            LoginEvent.SignInClicked -> signIn()
            LoginEvent.NavigationHandled -> {
                _uiState.update { it.copy(loggedInRole = null) }
            }
        }
    }

    private fun signIn() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa correo y contraseña.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                val role = resolveLoginRole(email)
                _uiState.update {
                    it.copy(isLoading = false, loggedInRole = role)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = mapAuthError(e)
                    )
                }
            }
        }
    }

    private fun mapAuthError(error: Exception): String {
        val message = error.message.orEmpty().lowercase()
        return when {
            "password" in message || "invalid" in message ->
                "Correo o contraseña incorrectos."
            "network" in message ->
                "Sin conexión. Revisa tu internet."
            "user-not-found" in message ->
                "No existe una cuenta con ese correo."
            else -> "No se pudo iniciar sesión. Intenta de nuevo."
        }
    }
}
