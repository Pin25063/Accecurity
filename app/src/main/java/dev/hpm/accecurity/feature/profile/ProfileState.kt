package dev.hpm.accecurity.feature.profile

import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Opciones disponibles en el menú de Perfil.
 */
enum class OpcionPerfil {
    NOTIFICACIONES,
    PREFERENCIAS,
    AYUDA
}

/**
 * Estado inmutable de la pantalla de Perfil.
 */
data class ProfileState(
    val nombre: String = "Carlos Mendoza",
    val iniciales: String = "CM",
    val tipoUsuario: TipoUsuario = TipoUsuario.ADMIN,
    val numeroSolicitudesJefe: Int = 0,
    val opcionSeleccionada: OpcionPerfil? = null,
    val notificaciones: List<String> = emptyList(),
    val mostrarNotificaciones: Boolean = true,
    val ayuda: String = "",
    val sesionCerrada: Boolean = false,
    val errorMessage: String? = null
)