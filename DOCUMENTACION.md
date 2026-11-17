# Accecurity — documentación del proyecto

Guía para explicar cómo está armada la app, qué hace cada parte y cómo se conectó a Firebase. El paquete es `dev.hpm.accecurity`. La interfaz está en español. Los nombres de campos que se guardan están en inglés.

## 1. Qué es la app

Accecurity controla el acceso de visitantes en una garita. Hay cuatro roles. Cada uno entra a un grupo de pantallas distinto después del login.

| Rol en Firestore | Enum en código | Qué ve |
|---|---|---|
| `admin` | `TipoUsuario.ADMIN` | Inicio, pases, actividad, perfil. Puede enviar una solicitud de pase. |
| `residente` | `TipoUsuario.RESIDENTE` | El mismo grafo que el admin. |
| `jefe` | `TipoUsuario.JEFE` | Solicitudes, personas adentro, actividad, perfil. Aprueba o rechaza. |
| `guardia` | `TipoUsuario.GUARDIA` | Escáner QR, registro de garita, perfil. |

El rol no se deduce del correo. Firebase Authentication solo comprueba correo y contraseña. El rol está en Firestore, en el documento `users/{uid}`, campo `rol`.

## 2. Estructura de carpetas

Todo el código Kotlin vive en `app/src/main/java/dev/hpm/accecurity/`.

```
MainActivity.kt                 arranque: AppGraph + tema + navegación
appnavhost/AppNavHost.kt        grafos y destinos por rol

core/auth/                      login y lectura del rol
core/data/                      repositorio, mappers, AppGraph, mocks viejos
core/database/                  Room: entidades, DAOs, AccecurityDatabase
core/model/                     modelos de pantalla (PaseData, SolicitudData, …)
core/ui/components/             barras, tarjetas, diálogos reutilizables
core/ui/theme/                  colores y tipografía Material 3
core/ui/preview/                previews de Compose

feature/login/                  correo y contraseña
feature/pases/                  inicio del admin y lista de pases
feature/solicitudes/            bandeja del jefe
feature/inside/                 personas que están adentro
feature/activity/               historial de movimientos
feature/registro/               registro de la garita (guardia)
feature/escaner/                cámara y lectura de QR
feature/profile/                perfil y cierre de sesión
```

Cada feature sigue MVI. Casi todas tienen cuatro archivos:

- `XxxScreen`: dibuja la UI y manda eventos.
- `XxxState`: datos inmutables de esa pantalla.
- `XxxEvent`: lo que el usuario hizo (tocó, escribió, aprobó).
- `XxxViewModel`: recibe el evento, llama al repositorio y publica un estado nuevo.

La pantalla no llama a Firebase. El ViewModel no arma SQL. Eso lo hace `AccessRepository`.

## 3. Cómo arranca

`MainActivity` hace tres cosas:

1. `AppGraph.init(this)` crea una sola vez la base Room y el repositorio, y enciende la sincronización con Firebase Auth.
2. Aplica el tema `ProyectoTheme`.
3. Muestra `AppNavHost()`, que empieza en el login.

`AppGraph` es un localizador manual. No usamos Hilt ni Koin. Todos los ViewModel leen `AppGraph.repository`, así que comparten la misma base y la misma sesión de Firebase.

## 4. Navegación

La navegación es type-safe con Navigation Compose y `@Serializable`. No existe un artefacto `navigation-serialization`. Se usan estas dos librerías juntas:

- `androidx.navigation:navigation-compose:2.8.9`
- `org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3`

El plugin de Gradle es `org.jetbrains.kotlin.plugin.serialization`.

Destinos:

- `LoginDestination`
- `AdminGraph`: inicio, pases, actividad, perfil
- `JefeGraph`: solicitudes, adentro, actividad, perfil
- `GuardiaGraph`: escáner, registro, perfil

`LoginScreen` no navega. Cuando el login sale bien, el ViewModel deja una `AuthenticatedSession` en el estado. La pantalla avisa con `onLoginSuccess`. `AppNavHost` decide el grafo:

```kotlin
when (session.tipoUsuario) {
    TipoUsuario.ADMIN, TipoUsuario.RESIDENTE -> AdminGraph
    TipoUsuario.JEFE -> JefeGraph
    TipoUsuario.GUARDIA -> GuardiaGraph
}
```

Ese `navigate` hace `popUpTo` del login con `inclusive = true`, para que Atrás no regrese al formulario. Cerrar sesión vuelve a `LoginDestination` y limpia el back stack con `popUpTo(0)`.

## 5. Login y rol

### 5.1 Authentication

`AuthRepository.signIn` llama a:

```kotlin
auth.signInWithEmailAndPassword(email, password).await()
```

`await()` viene de `kotlinx-coroutines-play-services`. Convierte el `Task` de Firebase en una función `suspend`, así que no se bloquea el hilo de la interfaz.

Si el correo no existe, el mensaje es «No existe una cuenta con ese correo». Si la contraseña está mal, «Correo o contraseña incorrectos».

### 5.2 De dónde sale el rol

Después del login se lee `users/{uid}` en Firestore.

```kotlin
val role = (snapshot.getString("role") ?: snapshot.getString("rol"))
    ?.lowercase()
    ?.trim()
```

El campo que creamos en la consola se llama `rol`. El código también acepta `role` por si el documento se escribió con el nombre en inglés. `parseAssignedRole` solo acepta `admin`, `residente`, `jefe` y `guardia`.

Si el documento no existe, el rol es inválido o Firestore niega la lectura, se hace `signOut()` y el usuario se queda en el login con un mensaje en español. No se entra a ninguna pantalla sin rol.

### 5.3 Cómo se muestra el rol

`TipoUsuario.displayName()` traduce el enum a texto:

- Administrador
- Residente
- Jefe de seguridad
- Guardia de seguridad

La navegación usa el enum, no el texto. El texto es solo para la UI.

### 5.4 Cómo se crearon los usuarios

En Firebase Console, proyecto `accecurity`:

1. Authentication → Sign-in method → Email/Password → activado.
2. Authentication → Users → Add user. Ahí se crea el correo y la contraseña. Firebase no devuelve la contraseña después. Si se olvida, se cambia desde la consola. No está en el código.
3. Firestore → colección `users` → el id del documento es el UID que muestra Authentication, no el correo.
4. Campos del documento: `email` (string) y `rol` (string). Valores de `rol`: `admin`, `residente`, `jefe`, `guardia`.

Ejemplo real usado en pruebas: `guardia@accecurity.test`, rol `guardia`. La contraseña de esa cuenta de prueba es `123456`. El admin se creó como `admin@accecurity.test`; su contraseña solo está en Authentication.

No hay lista de usuarios hardcodeada en la app.

## 6. Conexión con Firebase

### 6.1 Proyecto

- Proyecto: `accecurity`
- Número de proyecto: `868862720632`
- App Android registrada con el package `dev.hpm.accecurity`
- Firestore: base `(default)`, edición Standard, ubicación `nam5`
- Productos usados: Authentication (email/contraseña) y Cloud Firestore

### 6.2 Archivo de credenciales

Las credenciales de la app están en `app/google-services.json`. Lo descarga Firebase al registrar la app Android. El plugin `com.google.gms.google-services` (versión 4.4.2) lo lee en el build y genera los recursos que usan `FirebaseAuth.getInstance()` y `FirebaseFirestore.getInstance()`.

Ese JSON contiene, entre otras cosas:

- `project_id`: `accecurity`
- `project_number`
- `mobilesdk_app_id`
- `package_name`: `dev.hpm.accecurity`
- `api_key` de la app Android

No hay archivo `.env` ni contraseñas en el código. La API key identifica al proyecto de Firebase; no es la contraseña de un usuario. Las contraseñas de las cuentas viven solo en Authentication.

Para que las llamadas salgan del teléfono, el manifest tiene:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 6.3 Dependencias de Firebase

En `gradle/libs.versions.toml`:

- BOM `com.google.firebase:firebase-bom:33.7.0`
- `firebase-auth` y `firebase-firestore` sin versión propia: la pone el BOM
- `kotlinx-coroutines-play-services:1.9.0` para `.await()`

En `app/build.gradle.kts` el plugin `google-services` se aplica en el módulo app. En el `build.gradle.kts` de la raíz está con `apply false`.

### 6.4 Reglas

Al crear la base se eligió modo de prueba, para poder leer y escribir mientras se desarrollaba. El modo de producción `allow read, write: if false` bloquea todo: el login autentica, pero falla con «No se pudo leer tu rol» porque no puede abrir `users/{uid}`.

Reglas coherentes con el código, para usuarios ya autenticados:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow write: if false;
    }
    match /pases/{passId} {
      allow read, write: if request.auth != null;
    }
    match /movimientos/{movementId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

El documento de usuario lo lee el dueño. Los pases y movimientos los lee y escribe cualquiera que haya iniciado sesión. La app no crea usuarios desde el teléfono: por eso `users` no se escribe desde el cliente.

## 7. Qué se guarda y dónde

Hay dos bases. Firestore es la fuente real, compartida entre teléfonos. Room es la copia local que pintan las pantallas.

### 7.1 Firestore

Colección `pases`. El id del documento lo genera Firestore (o se puso a mano, como `pase-001`). No hay un campo `id` adentro.

| Campo | Ejemplo | Uso |
|---|---|---|
| `visitorName` | Laura Méndez | Nombre del visitante |
| `description` | Visita familiar | Motivo |
| `schedule` | Hoy 09:00 – 12:00 o `14:30` | Horario |
| `location` | Garita principal | Dónde entra |
| `requestedBy` | Apto 402 | Quién pidió |
| `createdById` | UID de Auth | Quién lo creó |
| `status` | `PENDING` | Estado del flujo |
| `isUrgent` | `false` | Prioridad en la lista del jefe |
| `qrCode` | id del pase | Código que leería el escáner |
| `entryTime` | texto o vacío | Hora de entrada |
| `exitTime` | texto o vacío | Hora de salida |

Al leer, si el documento viejo trae nombres en español (`nombreVisitante`, `estado`, …), el repositorio los acepta como respaldo. El texto se recorta con `trim()` porque algunos valores de la consola quedaron con un salto de línea al inicio.

Estados que entiende `normalizeStatus`:

| Guardado | También acepta al leer | Pantalla |
|---|---|---|
| `PENDING` | `PENDIENTE` | Solicitudes del jefe |
| `EXPECTED` | `ESPERADO` y cualquier otro valor no listado | Esperado |
| `INSIDE` | `DENTRO` | Dentro |
| `LEFT` | `SALIO`, `SALIÓ` | Salió |
| `REJECTED` | `RECHAZADO` | No se lista en pases ni en solicitudes |

Colección `movimientos`:

| Campo | Uso |
|---|---|
| `passId` | Pase al que pertenece |
| `title` | Texto que se muestra, por ejemplo «Solicitud de acceso» |
| `description` | Detalle |
| `time` | Hora |
| `type` | `PASS_CREATED`, `ENTRY`, `EXIT`, `REJECTION` |

Al leer también se aceptan `PASE_CREADO`, `ENTRADA`, `SALIDA` y `RECHAZO`.

### 7.2 Room

Archivo local: `accecurity.db`. Versión 1. `exportSchema = false`. Se abre en `AccecurityDatabase.get(context)` con doble chequeo y `synchronized`, para que haya una sola instancia.

| Tabla Room | Entidad | De dónde sale |
|---|---|---|
| `users` | `UserEntity` (`id`, `email`, `role`) | Se guarda al iniciar sesión. `role` aquí es el nombre del enum (`JEFE`, `GUARDIA`, …). |
| `passes` | `PassEntity` | Copia de `pases` |
| `movements` | `MovementEntity` | Copia de `movimientos` |

Consultas que alimentan las pantallas (`PassDao`):

- Admin, pases visibles: `status IN ('EXPECTED', 'INSIDE', 'LEFT')`
- Jefe, solicitudes: `status = 'PENDING'`
- Jefe, adentro: `status = 'INSIDE'`
- Actividad y registro: todos los movimientos

`replaceAll` borra la tabla y vuelve a insertar dentro de una `@Transaction`, para no dejar la lista a medias. El `OnConflictStrategy.REPLACE` actualiza si el id ya existe.

Los DAO devuelven `Flow`. Cuando Room cambia, el ViewModel recibe la lista nueva y Compose se redibuja. La pantalla no hace polling.

### 7.3 Mocks

Siguen en `core/data` (`MockPases`, `MockSolicitudes`, `MockActividad`, …) de cuando las pantallas eran solo visuales. Las listas de pases, solicitudes, actividad, adentro y registro ya no los usan: observan el repositorio. El perfil todavía arma notificaciones y ayuda con datos locales de ejemplo.

## 8. Flujo de un pase

1. El admin o el residente, en Inicio, abre «Crear pase».
2. Valida nombre, motivo y hora `HH:mm` (00:00 a 23:59).
3. `createAccessRequest` crea un documento en `pases` con `status = PENDING` y otro en `movimientos` con título «Solicitud de acceso» y tipo `PASS_CREATED`.
4. El botón pasa a «Enviando...» y no acepta otro toque. El trabajo corre en `Dispatchers.IO`.
5. El pase no aparece en Esperados. Aparece en Solicitudes del jefe.
6. El jefe aprueba: el estado pasa a `EXPECTED` y se agrega el movimiento «Solicitud aprobada». Entonces el admin lo ve como Esperado.
7. El jefe rechaza: el estado pasa a `REJECTED`, movimiento «Solicitud rechazada» con tipo `REJECTION`, y sale de la bandeja.

Al crear o al cambiar el estado, el repositorio vuelve a bajar las dos colecciones y reemplaza Room. Además, con la sesión abierta, un `addSnapshotListener` de `pases` escribe los cambios en Room en un `CoroutineScope` de `Dispatchers.IO`. Un `Mutex` evita que el refresh y el listener borren la tabla al mismo tiempo.

Por eso el jefe, al abrir Solicitudes, llama `refresh()` en segundo plano y además recibe lo que el listener vaya trayendo. Si la lectura falla, el mensaje es «No se pudieron cargar las solicitudes».

El pase de ejemplo Laura Méndez quedó en Firestore como `ESPERADO`. La app lo normaliza a `EXPECTED`, así que se ve en los pases del admin, no en las solicitudes del jefe. Una solicitud nueva sí entra como `PENDING`.

## 9. Hilos

La UI de Compose corre en el hilo principal. Red, Firestore y Room no se hacen ahí.

- Los ViewModel lanzan el guardado, el refresh y el login con `viewModelScope.launch(Dispatchers.IO)`.
- El repositorio envuelve `refresh`, `createAccessRequest`, `updatePassStatus` y `saveSession` en `withContext(Dispatchers.IO)`.
- Al terminar, el estado de la pantalla se actualiza con `withContext(Dispatchers.Main)`.
- El listener de Firestore entrega el snapshot y el `syncScope` (también en `Dispatchers.IO`) escribe Room.
- `StateFlow.update` publica el estado. Compose lo recoge con `collectAsStateWithLifecycle()`.

`Dispatchers.IO` es un pool de hilos de fondo. La corrutina se suspende en `.await()`; no se usa `Thread.sleep` ni se bloquea el hilo principal esperando la red.

## 10. Qué hace cada pantalla

| Pantalla | Rol | Datos |
|---|---|---|
| Login | Todos | Auth + documento `users/{uid}` |
| Inicio admin | Admin y residente | Conteos y filtros de pases visibles. Aquí se envía la solicitud. |
| Mis pases | Admin y residente | Misma lista visible, sin el formulario |
| Solicitudes | Jefe | Solo `PENDING`. Aprobar o rechazar. |
| Adentro | Jefe | Solo `INSIDE` |
| Actividad | Admin y jefe | Movimientos |
| Escáner | Guardia | Cámara (CameraX) y ML Kit. Muestra el texto del QR. Todavía no busca el pase en la base ni registra entrada o salida. |
| Registro | Guardia | Los mismos movimientos que Actividad |
| Perfil | Todos | Datos de presentación. Cerrar sesión llama `FirebaseAuth.signOut()`. Parte del contenido sigue siendo de ejemplo. |

Filtros del inicio: Todos, Esperados (`Esperado`), Adentro (`Dentro`), Salieron (`Salió`). Esos textos salen de `AccessMappers.statusColors`, no se guardan así en la base.

## 11. Librerías y para qué están

Definidas en `gradle/libs.versions.toml` y aplicadas en `app/build.gradle.kts`.

| Librería | Versión | Para qué |
|---|---|---|
| Android Gradle Plugin | 9.2.1 | Compilar el módulo app |
| Kotlin | 2.2.10 | Lenguaje |
| Jetpack Compose BOM | 2026.02.01 | UI declarativa |
| Material 3 | vía BOM | Componentes y tema |
| Activity Compose | 1.13.0 | `setContent` en la Activity |
| Navigation Compose | 2.8.9 | Grafos y destinos |
| kotlinx-serialization-json | 1.7.3 | Destinos `@Serializable` |
| Lifecycle ViewModel / runtime Compose | 2.11.0 | ViewModel y `collectAsStateWithLifecycle` |
| Firebase BOM | 33.7.0 | Alinea Auth y Firestore |
| firebase-auth | BOM | Correo y contraseña |
| firebase-firestore | BOM | Colecciones |
| coroutines-play-services | 1.9.0 | `Task.await()` |
| Room runtime + ktx | 2.7.2 | Base local y `Flow` |
| Room compiler + KSP | Room 2.7.2, KSP 2.2.10-2.0.2 | Genera el código de los DAO |
| CameraX | 1.4.1 | Vista de la cámara del guardia |
| ML Kit barcode-scanning | 17.3.0 | Leer el QR |
| core-ktx | 1.19.0 | Extensiones de Android |
| Material icons extended | BOM | Íconos |

SDK de la app: `minSdk 26`, `targetSdk 36`, `compileSdk` 37. Java 11. `applicationId` `dev.hpm.accecurity`.

Instrucción de Gradle que hizo falta para KSP con AGP 9, en `gradle.properties`:

```
android.disallowKotlinSourceSets=false
```

Sin esa línea, KSP falla al generar Room porque AGP 9 trae Kotlin integrado y no deja agregar source sets de Kotlin a mano.

Otra instrucción útil de compilación:

```
./gradlew compileDebugKotlin
```

En Windows: `.\gradlew.bat compileDebugKotlin`.

## 12. Permisos

- `INTERNET`: Firebase.
- `CAMERA`: escáner. En el manifest la cámara no es obligatoria (`required="false"`), para que un emulador sin cámara igual instale la app. Si el usuario niega el permiso, el escáner muestra que hace falta la cámara.

## 13. Respuestas cortas si preguntan

**¿Dónde está la lógica de negocio?** En `AccessRepository` y en los ViewModel. Las pantallas solo dibujan y emiten eventos.

**¿Por qué Room y también Firestore?** Firestore es la base compartida. Room es la caché del teléfono. Las pantallas observan Room. El repositorio llena Room desde Firestore.

**¿Cómo sabe la app qué pantalla abrir?** Lee `users/{uid}.rol`, lo convierte a `TipoUsuario` y `AppNavHost` elige el grafo.

**¿El correo decide el rol?** No. Dos pasos: Authentication valida la clave, Firestore dice el rol.

**¿Dónde están las credenciales?** El archivo `app/google-services.json` identifica el proyecto. Las contraseñas están en Firebase Authentication, no en el repositorio. El rol está en Firestore.

**¿Qué pasa si creo un pase?** Se guarda `PENDING`. El jefe lo ve en Solicitudes. Si lo aprueba, pasa a `EXPECTED` y el admin lo ve como Esperado.

**¿Por qué la UI no se congela al guardar?** Porque la red y Room corren en `Dispatchers.IO`. El hilo principal solo actualiza el estado al final.

**¿Qué no está terminado?** El escáner lee el código y lo muestra, pero no cambia el estado del pase a `INSIDE` o `LEFT`. El perfil todavía usa textos de ejemplo para notificaciones y ayuda.
