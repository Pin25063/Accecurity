package dev.hpm.accecurity.core.ui.components.escaner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hpm.accecurity.core.model.PersonaAdentroData

@Composable
fun RegistroSalidaDialog(
    personas: List<PersonaAdentroData>,
    busqueda: String,
    seleccionada: PersonaAdentroData?,
    cargando: Boolean,
    registrando: Boolean,
    errorLista: String?,
    mensaje: String?,
    salidaRegistrada: Boolean,
    onBuscar: (String) -> Unit,
    onSeleccionar: (PersonaAdentroData) -> Unit,
    onReintentar: () -> Unit,
    onVolver: () -> Unit,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit
) {
    val filtradas = personas.filter {
        it.nombre.contains(busqueda.trim(), ignoreCase = true) ||
                it.destino.contains(busqueda.trim(), ignoreCase = true)
    }
    AlertDialog(
        onDismissRequest = { if (!registrando) onCerrar() },
        title = {
            Text(when {
                salidaRegistrada -> "Salida registrada"
                seleccionada != null -> "Confirmar salida"
                else -> "Registrar salida"
            })
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    seleccionada != null -> {
                        Text(seleccionada.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("Motivo: ${seleccionada.descripcion.ifBlank { "Sin motivo registrado" }}")
                        Text("Destino: ${seleccionada.destino.ifBlank { "Sin destino registrado" }}")
                        Text("Entrada: ${seleccionada.horaEntrada}")
                        if (!salidaRegistrada && !registrando) Text("¿Confirmás que el visitante se está retirando?")
                        if (registrando) CircularProgressIndicator()
                    }
                    cargando -> {
                        CircularProgressIndicator()
                        Text("Cargando visitantes adentro...")
                    }
                    errorLista != null -> {
                        Text(errorLista, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onReintentar) { Text("Reintentar") }
                    }
                    personas.isEmpty() -> Text("No hay visitantes adentro.")
                    else -> {
                        OutlinedTextField(
                            value = busqueda,
                            onValueChange = onBuscar,
                            label = { Text("Buscar por nombre o destino") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Text("Seleccioná al visitante que se retira.")
                        if (filtradas.isEmpty()) Text("No hay coincidencias para esa búsqueda.")
                        filtradas.forEach { persona ->
                            OutlinedButton(
                                onClick = { onSeleccionar(persona) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(persona.nombre, style = MaterialTheme.typography.titleSmall)
                                    Text(persona.destino.ifBlank { "Sin destino registrado" })
                                    Text("Entrada: ${persona.horaEntrada}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                mensaje?.let {
                    Text(it, color = if (salidaRegistrada) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            if (seleccionada != null && !salidaRegistrada) {
                Button(onClick = onConfirmar, enabled = !registrando && !cargando) {
                    Text(if (registrando) "Registrando..." else "Confirmar salida")
                }
            } else {
                TextButton(onClick = onCerrar, enabled = !registrando) { Text("Cerrar") }
            }
        },
        dismissButton = {
            if (seleccionada != null && !salidaRegistrada) {
                TextButton(onClick = onVolver, enabled = !registrando) { Text("Volver") }
            }
        }
    )
}
