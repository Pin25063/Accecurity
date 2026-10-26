package dev.hpm.accecurity.core.data

/**
 * Datos QUEMADOS de notificaciones para desarrollo y previews.
 */
fun obtenerNotificacionesFalsas(): List<String> = listOf(
    "Bienvenido a Accecurity.",
    "Consulta tus opciones desde el menú inferior."
)

/**
 * Información de ayuda para el uso de la aplicación.
 */
fun obtenerAyudaPerfil(): String =
    "Utiliza el menú inferior para acceder a las funciones de tu rol.\n\n" +
            "En Inicio puedes crear y consultar pases. " +
            "El jefe de seguridad puede revisar solicitudes y personas adentro. " +
            "El guardia puede acceder al escáner y al registro de garita.\n\n" +
            "Si necesitas asistencia, comunícate con la administración " +
            "de tu colegio o conjunto residencial."