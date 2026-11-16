package dev.hpm.accecurity.core.ui.components.pass

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.hpm.accecurity.core.model.PaseData
import dev.hpm.accecurity.core.qr.PassQr
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** El QR se ofrece únicamente para pases aprobados que aún están esperados. */
@Composable
fun PaseQrSection(pase: PaseData) {
    if (pase.estadoAcceso != "EXPECTED") {
        Text("El QR de ingreso está disponible únicamente para pases aprobados y esperados.")
        return
    }
    if (pase.codigoQr.isBlank()) {
        Text("Este pase no tiene un código QR disponible.", color = MaterialTheme.colorScheme.error)
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var compartiendo by remember(pase.codigoQr) { mutableStateOf(false) }
    var errorCompartir by remember(pase.codigoQr) { mutableStateOf<String?>(null) }
    var intento by remember(pase.codigoQr) { mutableStateOf(0) }
    val resultado by produceState<Result<Bitmap>?>(null, pase.codigoQr, intento) {
        value = null
        value = try {
            Result.success(PassQr.generarImagen(pase.codigoQr))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
    val imagen = resultado?.getOrNull()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("QR de ingreso", style = MaterialTheme.typography.titleMedium)
        when {
            resultado == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            imagen != null -> {
                Image(
                    bitmap = imagen.asImageBitmap(),
                    contentDescription = "Código QR del pase de ${pase.nombre}",
                    modifier = Modifier.size(220.dp).background(Color.White).padding(8.dp),
                    filterQuality = FilterQuality.None
                )
                Text("Presentá este QR en garita dentro del horario autorizado.")
                Button(
                    enabled = !compartiendo,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (!compartiendo) {
                            compartiendo = true
                            errorCompartir = null
                            scope.launch {
                                try {
                                    val intent = PassQr.prepararCompartir(context, imagen, pase)
                                    context.startActivity(intent)
                                } catch (cancelled: CancellationException) {
                                    throw cancelled
                                } catch (_: Exception) {
                                    errorCompartir = "No se pudo compartir el QR. Intentá nuevamente."
                                } finally {
                                    compartiendo = false
                                }
                            }
                        }
                    }
                ) {
                    Text(if (compartiendo) "Preparando imagen..." else "Compartir QR")
                }
            }
            else -> {
                Text("No se pudo generar el QR.", color = MaterialTheme.colorScheme.error)
                Button(onClick = { intento += 1 }) { Text("Reintentar") }
            }
        }
        errorCompartir?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
