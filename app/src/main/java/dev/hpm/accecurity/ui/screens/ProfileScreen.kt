package dev.hpm.accecurity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hpm.accecurity.model.TipoUsuario
import dev.hpm.accecurity.ui.components.navigation.*
import dev.hpm.accecurity.ui.components.profile.*
import dev.hpm.accecurity.ui.components.topbar.TopBarPerfil

/**
 * Pantalla de perfil del usuario. Adapta la barra inferior según el rol.
 *
 * @param nombre Nombre completo del usuario.
 * @param iniciales Iniciales para el avatar.
 * @param tipoUsuario Rol que determina la navegación inferior.
 * @param numeroSolicitudesJefe Badge de solicitudes pendientes (solo jefe).
 */
@Composable
fun PerfilScreen(
    nombre: String,
    iniciales: String,
    tipoUsuario: TipoUsuario,
    numeroSolicitudesJefe: Int = 0
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarPerfil() },
        bottomBar = {
            when (tipoUsuario) {
                TipoUsuario.ADMIN, TipoUsuario.RESIDENTE -> {
                    BottomNavigationBarAdminYResidente(
                        inicio = false,
                        pases = false,
                        actividad = false,
                        perfil = true
                    )
                }
                TipoUsuario.GUARDIA -> {
                    BottomNavigationBarGuardia(
                        escaner = false,
                        registro = false,
                        perfil = true
                    )
                }
                TipoUsuario.JEFE -> {
                    BottomNavigationBarJefe(
                        solicitudes = false,
                        adentro = false,
                        actividad = false,
                        perfil = true,
                        numeroSolicitudes = numeroSolicitudesJefe
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TarjetaInformacionUsuario(
                iniciales = iniciales,
                nombre = nombre,
                tipoUsuario = tipoUsuario
            )

            MenuPerfil()

            BotonCerrarSesion(onClick = { })

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
