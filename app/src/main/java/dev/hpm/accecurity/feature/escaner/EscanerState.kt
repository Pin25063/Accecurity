package dev.hpm.accecurity.feature.escaner

import dev.hpm.accecurity.core.model.PersonaAdentroData
import dev.hpm.accecurity.core.model.ScannedPass

data class EscanerState(
    val estaEscaneando: Boolean = true,
    val ultimoCodigoEscaneado: String? = null,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val tienePermisoCamara: Boolean = false,
    val mostrarResultado: Boolean = false,
    val consultaEnCurso: Boolean = false,
    val registroEnCurso: Boolean = false,
    val paseValidado: ScannedPass? = null,
    val mensajeResultado: String? = null,
    val entradaRegistrada: Boolean = false,
    val mostrarSalida: Boolean = false,
    val personasAdentro: List<PersonaAdentroData> = emptyList(),
    val busquedaSalida: String = "",
    val personaSalidaSeleccionada: PersonaAdentroData? = null,
    val cargandoSalidas: Boolean = false,
    val registrandoSalida: Boolean = false,
    val errorListaSalida: String? = null,
    val mensajeSalida: String? = null,
    val salidaRegistrada: Boolean = false
)
