package dev.hpm.accecurity.core.ui.components.escaner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hpm.accecurity.core.model.ScannedPass
import dev.hpm.accecurity.core.model.displayVisitWindow

@Composable
fun ResultadoEscaneoDialog(
    pase: ScannedPass?,
    consultando: Boolean,
    registrando: Boolean,
    mensaje: String?,
    entradaRegistrada: Boolean,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit
) {
    val puedeConfirmar = pase != null && !consultando && !registrando && !entradaRegistrada
    AlertDialog(
        onDismissRequest = { if (!registrando) onCerrar() },
        title = {
            Text(when {
                consultando -> "Consultando pase"
                registrando -> "Registrando entrada"
                entradaRegistrada -> "Entrada registrada"
                pase != null -> "Confirmar ingreso"
                else -> "Ingreso no autorizado"
            })
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (consultando || registrando) CircularProgressIndicator()
                pase?.let {
                    Text(it.visitorName, style = MaterialTheme.typography.titleMedium)
                    Text("Motivo: ${it.description.ifBlank { "Sin motivo registrado" }}")
                    Text("Destino: ${it.location.ifBlank { "Sin destino registrado" }}")
                    Text("Solicitado por: ${it.requestedBy.ifBlank { "No registrado" }}")
                    Text("Ingreso: ${displayVisitWindow(it.visitDate, it.startTime, it.endTime, "Sin horario")}")
                }
                mensaje?.let {
                    Text(it, color = if (entradaRegistrada) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error)
                }
                if (puedeConfirmar) Text("Verificá los datos del visitante antes de confirmar.")
            }
        },
        confirmButton = {
            if (pase != null && !entradaRegistrada) {
                Button(onClick = onConfirmar, enabled = puedeConfirmar) {
                    Text(if (registrando) "Registrando..." else "Confirmar entrada")
                }
            } else {
                TextButton(onClick = onCerrar, enabled = !registrando) { Text("Cerrar") }
            }
        },
        dismissButton = {
            if (pase != null && !entradaRegistrada) {
                TextButton(onClick = onCerrar, enabled = !registrando) { Text("Cancelar") }
            }
        }
    )
}
