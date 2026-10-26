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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hpm.accecurity.core.model.TipoUsuario
import dev.hpm.accecurity.core.ui.components.activity.ActividadItem
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarAdminYResidente
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarJefe
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.TipoActividad

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
    tipoUsuario: TipoUsuario = TipoUsuario.ADMIN,
    numeroSolicitudesJefe: Int = 0,
    onInicioClick: () -> Unit = {},
    onPasesClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onSolicitudesClick: () -> Unit = onInicioClick,
    onAdentroClick: () -> Unit = onPasesClick,
    viewModel: ActivityViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(tipoUsuario, numeroSolicitudesJefe) {
        viewModel.onEvent(
            ActivityEvent.ConfigurarPantalla(
                tipoUsuario = tipoUsuario,
                numeroSolicitudesJefe = numeroSolicitudesJefe
            )
        )
    }

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
            when (state.tipoUsuario) {
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
                        numeroSolicitudes = state.numeroSolicitudesJefe,
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
            if (state.actividades.isEmpty()) {
                item {
                    Text(
                        text = "No hay actividad registrada.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(
                items = state.actividades,
                key = { it.id }
            ) { actividad ->
                RegistroActividadItem(actividad = actividad)
            }
        }
    }
}

/**
 * Representación visual de un movimiento según su tipo.
 */
@Composable
private fun RegistroActividadItem(actividad: ActividadData) {
    val icono = when (actividad.tipo) {
        TipoActividad.ENTRADA -> Icons.Outlined.Login
        TipoActividad.PASE_CREADO -> Icons.Outlined.ConfirmationNumber
        TipoActividad.SALIDA -> Icons.AutoMirrored.Outlined.ExitToApp
        TipoActividad.RECHAZO -> Icons.Outlined.WarningAmber
    }

    val colorIcono = when (actividad.tipo) {
        TipoActividad.ENTRADA -> Color(0xFF287D3C)
        TipoActividad.PASE_CREADO -> MaterialTheme.colorScheme.primary
        TipoActividad.SALIDA -> MaterialTheme.colorScheme.onSurfaceVariant
        TipoActividad.RECHAZO -> Color(0xFFB3261E)
    }

    val colorFondo = when (actividad.tipo) {
        TipoActividad.ENTRADA -> Color(0xFFDDF3E2)
        TipoActividad.PASE_CREADO -> MaterialTheme.colorScheme.primaryContainer
        TipoActividad.SALIDA -> MaterialTheme.colorScheme.surfaceVariant
        TipoActividad.RECHAZO -> Color(0xFFFCE4E2)
    }

    ActividadItem(
        titulo = actividad.titulo,
        descripcion = actividad.descripcion,
        hora = actividad.hora,
        icono = icono,
        colorIcono = colorIcono,
        colorFondo = colorFondo
    )
}