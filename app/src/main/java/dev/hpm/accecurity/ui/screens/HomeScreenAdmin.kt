package dev.hpm.accecurity.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.data.obtenerPasesFalsos
import dev.hpm.accecurity.ui.components.navigation.BottomNavigationBarAdminYResidente
import dev.hpm.accecurity.ui.components.pass.*
import dev.hpm.accecurity.ui.components.topbar.TopBarInicioAdminYResidente
import dev.hpm.accecurity.ui.theme.StatusExpectedContainer
import dev.hpm.accecurity.ui.theme.StatusInsideContainer

/**
 * Pantalla principal del administrador con estadísticas, filtros y listado de pases.
 */
@Composable
fun HomeScreenAdmin() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarInicioAdminYResidente() },
        bottomBar = { BottomNavigationBarAdminYResidente(inicio = true, pases = false, actividad = false, perfil = false) }
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
                BotonCrearPase(onClick = { })
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CuadroEstadistica(
                        titulo = "2",
                        subtitulo = "Esperados",
                        colorFondo = StatusExpectedContainer,
                        modifier = Modifier.weight(1f)
                    )
                    CuadroEstadistica(
                        titulo = "2",
                        subtitulo = "Adentro",
                        colorFondo = StatusInsideContainer,
                        modifier = Modifier.weight(1f)
                    )
                    CuadroEstadistica(
                        titulo = "1",
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
                        FiltroChip(texto = "Todos", seleccionado = true)
                        FiltroChip(texto = "Esperados", seleccionado = false)
                        FiltroChip(texto = "Adentro", seleccionado = false)
                        FiltroChip(texto = "Salieron", seleccionado = false)
                    }
                }
            }

            items(obtenerPasesFalsos()) { pase ->
                PaseItem(pase = pase)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
