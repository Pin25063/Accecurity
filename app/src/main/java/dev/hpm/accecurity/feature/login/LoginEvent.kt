package dev.hpm.accecurity.feature.login

/**
 * Acciones del usuario en la pantalla de login.
 */
sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object SignInClicked : LoginEvent
    data object NavigationHandled : LoginEvent
}
