package dev.hpm.accecurity.feature.solicitudes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarJefe
import dev.hpm.accecurity.core.ui.components.solicitud.BadgePendientes
import dev.hpm.accecurity.core.ui.components.solicitud.SolicitudItem

/**
 * Pantalla de solicitudes de acceso para el rol jefe de seguridad.
 * Muestra solicitudes pendientes con opciones de aprobar o rechazar.
 */
@Composable
fun SolicitudesScreenJefe(
    modifier: Modifier = Modifier,
    onSolicitudesClick: () -> Unit = {},
    onAdentroClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: SolicitudesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val solicitudes = state.solicitudes
    val pendientes = state.pendientes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Solicitudes de acceso",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Garita de Seguridad",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        bottomBar = {
            BottomNavigationBarJefe(
                solicitudes = true,
                adentro = false,
                actividad = false,
                perfil = false,
                numeroSolicitudes = pendientes,
                onSolicitudesClick = onSolicitudesClick,
                onAdentroClick = onAdentroClick,
                onActividadClick = onActividadClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgePendientes(cantidad = pendientes)
                    Text(
                        text = "Ordenado por urgencia",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            state.mensaje?.let { mensaje ->
                item {
                    Text(
                        text = mensaje,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }

            if (solicitudes.isEmpty()) {
                item {
                    Text(
                        text = "No hay solicitudes pendientes.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(
                items = solicitudes,
                key = { it.id }
            ) { solicitud ->
                SolicitudItem(
                    solicitud = solicitud,
                    onRechazar = {
                        viewModel.onEvent(
                            SolicitudesEvent.RechazarSolicitud(solicitud.id)
                        )
                    },
                    onAprobar = {
                        viewModel.onEvent(
                            SolicitudesEvent.AprobarSolicitud(solicitud.id)
                        )
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
