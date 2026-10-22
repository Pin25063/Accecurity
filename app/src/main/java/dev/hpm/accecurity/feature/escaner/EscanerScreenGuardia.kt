package dev.hpm.accecurity.feature.escaner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.core.ui.components.escaner.BadgeEnLinea
import dev.hpm.accecurity.core.ui.components.escaner.VisorQrPlaceholder
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarGuardia

@Composable
fun EscanerScreenGuardia(
    modifier: Modifier = Modifier,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Escáner de garita",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Garita Principal • Estación del guardia",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BadgeEnLinea()
            }
        },
        bottomBar = {
            BottomNavigationBarGuardia(
                escaner = true,
                registro = false,
                perfil = false,
                onEscanerClick = onEscanerClick,
                onRegistroClick = onRegistroClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            VisorQrPlaceholder(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Escanear código QR", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(Icons.Outlined.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar salida", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun RegistroGaritaScreen(
    modifier: Modifier = Modifier,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Text(
                text = "Registro de garita",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        },
        bottomBar = {
            BottomNavigationBarGuardia(
                escaner = false,
                registro = true,
                perfil = false,
                onEscanerClick = onEscanerClick,
                onRegistroClick = onRegistroClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Registro visual (datos de prueba)", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
