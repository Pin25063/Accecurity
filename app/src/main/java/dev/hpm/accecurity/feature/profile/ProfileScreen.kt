package dev.hpm.accecurity.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hpm.accecurity.core.model.TipoUsuario
import dev.hpm.accecurity.core.ui.components.navigation.*
import dev.hpm.accecurity.core.ui.components.profile.*
import dev.hpm.accecurity.core.ui.components.topbar.TopBarPerfil
import androidx.compose.runtime.LaunchedEffect

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
 * @param onLogOutClick Callback al confirmar el cierre de sesión.
 * @param onSolicitudesClick Callback al presionar la pestaña Solicitudes (solo jefe).
 * @param onAdentroClick Callback al presionar la pestaña Adentro (solo jefe).
 * @param onEscanerClick Callback al presionar la pestaña Escáner (solo guardia).
 * @param onRegistroClick Callback al presionar la pestaña Registro (solo guardia).
 */
@Composable
fun PerfilScreen(
    nombre: String = "Carlos Mendoza",
    iniciales: String = "CM",
    tipoUsuario: TipoUsuario = TipoUsuario.ADMIN,
    numeroSolicitudesJefe: Int = 0,
    onInicioClick: () -> Unit = {},
    onPasesClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {},
    onSolicitudesClick: () -> Unit = onInicioClick,
    onAdentroClick: () -> Unit = onPasesClick,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    state.opcionSeleccionada?.let { opcion ->
        OpcionPerfilDialog(
            opcion = opcion,
            state = state,
            onEvent = viewModel::onEvent
        )
    }

    LaunchedEffect(nombre, iniciales, tipoUsuario, numeroSolicitudesJefe) {
        viewModel.onEvent(
            ProfileEvent.ConfigurarPerfil(
                nombre = nombre,
                iniciales = iniciales,
                tipoUsuario = tipoUsuario,
                numeroSolicitudesJefe = numeroSolicitudesJefe
            )
        )
    }

    LaunchedEffect(state.sesionCerrada) {
        if (state.sesionCerrada) {
            onLogOutClick()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarPerfil() },
        bottomBar = {
            when (state.tipoUsuario) {
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
                        numeroSolicitudes = state.numeroSolicitudesJefe,
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
                iniciales = state.iniciales,
                nombre = state.nombre,
                tipoUsuario = state.tipoUsuario
            )

            MenuPerfil(
                onNotificacionesClick = {
                    viewModel.onEvent(
                        ProfileEvent.SeleccionarOpcion(
                            OpcionPerfil.NOTIFICACIONES
                        )
                    )
                },
                onPreferenciasClick = {
                    viewModel.onEvent(
                        ProfileEvent.SeleccionarOpcion(
                            OpcionPerfil.PREFERENCIAS
                        )
                    )
                },
                onAyudaClick = {
                    viewModel.onEvent(
                        ProfileEvent.SeleccionarOpcion(
                            OpcionPerfil.AYUDA
                        )
                    )
                }
            )

            state.errorMessage?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BotonCerrarSesion(
                onClick = {
                    viewModel.onEvent(ProfileEvent.CerrarSesion)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Diálogo de consulta y configuración de las opciones de Perfil.
 */
@Composable
private fun OpcionPerfilDialog(
    opcion: OpcionPerfil,
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
) {
    val titulo = when (opcion) {
        OpcionPerfil.NOTIFICACIONES -> "Notificaciones"
        OpcionPerfil.PREFERENCIAS -> "Preferencias"
        OpcionPerfil.AYUDA -> "Ayuda y soporte"
    }

    AlertDialog(
        onDismissRequest = {
            onEvent(ProfileEvent.CerrarOpcion)
        },
        title = {
            Text(text = titulo)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (opcion) {
                    OpcionPerfil.NOTIFICACIONES -> {
                        if (!state.mostrarNotificaciones) {
                            Text(
                                text = "Las notificaciones están ocultas. " +
                                        "Puedes mostrarlas desde Preferencias."
                            )
                        } else if (state.notificaciones.isEmpty()) {
                            Text(text = "No hay notificaciones.")
                        } else {
                            state.notificaciones.forEach { notificacion ->
                                Text(text = notificacion)
                            }
                        }
                    }

                    OpcionPerfil.PREFERENCIAS -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mostrar notificaciones",
                                modifier = Modifier.weight(1f)
                            )

                            Switch(
                                checked = state.mostrarNotificaciones,
                                onCheckedChange = {
                                    onEvent(
                                        ProfileEvent.CambiarNotificaciones(it)
                                    )
                                }
                            )
                        }
                    }

                    OpcionPerfil.AYUDA -> {
                        Text(text = state.ayuda)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(ProfileEvent.CerrarOpcion)
                }
            ) {
                Text(text = "Cerrar")
            }
        }
    )
}