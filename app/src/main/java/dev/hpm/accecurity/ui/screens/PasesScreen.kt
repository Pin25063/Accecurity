package dev.hpm.accecurity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.data.obtenerPasesFalsos
import dev.hpm.accecurity.ui.components.navigation.BottomNavigationBarAdminYResidente
import dev.hpm.accecurity.ui.components.pass.PaseItem

/**
 * Pantalla de listado de pases del usuario (admin y residente).
 */
@Composable
fun PasesScreenAdminYResidente(modifier: Modifier = Modifier) {
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
        bottomBar = { BottomNavigationBarAdminYResidente(inicio = false, pases = true, actividad = false, perfil = false) }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(obtenerPasesFalsos()) { pase ->
                PaseItem(pase = pase)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
