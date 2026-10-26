package dev.hpm.accecurity.core.data

import dev.hpm.accecurity.core.model.SolicitudData

/**
 * Datos QUeMADOS de solicitudes de acceso para desarrollo y previews.
 *
 */
fun obtenerSolicitudesFalsas(): List<SolicitudData> = listOf(
    SolicitudData(
        id = "solicitud-001",
        nombre = "Grupo Aseo S.A.",
        descripcion = "Equipo de limpieza · 3 personas",
        horario = "Hoy 14:00 – 17:00",
        ubicacion = "Garita de servicio",
        solicitadoPor = "Solicitado por Apto 402 - C. Herrera",
        esUrgente = true
    ),
    SolicitudData(
        id = "solicitud-002",
        nombre = "Mariana López",
        descripcion = "Docente suplente",
        horario = "Hoy 07:30 – 13:00",
        ubicacion = "Garita principal",
        solicitadoPor = "Solicitado por Oficina del colegio",
        esUrgente = false
    ),
    SolicitudData(
        id = "solicitud-003",
        nombre = "Soporte Técnico",
        descripcion = "Instalación de internet",
        horario = "Mañana 09:00 – 11:00",
        ubicacion = "Garita principal",
        solicitadoPor = "Solicitado por Administración",
        esUrgente = false
    )
)

/** Cantidad de solicitudes pendientes mostrada en el badge del encabezado. */
fun contarSolicitudesPendientes(): Int = obtenerSolicitudesFalsas().size
