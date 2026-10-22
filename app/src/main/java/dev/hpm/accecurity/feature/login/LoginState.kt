package dev.hpm.accecurity.feature.login

import dev.hpm.accecurity.core.auth.AuthenticatedSession

/**
 * Estado inmutable de la pantalla de login.
 *
 * @property session Sesión válida tras un login correcto. La navegación
 * puede observarla; esta pantalla no cambia de ruta por su cuenta.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val session: AuthenticatedSession? = null
)
