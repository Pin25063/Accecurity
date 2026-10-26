# Accecurity

Accecurity es una aplicación android diseñada para el control de acceso y gestión de visitantes en colegios y conjuntos residenciales. Su objetivo es optimizar y asegurar el registro de entradas y salidas, permitiendo a residentes, administradores, guardias y jefes de seguridad operar en un entorno interconectado y en tiempo real.
## Servicios
* **Firebase Authentication:** Se utiliza para gestionar el inicio de sesión de los usuarios de forma segura mediante correo electrónico y contraseña.
* **Cloud Firestore:** Base de datos en la nube utilizada para almacenar y consultar el rol asignado a cada usuario autenticado (admin, residente, jefe, guardia) y definir sus permisos de navegación.
* **Datos Locales (Fake Data):** Fuentes de datos estáticos desarrolladas por el equipo dentro de `core/data`. Proporcionan información de pases, solicitudes, actividad, personas adentro, registro de garita y notificaciones mediante funciones como `obtenerPasesFalsos()`, `obtenerSolicitudesFalsas()` y `obtenerRegistroGaritaFalso()`. Esto simula la comunicación con una API externa para tener la app 100% navegable.

## Librerías
* **Jetpack Compose & Material 3:** Kit de herramientas principal para construir la interfaz de usuario de forma declarativa.
* **Navigation Compose (Type-Safe):** Se utiliza en conjunto con `kotlinx.serialization` para manejar el enrutamiento interno de la aplicación basándose en objetos y clases seguras, evitando el uso de strings.
* **Firebase (Auth & Firestore):** SDKs oficiales de Google para la validación de credenciales y consulta de documentos remotos.
* **Material Icons Extended:** Proporciona los iconos utilizados en las pantallas y componentes de la aplicación.
* **Kotlinx Serialization:** Permite serializar los destinos utilizados por la navegación type safe.
* **ViewModel:** Gestiona el estado y procesa los eventos de las funcionalidades refactorizadas siguiendo el patrón MVI.
* **Kotlin Coroutines y StateFlow:** Permiten ejecutar operaciones asíncronas y exponer el estado observable de las pantallas.
* **Lifecycle Runtime Compose:** Permite observar el estado con `collectAsStateWithLifecycle()`, respetando el ciclo de vida de la pantalla.
* **Kotlinx Coroutines Play Services:** Permite esperar los resultados de las operaciones de Firebase mediante `await()` dentro de corrutinas.

### Credenciales de prueba

Las credenciales de acceso para los roles de administrador, jefe de seguridad y guardia se incluyen en los comentarios de la entrega en Canvas.