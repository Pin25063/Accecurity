package dev.hpm.accecurity.ui.components.escaner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hpm.accecurity.ui.theme.StatusInside
import dev.hpm.accecurity.ui.theme.StatusInsideContainer

private val FondoCamara = Color(0xFF232628)

/**
 * Placeholder visual del visor de cámara con marco de escaneo QR.
 * Solo representación gráfica; sin acceso real a la cámara.
 */
@Composable
fun VisorQrPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(FondoCamara),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                EsquinaVisor(modifier = Modifier.align(Alignment.TopStart))
                EsquinaVisor(modifier = Modifier.align(Alignment.TopEnd), espejoHorizontal = true)
                EsquinaVisor(modifier = Modifier.align(Alignment.BottomStart), espejoVertical = true)
                EsquinaVisor(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    espejoHorizontal = true,
                    espejoVertical = true
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(2.dp)
                            .background(Color.White.copy(alpha = 0.6f))
                    )
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(2.dp)
                            .background(Color.White.copy(alpha = 0.6f))
                    )
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(2.dp)
                            .background(Color.White.copy(alpha = 0.6f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Apunta la cámara al código QR del visitante",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
private fun EsquinaVisor(
    modifier: Modifier = Modifier,
    espejoHorizontal: Boolean = false,
    espejoVertical: Boolean = false
) {
    val horizontalArrangement = if (espejoHorizontal) Arrangement.End else Arrangement.Start
    val verticalArrangement = if (espejoVertical) Arrangement.Bottom else Arrangement.Top

    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = verticalArrangement
    ) {
        Row(horizontalArrangement = horizontalArrangement) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(3.dp)
                    .background(Color.White)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Row(horizontalArrangement = horizontalArrangement) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(28.dp)
                    .background(Color.White)
            )
        }
    }
}

/**
 * Indicador de conexión activa en la garita.
 */
@Composable
fun BadgeEnLinea() {
    Row(
        modifier = Modifier
            .background(StatusInsideContainer, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Wifi,
            contentDescription = "En línea",
            tint = StatusInside,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "En línea",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = StatusInside
        )
    }
}
