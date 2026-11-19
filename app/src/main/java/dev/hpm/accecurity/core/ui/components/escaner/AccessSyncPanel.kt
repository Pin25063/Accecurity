package dev.hpm.accecurity.core.ui.components.escaner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hpm.accecurity.core.model.AccessSyncStatus

@Composable
fun AccessSyncPanel(status: AccessSyncStatus, onSync: () -> Unit, onRetryConflicts: () -> Unit) {
    var showIssues by rememberSaveable { mutableStateOf(false) }
    if (showIssues) {
        AlertDialog(
            onDismissRequest = { showIssues = false },
            title = { Text("Movimientos por revisar") },
            text = {
                Column(modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (status.issues.isEmpty()) Text("Ya no hay movimientos por revisar.")
                    status.issues.forEach { Text(it) }
                }
            },
            confirmButton = {
                if (status.issues.isNotEmpty()) {
                    TextButton(enabled = status.online && !status.syncing, onClick = {
                        onRetryConflicts()
                        showIssues = false
                    }) { Text("Reintentar") }
                }
            },
            dismissButton = { TextButton(onClick = { showIssues = false }) { Text("Cerrar") } }
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = when {
                    status.syncing -> "Sincronizando movimientos..."
                    status.pending == 1 -> "1 movimiento pendiente de sincronizar"
                    status.pending > 1 -> "${status.pending} movimientos pendientes de sincronizar"
                    else -> "Sin movimientos pendientes"
                },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall
            )
            if (status.pending > 0) {
                TextButton(onClick = onSync, enabled = status.online && !status.syncing) { Text("Sincronizar") }
            }
        }
        if (!status.online) Text("Sin conexión: usá pases descargados previamente.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (status.conflicts > 0) {
            TextButton(onClick = { showIssues = true }) {
                Text("${status.conflicts} por revisar", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
