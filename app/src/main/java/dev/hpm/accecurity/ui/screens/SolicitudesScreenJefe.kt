package dev.hpm.accecurity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.data.contarSolicitudesPendientes
import dev.hpm.accecurity.data.obtenerSolicitudesFalsas
import dev.hpm.accecurity.ui.components.navigation.BottomNavigationBarJefe
import dev.hpm.accecurity.ui.components.solicitud.BadgePendientes
import dev.hpm.accecurity.ui.components.solicitud.SolicitudItem
import dev.hpm.accecurity.ui.theme.ProyectoTheme

/**
 * Pantalla de solicitudes de acceso para el rol jefe de seguridad.
 * Muestra solicitudes pendientes con opciones de aprobar o rechazar.
 */
@Composable
fun SolicitudesScreenJefe(modifier: Modifier = Modifier) {
    val solicitudes = obtenerSolicitudesFalsas()
    val pendientes = contarSolicitudesPendientes()

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
                numeroSolicitudes = pendientes
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

            items(solicitudes) { solicitud ->
                SolicitudItem(solicitud = solicitud)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Preview(
    name = "Solicitudes de acceso - Jefe",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SolicitudesScreenJefePreview() {
    ProyectoTheme {
        SolicitudesScreenJefe()
    }
}
