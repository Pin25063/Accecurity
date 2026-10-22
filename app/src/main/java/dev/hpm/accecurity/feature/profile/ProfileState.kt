package dev.hpm.accecurity.feature.profile

import dev.hpm.accecurity.core.model.TipoUsuario

/**
 * Estado inmutable de la pantalla de Perfil.
 */
data class ProfileState(
    val nombre: String = "Carlos Mendoza",
    val iniciales: String = "CM",
    val tipoUsuario: TipoUsuario = TipoUsuario.ADMIN,
    val numeroSolicitudesJefe: Int = 0
)
