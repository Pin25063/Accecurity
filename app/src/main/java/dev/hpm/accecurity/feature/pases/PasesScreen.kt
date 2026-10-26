package dev.hpm.accecurity.feature.pases

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarAdminYResidente
import dev.hpm.accecurity.core.ui.components.pass.*
import dev.hpm.accecurity.core.ui.components.topbar.TopBarInicioAdminYResidente
import dev.hpm.accecurity.core.ui.theme.StatusExpectedContainer
import dev.hpm.accecurity.core.ui.theme.StatusInsideContainer
import dev.hpm.accecurity.core.model.PaseData

/**
 * Pantalla de listado de pases del usuario (admin y residente).
 */
@Composable
fun PasesScreenAdminYResidente(
    modifier: Modifier = Modifier,
    onInicioClick: () -> Unit = {},
    onPasesClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: PasesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    state.paseSeleccionado?.let { pase ->
        DetallePaseDialog(
            pase = pase,
            onCerrar = {
                viewModel.onEvent(PasesEvent.CerrarDetalle)
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mis pases",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Invitaciones activas y recientes",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBarAdminYResidente(
                inicio = false,
                pases = true,
                actividad = false,
                perfil = false,
                onInicioClick = onInicioClick,
                onPasesClick = onPasesClick,
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(state.pases) { pase ->
                PaseItem(
                    pase = pase,
                    onClick = {
                        viewModel.onEvent(PasesEvent.VerDetalle(pase))
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * Pantalla principal del administrador con estadísticas, filtros y listado de pases.
 */
@Composable
fun HomeScreenAdmin(
    onInicioClick: () -> Unit = {},
    onPasesClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: PasesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    state.paseSeleccionado?.let { pase ->
        DetallePaseDialog(
            pase = pase,
            onCerrar = {
                viewModel.onEvent(PasesEvent.CerrarDetalle)
            }
        )
    }

    if (state.mostrarFormulario) {
        CrearPaseDialog(
            state = state,
            onEvent = viewModel::onEvent
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarInicioAdminYResidente() },
        bottomBar = {
            BottomNavigationBarAdminYResidente(
                inicio = true,
                pases = false,
                actividad = false,
                perfil = false,
                onInicioClick = onInicioClick,
                onPasesClick = onPasesClick,
                onActividadClick = onActividadClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                BotonCrearPase(
                    onClick = {
                        viewModel.onEvent(PasesEvent.CrearPase)
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CuadroEstadistica(
                        titulo = state.totalEsperados.toString(),
                        subtitulo = "Esperados",
                        colorFondo = StatusExpectedContainer,
                        modifier = Modifier.weight(1f)
                    )
                    CuadroEstadistica(
                        titulo = state.totalAdentro.toString(),
                        subtitulo = "Adentro",
                        colorFondo = StatusInsideContainer,
                        modifier = Modifier.weight(1f)
                    )
                    CuadroEstadistica(
                        titulo = state.totalSalieron.toString(),
                        subtitulo = "Salieron",
                        colorFondo = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rastreo en vivo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FiltroChip(
                            texto = "Todos",
                            seleccionado = state.filtroSeleccionado == "Todos",
                            onClick = {
                                viewModel.onEvent(
                                    PasesEvent.CambiarFiltro("Todos")
                                )
                            }
                        )
                        FiltroChip(
                            texto = "Esperados",
                            seleccionado = state.filtroSeleccionado == "Esperados",
                            onClick = {
                                viewModel.onEvent(
                                    PasesEvent.CambiarFiltro("Esperados")
                                )
                            }
                        )
                        FiltroChip(
                            texto = "Adentro",
                            seleccionado = state.filtroSeleccionado == "Adentro",
                            onClick = {
                                viewModel.onEvent(
                                    PasesEvent.CambiarFiltro("Adentro")
                                )
                            }
                        )
                        FiltroChip(
                            texto = "Salieron",
                            seleccionado = state.filtroSeleccionado == "Salieron",
                            onClick = {
                                viewModel.onEvent(
                                    PasesEvent.CambiarFiltro("Salieron")
                                )
                            }
                        )
                    }
                }
            }

            if (state.pasesFiltrados.isEmpty()) {
                item {
                    Text(
                        text = "No hay pases para este filtro.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(state.pasesFiltrados) { pase ->
                PaseItem(
                    pase = pase,
                    onClick = {
                        viewModel.onEvent(PasesEvent.VerDetalle(pase))
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * Formulario de creación de un pase digital.
 */
@Composable
private fun CrearPaseDialog(
    state: PasesState,
    onEvent: (PasesEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onEvent(PasesEvent.CancelarCreacion)
        },
        title = {
            Text(text = "Crear pase digital")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.nombreVisitante,
                    onValueChange = {
                        onEvent(PasesEvent.CambiarNombre(it))
                    },
                    label = {
                        Text(text = "Nombre del visitante")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.descripcionVisita,
                    onValueChange = {
                        onEvent(PasesEvent.CambiarDescripcion(it))
                    },
                    label = {
                        Text(text = "Motivo y destino de la visita")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                OutlinedTextField(
                    value = state.horaVisita,
                    onValueChange = {
                        onEvent(PasesEvent.CambiarHora(it))
                    },
                    label = {
                        Text(text = "Hora de la visita")
                    },
                    supportingText = {
                        Text(text = "Formato de 24 horas. Ejemplo: 14:30")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                state.errorFormulario?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(PasesEvent.GuardarPase)
                }
            ) {
                Text(text = "Crear pase")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(PasesEvent.CancelarCreacion)
                }
            ) {
                Text(text = "Cancelar")
            }
        }
    )
}

/**
 * Diálogo con la información del pase seleccionado.
 */
@Composable
private fun DetallePaseDialog(
    pase: PaseData,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(text = "Detalle del pase")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Visitante",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = pase.nombre,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Motivo y destino",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = pase.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Estado",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = pase.estado,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Hora",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = pase.hora,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text(text = "Cerrar")
            }
        }
    )
}
