package dev.hpm.accecurity.feature.registro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.ConfirmationNumber
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
import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.TipoActividad
import dev.hpm.accecurity.core.ui.components.activity.ActividadItem
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarGuardia

/**
 * Pantalla de registro de garita con el listado de eventos de acceso.
 */
@Composable
fun RegistroGaritaScreen(
    modifier: Modifier = Modifier,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: RegistroViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
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
                    text = "Registro de garita",
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
            BottomNavigationBarGuardia(
                escaner = false,
                registro = true,
                perfil = false,
                onEscanerClick = onEscanerClick,
                onRegistroClick = onRegistroClick,
                onPerfilClick = onPerfilClick
            )
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
            if (state.movimientos.isEmpty()) {
                item {
                    Text(
                        text = "No hay movimientos registrados.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(
                items = state.movimientos,
                key = { it.id }
            ) { movimiento ->
                MovimientoGaritaItem(movimiento = movimiento)
            }
        }
    }
}

/**
 * Representación visual de un movimiento del registro de garita.
 */
@Composable
private fun MovimientoGaritaItem(movimiento: ActividadData) {
    val icono = when (movimiento.tipo) {
        TipoActividad.ENTRADA -> Icons.AutoMirrored.Outlined.Login
        TipoActividad.PASE_CREADO -> Icons.Outlined.ConfirmationNumber
        TipoActividad.SALIDA -> Icons.AutoMirrored.Outlined.ExitToApp
        TipoActividad.RECHAZO -> Icons.Outlined.WarningAmber
    }

    val colorIcono = when (movimiento.tipo) {
        TipoActividad.ENTRADA -> Color(0xFF287D3C)
        TipoActividad.PASE_CREADO -> MaterialTheme.colorScheme.primary
        TipoActividad.SALIDA -> MaterialTheme.colorScheme.onSurfaceVariant
        TipoActividad.RECHAZO -> Color(0xFFB3261E)
    }

    val colorFondo = when (movimiento.tipo) {
        TipoActividad.ENTRADA -> Color(0xFFDDF3E2)
        TipoActividad.PASE_CREADO -> MaterialTheme.colorScheme.primaryContainer
        TipoActividad.SALIDA -> MaterialTheme.colorScheme.surfaceVariant
        TipoActividad.RECHAZO -> Color(0xFFFCE4E2)
    }

    ActividadItem(
        titulo = movimiento.titulo,
        descripcion = movimiento.descripcion,
        hora = movimiento.hora,
        icono = icono,
        colorIcono = colorIcono,
        colorFondo = colorFondo
    )
}