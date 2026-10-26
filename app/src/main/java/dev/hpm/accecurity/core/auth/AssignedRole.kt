package dev.hpm.accecurity.core.auth

import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Convierte el valor de Firestore (`rol`) al rol de la app.
 * Valores válidos: admin, residente, jefe, guardia.
 */
fun parseAssignedRole(raw: String?): TipoUsuario? {
    return when (raw?.lowercase()?.trim()) {
        "admin" -> TipoUsuario.ADMIN
        "residente" -> TipoUsuario.RESIDENTE
        "jefe" -> TipoUsuario.JEFE
        "guardia" -> TipoUsuario.GUARDIA
        else -> null
    }
}

fun TipoUsuario.displayName(): String = when (this) {
    TipoUsuario.ADMIN -> "Administrador"
    TipoUsuario.RESIDENTE -> "Residente"
    TipoUsuario.JEFE -> "Jefe de seguridad"
    TipoUsuario.GUARDIA -> "Guardia de seguridad"
}
