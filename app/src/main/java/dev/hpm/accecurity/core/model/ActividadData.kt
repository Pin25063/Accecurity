package dev.hpm.accecurity.core.model

/**
 * Tipo de movimiento mostrado en el registro de actividad.
 */
enum class TipoActividad {
    ENTRADA,
    PASE_CREADO,
    SALIDA,
    RECHAZO
}

/**
 * Modelo de un movimiento del registro de actividad.
 *
 * @property id Identificador único del movimiento.
 * @property titulo Descripción principal del movimiento.
 * @property descripcion Información adicional del registro.
 * @property hora Hora en formato legible.
 * @property tipo Tipo de movimiento para su representación visual.
 */
data class ActividadData(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val hora: String,
    val tipo: TipoActividad
)