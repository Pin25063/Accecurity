package dev.hpm.accecurity.feature.inside

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla de personas actualmente dentro de las instalaciones (rol jefe).
 * Alias / compatibilidad para navegación y vistas existentes.
 */
@Composable
fun ActualmenteAdentroScreen(
    modifier: Modifier = Modifier,
    numeroSolicitudesJefe: Int = 0,
    onSolicitudesClick: () -> Unit = {},
    onAdentroClick: () -> Unit = {},
    onActividadClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: InsideViewModel = viewModel()
) {
    InsideScreen(
        modifier = modifier,
        numeroSolicitudesJefe = numeroSolicitudesJefe,
        onSolicitudesClick = onSolicitudesClick,
        onAdentroClick = onAdentroClick,
        onActividadClick = onActividadClick,
        onPerfilClick = onPerfilClick,
        viewModel = viewModel
    )
}
