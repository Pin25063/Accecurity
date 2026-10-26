package dev.hpm.accecurity.core.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Barra de navegación inferior para roles admin y residente.
 *
 * @param inicio Indica si la pestaña Inicio está seleccionada.
 * @param pases Indica si la pestaña Pases está seleccionada.
 * @param actividad Indica si la pestaña Actividad está seleccionada.
 * @param perfil Indica si la pestaña Perfil está seleccionada.
 * @param onInicioClick Callback al seleccionar la pestaña Inicio.
 * @param onPasesClick Callback al seleccionar la pestaña Pases.
 * @param onActividadClick Callback al seleccionar la pestaña Actividad.
 * @param onPerfilClick Callback al seleccionar la pestaña Perfil.
 */
@Composable
fun BottomNavigationBarAdminYResidente(
    inicio: Boolean,
    pases: Boolean,
    actividad: Boolean,
    perfil: Boolean,
    onInicioClick: () -> Unit = {},
    onPasesClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = inicio,
            onClick = onInicioClick,
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = pases,
            onClick = onPasesClick,
            icon = { Icon(Icons.Outlined.ConfirmationNumber, contentDescription = "Pases") },
            label = { Text("Pases", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = actividad,
            onClick = onActividadClick,
            icon = { Icon(Icons.Outlined.Timeline, contentDescription = "Actividad") },
            label = { Text("Actividad", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = perfil,
            onClick = onPerfilClick,
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp) }
        )
    }
}

/**
 * Barra de navegación inferior para el rol guardia de seguridad.
 */
@Composable
fun BottomNavigationBarGuardia(
    escaner: Boolean,
    registro: Boolean,
    perfil: Boolean,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = escaner,
            onClick = onEscanerClick,
            icon = {
                Icon(
                    if (escaner) Icons.Filled.QrCodeScanner else Icons.Outlined.QrCodeScanner,
                    contentDescription = "Escáner"
                )
            },
            label = { Text("Escáner", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = registro,
            onClick = onRegistroClick,
            icon = {
                Icon(
                    if (registro) Icons.Filled.FactCheck else Icons.Outlined.FactCheck,
                    contentDescription = "Registro"
                )
            },
            label = { Text("Registro", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = perfil,
            onClick = onPerfilClick,
            icon = {
                Icon(
                    if (perfil) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

/**
 * Barra de navegación inferior para el rol jefe de seguridad.
 *
 * @param numeroSolicitudes Cantidad de solicitudes pendientes mostrada en el badge.
 * @param onSolicitudesClick Callback al seleccionar la pestaña Solicitudes.
 * @param onAdentroClick Callback al seleccionar la pestaña Adentro.
 * @param onActividadClick Callback al seleccionar la pestaña Actividad.
 * @param onPerfilClick Callback al seleccionar la pestaña Perfil.
 */
@Composable
fun BottomNavigationBarJefe(
    solicitudes: Boolean,
    adentro: Boolean,
    actividad: Boolean,
    perfil: Boolean,
    numeroSolicitudes: Int = 0,
    onSolicitudesClick: () -> Unit = {},
    onAdentroClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = solicitudes,
            onClick = onSolicitudesClick,
            icon = {
                BadgedBox(
                    badge = {
                        if (numeroSolicitudes > 0) {
                            Badge(
                                containerColor = Color(0xFFD32F2F),
                                contentColor = Color.White
                            ) {
                                Text(numeroSolicitudes.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (solicitudes) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                        contentDescription = "Solicitudes"
                    )
                }
            },
            label = { Text("Solicitudes", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = adentro,
            onClick = onAdentroClick,
            icon = {
                Icon(
                    if (adentro) Icons.Filled.MeetingRoom else Icons.Outlined.MeetingRoom,
                    contentDescription = "Adentro"
                )
            },
            label = { Text("Adentro", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = actividad,
            onClick = onActividadClick,
            icon = {
                Icon(
                    if (actividad) Icons.Filled.Timeline else Icons.Outlined.Timeline,
                    contentDescription = "Actividad"
                )
            },
            label = { Text("Actividad", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = perfil,
            onClick = onPerfilClick,
            icon = {
                Icon(
                    if (perfil) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
