package dev.hpm.accecurity.feature.escaner

/**
 * Estado inmutable de la funcionalidad del Escáner de garita bajo arquitectura MVI.
 */
data class EscanerState(
    val estaEscaneando: Boolean = true,
    val ultimoCodigoEscaneado: String? = null,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val tienePermisoCamara: Boolean = false
)
