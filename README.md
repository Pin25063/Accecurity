# Accecurity

Accecurity es una aplicación android diseñada para el control de acceso y gestión de visitantes en colegios y conjuntos residenciales. Su objetivo es optimizar y asegurar el registro de entradas y salidas, permitiendo a residentes, administradores, guardias y jefes de seguridad operar en un entorno interconectado y en tiempo real.
## Servicios
* **Firebase Authentication:** Se utiliza para gestionar el inicio de sesión de los usuarios de forma segura mediante correo electrónico y contraseña.
* **Cloud Firestore:** Base de datos en la nube utilizada para almacenar y consultar el rol asignado a cada usuario autenticado (admin, residente, jefe, guardia) y definir sus permisos de navegación.
* **Datos Locales (Fake Data):** Para cumplir con el requerimiento de la fase de desarrollo actual, la información de pases, solicitudes y registro de actividad se extrae de orígenes de datos locales estáticos (ej. `obtenerPasesFalsos()`, `obtenerSolicitudesFalsas()`). Esto simula la comunicación con una API externa para tener la app 100% navegable.

## Librerías
* **Jetpack Compose & Material 3:** Kit de herramientas principal para construir la interfaz de usuario de forma declarativa.
* **Navigation Compose (Type-Safe):** Se utiliza en conjunto con `kotlinx.serialization` para manejar el enrutamiento interno de la aplicación basándose en objetos y clases seguras, evitando el uso de strings.
* **ViewModel & StateFlow:** Componentes de la arquitectura de Android implementados para cumplir con el patrón MVI, manejando el estado de la UI y haciéndola reactiva a través de `collectAsStateWithLifecycle()`.
* **Firebase (Auth & Firestore):** SDKs oficiales de Google para la validación de credenciales y consulta de documentos remotos.