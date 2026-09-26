package dev.hpm.accecurity.model

/**
 * Modelo de una solicitud de acceso pendiente de aprobación.
 *
 * @property nombre Nombre del visitante o grupo solicitante.
 * @property descripcion Motivo o detalle breve de la visita.
 * @property horario Rango horario de la visita.
 * @property ubicacion Punto de acceso o garita asignada.
 * @property solicitadoPor Origen de la solicitud (apartamento, oficina, etc.).
 * @property esUrgente Indica si la solicitud requiere atención prioritaria.
 */
data class SolicitudData(
    val nombre: String,
    val descripcion: String,
    val horario: String,
    val ubicacion: String,
    val solicitadoPor: String,
    val esUrgente: Boolean = false
)
