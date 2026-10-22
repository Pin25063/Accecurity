package dev.hpm.accecurity.feature.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.core.model.TipoUsuario
import dev.hpm.accecurity.core.ui.components.activity.ActividadItem
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarAdminYResidente
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarJefe

/**
 * Pantalla de actividad del usuario. Adapta la barra inferior según el rol.
 *
 * @param tipoUsuario Rol que determina la navegación inferior.
 * @param numeroSolicitudesJefe Badge de solicitudes pendientes (solo jefe).
 * @param onInicioClick Callback al presionar la pestaña Inicio.
 * @param onPasesClick Callback al presionar la pestaña Pases.
 * @param onActividadClick Callback al presionar la pestaña Actividad.
 * @param onPerfilClick Callback al presionar la pestaña Perfil.
 */
@Composable
fun ActivityScreen(
    tipoUsuario: TipoUsuario,
    numeroSolicitudesJefe: Int = 0,
    onInicioClick: () -> Unit = {},
    onPasesClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onSolicitudesClick: () -> Unit = onInicioClick,
    onAdentroClick: () -> Unit = onPasesClick
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp
                    )
            ) {
                Text(
                    text = "Actividad",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Hoy",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        bottomBar = {
            when (tipoUsuario) {
                TipoUsuario.ADMIN, TipoUsuario.RESIDENTE -> {
                    BottomNavigationBarAdminYResidente(
                        inicio = false,
                        pases = false,
                        actividad = true,
                        perfil = false,
                        onInicioClick = onInicioClick,
                        onPasesClick = onPasesClick,
                        onActividadClick = onActividadClick,
                        onPerfilClick = onPerfilClick
                    )
                }

                TipoUsuario.JEFE -> {
                    BottomNavigationBarJefe(
                        solicitudes = false,
                        adentro = false,
                        actividad = true,
                        perfil = false,
                        numeroSolicitudes = numeroSolicitudesJefe,
                        onSolicitudesClick = onSolicitudesClick,
                        onAdentroClick = onAdentroClick,
                        onActividadClick = onActividadClick,
                        onPerfilClick = onPerfilClick
                    )
                }

                TipoUsuario.GUARDIA -> Unit
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            item {
                ActividadItem(
                    titulo = "Laura Méndez ingresó",
                    descripcion = "Visita principal · QR verificado",
                    hora = "09:12",
                    icono = Icons.Outlined.Login,
                    colorIcono = Color(0xFF287D3C),
                    colorFondo = Color(0xFFDDF3E2)
                )
            }

            item {
                ActividadItem(
                    titulo = "Pase digital creado",
                    descripcion = "Para Diego Salas · 11:00",
                    hora = "08:47",
                    icono = Icons.Outlined.ConfirmationNumber,
                    colorIcono = MaterialTheme.colorScheme.primary,
                    colorFondo = MaterialTheme.colorScheme.primaryContainer
                )
            }

            item {
                ActividadItem(
                    titulo = "Carlos Núñez salió",
                    descripcion = "Garita principal",
                    hora = "08:35",
                    icono = Icons.AutoMirrored.Outlined.ExitToApp,
                    colorIcono = MaterialTheme.colorScheme.onSurfaceVariant,
                    colorFondo = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            item {
                ActividadItem(
                    titulo = "Código vencido rechazado",
                    descripcion = "Garita de servicio · ACC-4070",
                    hora = "08:21",
                    icono = Icons.Outlined.WarningAmber,
                    colorIcono = Color(0xFFB3261E),
                    colorFondo = Color(0xFFFCE4E2)
                )
            }
        }
    }
}
