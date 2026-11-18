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

/**
 * Nombre completo, primer nombre e iniciales listas para mostrar en UI.
 */
data class UserDisplayInfo(
    val fullName: String,
    val firstName: String,
    val initials: String
)

/**
 * Obtiene el nombre e iniciales a partir de `displayName` o, si no existe, del correo.
 */
fun resolveUserDisplay(
    displayName: String?,
    email: String?,
    fallbackName: String = "Usuario"
): UserDisplayInfo {
    val cleanDisplayName = displayName?.trim().orEmpty()
    val fullName = if (cleanDisplayName.isNotBlank()) {
        cleanDisplayName
    } else {
        deriveNameFromEmail(email).ifBlank { fallbackName }
    }
    val words = fullName.split(Regex("\\s+")).filter { it.isNotBlank() }
    val firstName = words.firstOrNull().orEmpty().ifBlank { fallbackName }
    val initials = when {
        words.size >= 2 -> "${words[0].first()}${words[1].first()}".uppercase()
        words.size == 1 -> words[0].take(2).uppercase()
        else -> "U"
    }
    return UserDisplayInfo(
        fullName = fullName,
        firstName = firstName,
        initials = initials
    )
}

private fun deriveNameFromEmail(email: String?): String {
    val localPart = email?.trim()?.substringBefore('@').orEmpty()
    if (localPart.isBlank()) return ""
    return localPart
        .split('.', '_', '-', ' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase() else ch.toString()
            }
        }
}
