package dev.hpm.accecurity.core.model

/**
 * Modelo de un visitante que permanece dentro de las instalaciones.
 *
 * @property id Identificador único del visitante en el registro.
 * @property nombre Nombre del visitante.
 * @property descripcion Motivo de la visita.
 * @property destino Lugar al que se dirige.
 * @property horaEntrada Hora de ingreso en formato legible.
 */
data class PersonaAdentroData(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val destino: String,
    val horaEntrada: String
)