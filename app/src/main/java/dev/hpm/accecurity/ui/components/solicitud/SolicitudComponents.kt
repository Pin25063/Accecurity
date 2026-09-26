package dev.hpm.accecurity.ui.components.solicitud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.model.SolicitudData
import dev.hpm.accecurity.ui.theme.StatusExpectedContainer

private val UrgenteContainer = Color(0xFFFDE8E8)
private val UrgenteContent = Color(0xFFD32F2F)
private val RechazarContent = Color(0xFFD32F2F)

/**
 * Tarjeta individual de solicitud de acceso con acciones de aprobar y rechazar.
 */
@Composable
fun SolicitudItem(
    solicitud: SolicitudData,
    onRechazar: () -> Unit = {},
    onAprobar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = solicitud.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (solicitud.esUrgente) {
                BadgeUrgente()
            }
        }

        Text(
            text = solicitud.descripcion,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        DetalleSolicitudFila(
            icono = Icons.Outlined.Schedule,
            texto = solicitud.horario
        )
        DetalleSolicitudFila(
            icono = Icons.Outlined.LocationOn,
            texto = solicitud.ubicacion
        )
        DetalleSolicitudFila(
            icono = Icons.Outlined.Person,
            texto = solicitud.solicitadoPor
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRechazar,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = RechazarContent
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Rechazar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Rechazar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onAprobar,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Aprobar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Aprobar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Badge amarillo con la cantidad de solicitudes pendientes.
 */
@Composable
fun BadgePendientes(cantidad: Int) {
    Row(
        modifier = Modifier
            .background(StatusExpectedContainer, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color(0xFFD69E2E), RoundedCornerShape(4.dp))
        )
        Text(
            text = "$cantidad pendientes",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BadgeUrgente() {
    Row(
        modifier = Modifier
            .background(UrgenteContainer, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.WarningAmber,
            contentDescription = "Urgente",
            tint = UrgenteContent,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "Urgente",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = UrgenteContent
        )
    }
}

@Composable
private fun DetalleSolicitudFila(icono: ImageVector, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = texto,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
