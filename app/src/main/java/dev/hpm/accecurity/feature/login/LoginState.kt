package dev.hpm.accecurity.feature.login

/**
 * Estado inmutable de la pantalla de login.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInRole: String? = null
)
