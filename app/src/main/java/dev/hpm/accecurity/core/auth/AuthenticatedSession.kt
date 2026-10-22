package dev.hpm.accecurity.core.auth

import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Sesión lista para que la navegación decida a qué grafo entrar.
 * El login solo produce este objeto; no navega.
 */
data class AuthenticatedSession(
    val uid: String,
    val email: String,
    val tipoUsuario: TipoUsuario
)
