package dev.hpm.accecurity.model

import androidx.compose.ui.graphics.Color

/**
 * Modelo de un pase de acceso mostrado en listas y tarjetas.
 *
 * @property iniciales Iniciales del visitante para el avatar circular.
 * @property nombre Nombre completo del visitante.
 * @property descripcion Motivo o detalle breve de la visita.
 * @property estado Etiqueta de estado visible (p. ej. "Dentro", "Esperado").
 * @property hora Hora asociada al pase en formato legible.
 * @property colorEstado Color de fondo del badge de estado.
 * @property colorPunto Color del indicador circular dentro del badge.
 */
data class PaseData(
    val iniciales: String,
    val nombre: String,
    val descripcion: String,
    val estado: String,
    val hora: String,
    val colorEstado: Color,
    val colorPunto: Color
)
