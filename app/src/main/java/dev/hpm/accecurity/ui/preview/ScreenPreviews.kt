package dev.hpm.accecurity.ui.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.hpm.accecurity.model.TipoUsuario
import dev.hpm.accecurity.ui.screens.*
import dev.hpm.accecurity.ui.theme.ProyectoTheme

/**
 * Previews de Compose para validar pantallas sin ejecutar la app completa.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormularioLogInPreview() {
    ProyectoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FormularioLogIn()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenJefePreview() {
    ProyectoTheme {
        PerfilScreen(
            nombre = "Elena Vargas",
            iniciales = "EV",
            tipoUsuario = TipoUsuario.JEFE,
            numeroSolicitudesJefe = 5
        )
    }
}

@Preview(
    name = "Pantalla de inicio completa",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun HomeScreenAdminPreview() {
    ProyectoTheme {
        HomeScreenAdmin()
    }
}

@Preview(
    name = "Pantalla de pases",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PasesScreenAdminYResidentePreview() {
    ProyectoTheme {
        PasesScreenAdminYResidente()
    }
}

@Preview(
    name = "ActividadAdministradorPreviwe",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ActividadScreenAdminPreview() {
    ProyectoTheme {
        ActivityScreen(tipoUsuario = TipoUsuario.ADMIN)
    }
}

@Preview(
    name = "ActividadResidentePreview",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ActividadScreenResidentePreview() {
    ProyectoTheme {
        ActivityScreen(tipoUsuario = TipoUsuario.RESIDENTE)
    }
}

@Preview(
    name = "ActividadJefePreview",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ActividadScreenJefePreview() {
    ProyectoTheme {
        ActivityScreen(
            tipoUsuario = TipoUsuario.JEFE,
            numeroSolicitudesJefe = 3
        )
    }
}

@Preview(
    name = "Registro de garita",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun RegistroGaritaScreenPreview() {
    ProyectoTheme {
        RegistroGaritaScreen()
    }
}

@Preview(
    name = "Actualmente adentro - Jefe de seguridad",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ActualmenteAdentroScreenPreview() {
    ProyectoTheme {
        ActualmenteAdentroScreen(
            numeroSolicitudesJefe = 3
        )
    }
}
