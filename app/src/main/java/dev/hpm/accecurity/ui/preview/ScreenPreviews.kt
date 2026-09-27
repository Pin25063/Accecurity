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

@Preview(
    name = "Perfil - Jefe de seguridad",
    showBackground = true,
    showSystemUi = true
)
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
    name = "Perfil - Administrador",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PerfilScreenAdminPreview() {
    ProyectoTheme {
        PerfilScreen(
            nombre = "Carlos Mendoza",
            iniciales = "CM",
            tipoUsuario = TipoUsuario.ADMIN
        )
    }
}

@Preview(
    name = "Perfil - Residente",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PerfilScreenResidentePreview() {
    ProyectoTheme {
        PerfilScreen(
            nombre = "Ana Morales",
            iniciales = "AM",
            tipoUsuario = TipoUsuario.RESIDENTE
        )
    }
}

@Preview(
    name = "Perfil - Guardia",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PerfilScreenGuardiaPreview() {
    ProyectoTheme {
        PerfilScreen(
            nombre = "Roberto Gómez",
            iniciales = "RG",
            tipoUsuario = TipoUsuario.GUARDIA
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
    name = "ActividadAdministradorPreview",
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

@Preview(
    name = "Escáner de garita - Guardia",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun EscanerScreenGuardiaPreview() {
    ProyectoTheme {
        EscanerScreenGuardia()
    }
}

@Preview(
    name = "Solicitudes de acceso - Jefe",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SolicitudesScreenJefePreview() {
    ProyectoTheme {
        SolicitudesScreenJefe()
    }
}
