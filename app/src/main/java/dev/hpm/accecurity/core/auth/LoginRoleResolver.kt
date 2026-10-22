package dev.hpm.accecurity.core.auth

/**
 * Asigna el rol de navegación según el correo del usuario autenticado.
 * Firebase Auth no incluye roles; ajusta las reglas según tus usuarios de prueba.
 */
fun resolveLoginRole(email: String): String {
    val normalized = email.lowercase().trim()
    return when {
        "jefe" in normalized -> "jefe"
        "guardia" in normalized -> "guardia"
        else -> "admin"
    }
}
