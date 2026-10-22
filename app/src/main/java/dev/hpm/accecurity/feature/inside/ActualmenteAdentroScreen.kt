package dev.hpm.accecurity.feature.inside

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.core.data.contarSolicitudesPendientes
import dev.hpm.accecurity.core.ui.components.inside.PersonaAdentroItem
import dev.hpm.accecurity.core.ui.components.inside.ResumenPersonasAdentro
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarJefe
import dev.hpm.accecurity.core.ui.components.topbar.TopBarActualmenteAdentro

/**
 * Pantalla de personas actualmente dentro de las instalaciones (rol jefe).
 */
@Composable
fun ActualmenteAdentroScreen(
    modifier: Modifier = Modifier,
    numeroSolicitudesJefe: Int = contarSolicitudesPendientes(),
    onSolicitudesClick: () -> Unit = {},
    onAdentroClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
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
                numeroSolicitudes = numeroSolicitudesJefe,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ResumenPersonasAdentro(cantidad = 2)
            }
            item {
                Text(
                    text = "Visitantes dentro",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            item {
                PersonaAdentroItem(
                    nombre = "Laura Méndez",
                    descripcion = "Visita familiar",
                    destino = "Apto 402",
                    horaEntrada = "09:12"
                )
            }
            item {
                PersonaAdentroItem(
                    nombre = "Mensajero DHL",
                    descripcion = "Entrega de paquete",
                    destino = "Garita principal",
                    horaEntrada = "09:40"
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
