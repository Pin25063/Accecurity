package dev.hpm.accecurity.feature.escaner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hpm.accecurity.core.ui.components.activity.ActividadItem
import dev.hpm.accecurity.core.ui.components.escaner.BadgeEnLinea
import dev.hpm.accecurity.core.ui.components.escaner.VisorQrPlaceholder
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarGuardia

/**
 * Pantalla de escáner QR para el rol guardia de seguridad.
 * Solo interfaz visual; la funcionalidad de cámara se implementará después.
 */
@Composable
fun EscanerScreenGuardia(
    modifier: Modifier = Modifier,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: EscanerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = "Escanear código QR",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Escanear código QR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = "Registrar salida",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar salida",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Pantalla de registro de garita con el listado de eventos de acceso.
 */
@Composable
fun RegistroGaritaScreen(
    modifier: Modifier = Modifier,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: EscanerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp
                    )
            ) {
                Text(
                    text = "Registro de garita",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Hoy",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                ActividadItem(
                    titulo = "Laura Méndez ingresó",
                    descripcion = "Visita principal · QR verificado",
                    hora = "09:12",
                    icono = Icons.AutoMirrored.Outlined.Login,
                    colorIcono = Color(0xFF287D3C),
                    colorFondo = Color(0xFFDDF3E2)
                )
            }

            item {
                ActividadItem(
                    titulo = "Pase digital creado",
                    descripcion = "Para Diego Salas · 11:00",
                    hora = "08:47",
                    icono = Icons.Outlined.ConfirmationNumber,
                    colorIcono = MaterialTheme.colorScheme.primary,
                    colorFondo = MaterialTheme.colorScheme.primaryContainer
                )
            }

            item {
                ActividadItem(
                    titulo = "Carlos Núñez salió",
                    descripcion = "Garita principal",
                    hora = "08:35",
                    icono = Icons.AutoMirrored.Outlined.ExitToApp,
                    colorIcono = MaterialTheme.colorScheme.onSurfaceVariant,
                    colorFondo = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            item {
                ActividadItem(
                    titulo = "Código vencido rechazado",
                    descripcion = "Garita de servicio · ACC-4070",
                    hora = "08:21",
                    icono = Icons.Outlined.WarningAmber,
                    colorIcono = Color(0xFFB3261E),
                    colorFondo = Color(0xFFFCE4E2)
                )
            }
        }
    }
}
