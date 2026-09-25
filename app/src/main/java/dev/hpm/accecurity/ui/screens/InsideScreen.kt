package dev.hpm.accecurity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hpm.accecurity.ui.components.inside.PersonaAdentroItem
import dev.hpm.accecurity.ui.components.inside.ResumenPersonasAdentro
import dev.hpm.accecurity.ui.components.navigation.BottomNavigationBarJefe
import dev.hpm.accecurity.ui.components.topbar.TopBarActualmenteAdentro

/**
 * Pantalla de consulta de visitantes dentro de las instalaciones.
 *
 * @param numeroSolicitudesJefe Badge de solicitudes pendientes.
 */
@Composable
fun ActualmenteAdentroScreen(
    numeroSolicitudesJefe: Int = 0
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarActualmenteAdentro() },
        bottomBar = {
            BottomNavigationBarJefe(
                solicitudes = false,
                adentro = true,
                actividad = false,
                perfil = false,
                numeroSolicitudes = numeroSolicitudesJefe
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
            item {
                ResumenPersonasAdentro(cantidad = 5)

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