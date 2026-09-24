package dev.hpm.accecurity.model

/**
 * Roles de usuario soportados en la aplicación.
 * Cada rol determina la navegación inferior y las pantallas disponibles.
 */
enum class TipoUsuario {
    ADMIN,
    RESIDENTE,
    GUARDIA,
    JEFE
}
