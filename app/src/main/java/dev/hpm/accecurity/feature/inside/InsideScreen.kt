package dev.hpm.accecurity.feature.inside

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hpm.accecurity.core.ui.components.inside.PersonaAdentroItem
import dev.hpm.accecurity.core.ui.components.inside.ResumenPersonasAdentro
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarJefe
import dev.hpm.accecurity.core.ui.components.topbar.TopBarActualmenteAdentro

/**
 * Pantalla de consulta de visitantes dentro de las instalaciones.
 *
 * @param numeroSolicitudesJefe Badge de solicitudes pendientes.
 */
@Composable
fun InsideScreen(
    modifier: Modifier = Modifier,
    numeroSolicitudesJefe: Int = 0,
    onSolicitudesClick: () -> Unit = {},
    onAdentroClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: InsideViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarActualmenteAdentro() },
        bottomBar = {
            BottomNavigationBarJefe(
                solicitudes = false,
                adentro = true,
                actividad = false,
                perfil = false,
                numeroSolicitudes = state.numeroSolicitudesJefe,
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
                .padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            item {
                ResumenPersonasAdentro(cantidad = state.totalAdentro)

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                PersonaAdentroItem(
                    nombre = "Laura Méndez",
                    descripcion = "Visita familiar",
                    destino = "Secretaría",
                    horaEntrada = "09:12"
                )
            }

            item {
                PersonaAdentroItem(
                    nombre = "Mensajero DHL",
                    descripcion = "Entrega de paquete",
                    destino = "Recepción",
                    horaEntrada = "09:40"
                )
            }

            item {
                PersonaAdentroItem(
                    nombre = "Diego Salas",
                    descripcion = "Mantenimiento",
                    destino = "Área técnica",
                    horaEntrada = "10:00"
                )
            }

            item {
                PersonaAdentroItem(
                    nombre = "Prof. Ramírez",
                    descripcion = "Reunión",
                    destino = "Dirección",
                    horaEntrada = "10:15"
                )
            }

            item {
                PersonaAdentroItem(
                    nombre = "Sofía Cano",
                    descripcion = "Visita",
                    destino = "Secretaría",
                    horaEntrada = "10:30"
                )
            }
        }
    }
}
