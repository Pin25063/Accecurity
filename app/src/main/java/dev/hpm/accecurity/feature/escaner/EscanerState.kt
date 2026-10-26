package dev.hpm.accecurity.feature.escaner

/**
 * Estado inmutable de la funcionalidad del Escáner de garita.
 */
data class EscanerState(
    val estaEscaneando: Boolean = false,
    val ultimoCodigoEscaneado: String? = null
)
