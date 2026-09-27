# Accecurity — Entrega: Vistas en Jetpack Compose

App de control de acceso y visitantes para colegios y conjuntos residenciales. Proyecto de Programación de Plataformas Móviles, Universidad del Valle de Guatemala. Esta entrega implementa **todas las vistas a nivel visual**: Kotlin + Jetpack Compose + Material 3, con fuente de datos falsa (`MockPases`) y sin backend.

## Funcionalidades

Se implementan las siguientes pantallas y funcionalidades base, adaptando la navegación inferior (Bottom Navigation) según el rol del usuario (Admin, Residente, Guardia, Jefe de seguridad):

| Funcionalidad | Pantallas |
| :--- | :--- |
| **Autenticación** | Login |
| **Dashboard y Estadísticas** | Inicio (Admin y Residente) |
| **Gestión de Pases** | Mis Pases (Invitaciones activas y recientes) |
| **Monitoreo en Vivo** | Rastreo con filtros de estado (Esperados, Adentro, Salieron) |
| **Perfiles por Rol** | Perfil (Adapta la UI y muestra credenciales/notificaciones) |

*Nota: Quedan para fases posteriores el backend, el inicio de sesión real, la base de datos local y el escáner de códigos QR funcional.*

## Estructura del Proyecto

```text
app/src/main/java/dev/hpm/accecurity/
├── MainActivity.kt                 # Punto de entrada de la aplicación y tema principal
├── data/
│   └── MockPases.kt                # Datos temporales para las listas LazyColumn
├── model/
│   ├── PaseData.kt                 # Data class de la tarjeta del pase digital
│   └── TipoUsuario.kt              # Enum con roles (ADMIN, RESIDENTE, GUARDIA, JEFE)
└── ui/
    ├── components/                 # TopBars, BottomNavBars y PassComponents reutilizables
    ├── preview/                    # ScreenPreviews para validación de vistas sin emulador
    ├── screens/                    # Pantallas (HomeScreenAdmin, LoginScreen, PasesScreen, etc.)
    └── theme/                      # Configuración de Material 3 (Color, Theme, Type)
