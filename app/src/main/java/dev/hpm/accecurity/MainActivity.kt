package dev.hpm.accecurity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.hpm.accecurity.ui.theme.ProyectoTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import dev.hpm.accecurity.ui.theme.StatusExpected
import dev.hpm.accecurity.ui.theme.StatusExpectedContainer
import dev.hpm.accecurity.ui.theme.StatusInside
import dev.hpm.accecurity.ui.theme.StatusInsideContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FormularioLogIn()
                }
            }
        }
    }
}

//Variables temporales

enum class TipoUsuario {
    ADMIN, RESIDENTE, GUARDIA, JEFE
}

data class PaseData(
    val iniciales: String,
    val nombre: String,
    val descripcion: String,
    val estado: String,
    val hora: String,
    val colorEstado: Color,
    val colorPunto: Color
)

fun obtenerPasesFalsos(): List<PaseData> {
    return listOf(
        PaseData("LM", "Laura Méndez", "Visita familiar · Apto 4...", "Dentro", "09:12", StatusInsideContainer, StatusInside),
        PaseData("MD", "Mensajero DHL", "Entrega de paquete · ...", "Dentro", "09:40", StatusInsideContainer, StatusInside),
        PaseData("DS", "Diego Salas", "Mantenimiento · Ap...", "Esperado", "11:00", StatusExpectedContainer, StatusExpected),
        PaseData("AB", "Ana Beltrán", "Reunión de padres ·...", "Esperado", "12:30", StatusExpectedContainer, StatusExpected)
    )
}

//Pantallas Completas

@Composable
fun FormularioLogIn(modifier: Modifier = Modifier) {
    // Variables de estado para los campos de texto
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            // Esto evita que el contenido se ponga debajo de la barra de estado/navegacion
            .windowInsetsPadding(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Icono superior tipo escudo
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = "Icono de seguridad",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título Principal
        Text(
            text = "Accecurity",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtítulo
        Text(
            text = "Control de acceso y visitantes para\ncolegios y conjuntos residenciales.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Campo de Correo Electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), //Cambia el teclado virtual a uno con el @
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true //Al dar enter, hace que vaya a lo siguiente
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), //Pone los *****
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //Desactiva autocorrector y texto predictivo
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        // Spacer con peso para empujar el boton y el footer hacia el final de la pantalla
        Spacer(modifier = Modifier.weight(1f))

        // Boton de Iniciar Sesion
        Button(
            onClick = { }, //Logica de inicio de sesion
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(
                text = "Iniciar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
fun HomeScreenAdmin() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarInicioAdminYResidente() },
        bottomBar = { BottomNavigationBarAdminYResidente(true,false,false,false) }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                BotonCrearPase(onClick = { }) //Logica de crear pase
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CuadroEstadistica(
                        titulo = "2",
                        subtitulo = "Esperados",
                        colorFondo = StatusExpectedContainer,
                        modifier = Modifier.weight(1f)
                    )
                    CuadroEstadistica(
                        titulo = "2",
                        subtitulo = "Adentro",
                        colorFondo = StatusInsideContainer,
                        modifier = Modifier.weight(1f)
                    )
                    CuadroEstadistica(
                        titulo = "1",
                        subtitulo = "Salieron",
                        colorFondo = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rastreo en vivo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FiltroChip(texto = "Todos", seleccionado = true)
                        FiltroChip(texto = "Esperados", seleccionado = false)
                        FiltroChip(texto = "Adentro", seleccionado = false)
                        FiltroChip(texto = "Salieron", seleccionado = false)
                    }
                }
            }

            items(obtenerPasesFalsos()) { pase ->
                PaseItem(pase = pase)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun PasesScreenAdminYResidente(modifier: Modifier = Modifier) {
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
                Column {
                    Text(
                        text = "Mis pases",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Invitaciones activas y recientes",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        bottomBar = { BottomNavigationBarAdminYResidente(false,true,false,false) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(obtenerPasesFalsos()) { pase ->
                PaseItem(pase = pase)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun PerfilScreen( nombre: String, iniciales: String, tipoUsuario: TipoUsuario, numeroSolicitudesJefe: Int = 0) { //Opcional

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarPerfil() },
        bottomBar = {
            // Se muestra la barra dependiendo del rol
            when (tipoUsuario) {
                TipoUsuario.ADMIN, TipoUsuario.RESIDENTE -> {
                    BottomNavigationBarAdminYResidente(
                        inicio = false, pases = false, actividad = false, perfil = true
                    )
                }
                TipoUsuario.GUARDIA -> {
                    BottomNavigationBarGuardia(
                        escaner = false, registro = false, perfil = true
                    )
                }
                TipoUsuario.JEFE -> {
                    BottomNavigationBarJefe(
                        solicitudes = false, adentro = false, actividad = false, perfil = true, numeroSolicitudes = numeroSolicitudesJefe
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), // Permite hacer scroll
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TarjetaInformacionUsuario(
                iniciales = iniciales,
                nombre = nombre,
                tipoUsuario = tipoUsuario
            )

            MenuPerfil()

            BotonCerrarSesion(onClick = { /* Lógica de cerrar sesión */ })

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

//Top Bars

@Composable
fun TopBarInicioAdminYResidente() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Buenos días, Sofía",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Colegio Seminario",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = { }) { //Logica de Notificaciones
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(28.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .size(8.dp)
                    .background(Color.Red, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.background, CircleShape)
            )
        }
    }
}

@Composable
fun TopBarPerfil() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Perfil",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

//Bottom Navigation Bars

@Composable
fun BottomNavigationBarAdminYResidente(inicio: Boolean, pases: Boolean, actividad: Boolean, perfil: Boolean) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = inicio,
            onClick = { },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = pases,
            onClick = { },
            icon = { Icon(Icons.Outlined.ConfirmationNumber, contentDescription = "Pases") },
            label = { Text("Pases", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = actividad,
            onClick = { },
            icon = { Icon(Icons.Outlined.Timeline, contentDescription = "Actividad") },
            label = { Text("Actividad", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = perfil,
            onClick = { },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp) }
        )
    }
}

@Composable
fun BottomNavigationBarGuardia(escaner: Boolean, registro: Boolean, perfil: Boolean) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = escaner,
            onClick = { },
            icon = { Icon(if (escaner) Icons.Filled.QrCodeScanner else Icons.Outlined.QrCodeScanner, contentDescription = "Escáner") },
            label = { Text("Escáner", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = registro,
            onClick = { },
            // Puedes usar FactCheck o Checklist dependiendo de cuáles iconos tengas importados
            icon = { Icon(if (registro) Icons.Filled.FactCheck else Icons.Outlined.FactCheck, contentDescription = "Registro") },
            label = { Text("Registro", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = perfil,
            onClick = { },
            icon = { Icon(if (perfil) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun BottomNavigationBarJefe( solicitudes: Boolean, adentro: Boolean, actividad: Boolean, perfil: Boolean, numeroSolicitudes: Int) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = solicitudes,
            onClick = { },
            icon = {
                BadgedBox(
                    badge = {
                        if (numeroSolicitudes > 0) {
                            Badge(
                                containerColor = Color(0xFFD32F2F),
                                contentColor = Color.White
                            ) {
                                Text(numeroSolicitudes.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (solicitudes) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                        contentDescription = "Solicitudes"
                    )
                }
            },
            label = { Text("Solicitudes", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = adentro,
            onClick = { },
            icon = { Icon(if (adentro) Icons.Filled.MeetingRoom else Icons.Outlined.MeetingRoom, contentDescription = "Adentro") },
            label = { Text("Adentro", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = actividad,
            onClick = { },
            icon = { Icon(if (actividad) Icons.Filled.Timeline else Icons.Outlined.Timeline, contentDescription = "Actividad") },
            label = { Text("Actividad", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = perfil,
            onClick = { },
            icon = { Icon(if (perfil) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

//Botones

@Composable
fun BotonCrearPase(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Crear pase digital",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Invita a alguien en menos de\nun minuto",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Escanear QR",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun CuadroEstadistica(titulo: String, subtitulo: String, colorFondo: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1.2f)
            .background(colorFondo, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun FiltroChip(texto: String, seleccionado: Boolean) {
    val backgroundColor = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { }, //Pendiente aplicar filtro
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            color = textColor,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun PaseItem(pase: PaseData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .clickable { } //Cambio a detalles del pase
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pase.iniciales,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pase.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = pase.descripcion,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(
                modifier = Modifier
                    .background(pase.colorEstado, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(pase.colorPunto, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pase.estado,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = pase.hora, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalles",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

//Visuales

@Composable
fun TarjetaInformacionUsuario(iniciales: String, nombre: String, tipoUsuario: TipoUsuario) {

    val nombreRol = when (tipoUsuario) {
        TipoUsuario.ADMIN -> "Administrador"
        TipoUsuario.RESIDENTE -> "Residente"
        TipoUsuario.GUARDIA -> "Guardia de seguridad"
        TipoUsuario.JEFE -> "Jefe de seguridad"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Círculo con iniciales
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            Text(
                text = nombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badge del rol
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = "Rol",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = nombreRol,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun OpcionMenu(icono: ImageVector, texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Acción del menú */ }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = texto,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = texto,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Ir",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MenuPerfil() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
    ) {
        OpcionMenu(icono = Icons.Outlined.Notifications, texto = "Notificaciones")
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        OpcionMenu(icono = Icons.Outlined.Settings, texto = "Preferencias")
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        OpcionMenu(icono = Icons.Outlined.HelpOutline, texto = "Ayuda y soporte")
    }
}

@Composable
fun BotonCerrarSesion(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFD32F2F),
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.ExitToApp,
            contentDescription = "Cerrar sesión",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Cerrar sesión",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

//Previews

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
    name = "Pantalla de Inicio Completa",
    showBackground = true,
    showSystemUi = true, // Muestra las barras de estado y navegación simuladas
)
@Composable
fun HomeScreenAdminPreview() {
    ProyectoTheme() {
        HomeScreenAdmin()
    }
}

@Preview(
    name = "Pantalla de Inicio Completa",
    showBackground = true,
    showSystemUi = true, // Muestra las barras de estado y navegación simuladas
)
@Composable
fun PasesScreenAdminYResidentePreview() {
    ProyectoTheme() {
        PasesScreenAdminYResidente()
    }
}