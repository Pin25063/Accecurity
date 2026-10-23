package dev.hpm.accecurity.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hpm.accecurity.core.model.TipoUsuario
import dev.hpm.accecurity.core.ui.components.navigation.*
import dev.hpm.accecurity.core.ui.components.profile.*
import dev.hpm.accecurity.core.ui.components.topbar.TopBarPerfil

/**
 * Pantalla de perfil del usuario. Adapta la barra inferior según el rol.
 *
 * @param nombre Nombre completo del usuario.
 * @param iniciales Iniciales para el avatar.
 * @param tipoUsuario Rol que determina la navegación inferior.
 * @param numeroSolicitudesJefe Badge de solicitudes pendientes (solo jefe).
 * @param onInicioClick Callback al presionar la pestaña Inicio.
 * @param onPasesClick Callback al presionar la pestaña Pases.
 * @param onActividadClick Callback al presionar la pestaña Actividad.
 * @param onPerfilClick Callback al presionar la pestaña Perfil.
 * @param onLogOutClick Callback al presionar el botón de cerrar sesión.
 * @param onSolicitudesClick Callback al presionar la pestaña Solicitudes (solo jefe).
 * @param onAdentroClick Callback al presionar la pestaña Adentro (solo jefe).
 * @param onEscanerClick Callback al presionar la pestaña Escáner (solo guardia).
 * @param onRegistroClick Callback al presionar la pestaña Registro (solo guardia).
 */
@Composable
fun PerfilScreen(
    nombre: String = "Carlos Mendoza",
    iniciales: String = "CM",
    tipoUsuario: TipoUsuario,
    numeroSolicitudesJefe: Int = 0,
    onInicioClick: () -> Unit = {},
    onPasesClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {},
    onSolicitudesClick: () -> Unit = onInicioClick,
    onAdentroClick: () -> Unit = onPasesClick,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {}
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
                        perfil = true,
                        onInicioClick = onInicioClick,
                        onPasesClick = onPasesClick,
                        onActividadClick = onActividadClick,
                        onPerfilClick = onPerfilClick
                    )
                }
                TipoUsuario.GUARDIA -> {
                    BottomNavigationBarGuardia(
                        escaner = false,
                        registro = false,
                        perfil = true,
                        onEscanerClick = onEscanerClick,
                        onRegistroClick = onRegistroClick,
                        onPerfilClick = onPerfilClick
                    )
                }
                TipoUsuario.JEFE -> {
                    BottomNavigationBarJefe(
                        solicitudes = false,
                        adentro = false,
                        actividad = false,
                        perfil = true,
                        numeroSolicitudes = numeroSolicitudesJefe,
                        onSolicitudesClick = onSolicitudesClick,
                        onAdentroClick = onAdentroClick,
                        onActividadClick = onActividadClick,
                        onPerfilClick = onPerfilClick
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

            BotonCerrarSesion(onClick = onLogOutClick)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
