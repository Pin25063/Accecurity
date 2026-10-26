package dev.hpm.accecurity.feature.inside

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    LaunchedEffect(numeroSolicitudesJefe) {
        viewModel.onEvent(
            InsideEvent.ConfigurarPantalla(
                numeroSolicitudesJefe = numeroSolicitudesJefe
            )
        )
    }

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

            if (state.personas.isEmpty()) {
                item {
                    Text(
                        text = "No hay personas dentro de las instalaciones.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(
                items = state.personas,
                key = { it.id }
            ) { persona ->
                PersonaAdentroItem(
                    nombre = persona.nombre,
                    descripcion = persona.descripcion,
                    destino = persona.destino,
                    horaEntrada = persona.horaEntrada
                )
            }
        }
    }
}