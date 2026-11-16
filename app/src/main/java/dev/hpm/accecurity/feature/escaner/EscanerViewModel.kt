package dev.hpm.accecurity.feature.escaner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hpm.accecurity.core.data.AppGraph
import dev.hpm.accecurity.core.model.RegisterExitResult
import dev.hpm.accecurity.core.model.PassLookupResult
import dev.hpm.accecurity.core.model.RegisterEntryResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EscanerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EscanerState())
    val uiState: StateFlow<EscanerState> = _uiState.asStateFlow()
    private var operationJob: Job? = null
    private var exitJob: Job? = null

    init {
        viewModelScope.launch {
            AppGraph.repository.observeInside().collect { personas ->
                _uiState.update { it.copy(personasAdentro = personas.sortedBy { persona -> persona.nombre.lowercase() }) }
            }
        }
    }

    fun onEvent(event: EscanerEvent) {
        when (event) {
            is EscanerEvent.CodigoEscaneado -> consultarPase(event.codigo)
            EscanerEvent.AbrirRegistroSalida -> abrirSalidas()
            EscanerEvent.ActualizarListaSalida -> cargarSalidas()
            is EscanerEvent.CambiarBusquedaSalida -> {
                _uiState.update { it.copy(busquedaSalida = event.texto) }
            }
            is EscanerEvent.SeleccionarPersonaSalida -> {
                val state = _uiState.value
                if (!state.mostrarSalida || state.cargandoSalidas || state.registrandoSalida || state.errorListaSalida != null) return
                val persona = state.personasAdentro.find { it.id == event.persona.id } ?: return
                _uiState.update {
                    it.copy(personaSalidaSeleccionada = persona, mensajeSalida = null, salidaRegistrada = false)
                }
            }
            EscanerEvent.VolverListaSalida -> {
                if (_uiState.value.registrandoSalida) return
                _uiState.update { it.copy(personaSalidaSeleccionada = null, mensajeSalida = null) }
                cargarSalidas()
            }
            EscanerEvent.ConfirmarSalida -> confirmarSalida()
            EscanerEvent.CerrarRegistroSalida -> cerrarSalidas()
            EscanerEvent.ConfirmarEntrada -> confirmarEntrada()
            EscanerEvent.CerrarResultado -> cerrarResultado()
            EscanerEvent.ReiniciarEscaner -> {
                if (_uiState.value.registroEnCurso || _uiState.value.mostrarSalida) return
                operationJob?.cancel()
                _uiState.update {
                    EscanerState(tienePermisoCamara = it.tienePermisoCamara, personasAdentro = it.personasAdentro)
                }
            }
            is EscanerEvent.ErrorOcurrido -> {
                _uiState.update {
                    it.copy(estaEscaneando = false, mensajeError = event.mensaje, mensajeExito = null)
                }
            }
            EscanerEvent.LimpiarMensajes -> {
                _uiState.update { it.copy(mensajeError = null, mensajeExito = null) }
            }
            is EscanerEvent.PermisoCamaraActualizado -> {
                _uiState.update {
                    it.copy(
                        tienePermisoCamara = event.concedido,
                        mensajeError = if (!event.concedido) "Se requiere permiso de cámara para escanear." else null
                    )
                }
            }
        }
    }

    private fun consultarPase(rawCode: String) {
        val state = _uiState.value
        // CameraX puede detectar el mismo QR en varios frames consecutivos.
        if (!state.estaEscaneando || state.consultaEnCurso || state.registroEnCurso || state.mostrarSalida) return
        val code = rawCode.trim()
        _uiState.update {
            it.copy(
                estaEscaneando = false,
                ultimoCodigoEscaneado = code,
                mostrarResultado = true,
                consultaEnCurso = true,
                paseValidado = null,
                mensajeResultado = null,
                entradaRegistrada = false,
                mensajeError = null,
                mensajeExito = null
            )
        }
        operationJob = viewModelScope.launch {
            when (val result = AppGraph.repository.lookupPassForEntry(code)) {
                is PassLookupResult.Valid -> _uiState.update {
                    it.copy(consultaEnCurso = false, paseValidado = result.pass)
                }
                is PassLookupResult.Invalid -> _uiState.update {
                    it.copy(consultaEnCurso = false, mensajeResultado = result.message)
                }
            }
        }
    }

    private fun confirmarEntrada() {
        val state = _uiState.value
        if (state.paseValidado == null || state.registroEnCurso || state.consultaEnCurso || state.entradaRegistrada) return
        val code = state.ultimoCodigoEscaneado ?: return
        _uiState.update { it.copy(registroEnCurso = true, mensajeResultado = null) }
        operationJob = viewModelScope.launch {
            when (val result = AppGraph.repository.registerEntry(code)) {
                is RegisterEntryResult.Success -> _uiState.update {
                    it.copy(
                        registroEnCurso = false,
                        entradaRegistrada = true,
                        mensajeResultado = "Entrada de ${result.visitorName} registrada el ${result.time}."
                    )
                }
                is RegisterEntryResult.Failure -> _uiState.update {
                    it.copy(
                        registroEnCurso = false,
                        paseValidado = null,
                        mensajeResultado = result.message
                    )
                }
            }
        }
    }

    private fun cerrarResultado() {
        if (_uiState.value.registroEnCurso) return
        operationJob?.cancel()
        // Queda pausado hasta tocar Escanear otro código, evitando reabrir el mismo QR.
        _uiState.update {
            it.copy(
                mostrarResultado = false,
                consultaEnCurso = false,
                paseValidado = null,
                mensajeResultado = null,
                entradaRegistrada = false,
                estaEscaneando = false
            )
        }
    }

    private fun abrirSalidas() {
        val state = _uiState.value
        if (state.registroEnCurso || state.consultaEnCurso || state.mostrarResultado || state.mostrarSalida) return
        operationJob?.cancel()
        _uiState.update {
            it.copy(
                estaEscaneando = false,
                mostrarSalida = true,
                busquedaSalida = "",
                personaSalidaSeleccionada = null,
                mensajeSalida = null,
                errorListaSalida = null,
                salidaRegistrada = false
            )
        }
        cargarSalidas()
    }

    private fun cargarSalidas() {
        if (!_uiState.value.mostrarSalida || _uiState.value.registrandoSalida) return
        exitJob?.cancel()
        _uiState.update { it.copy(cargandoSalidas = true, errorListaSalida = null) }
        exitJob = viewModelScope.launch {
            val error = AppGraph.repository.refreshPassesForExit()
            _uiState.update { it.copy(cargandoSalidas = false, errorListaSalida = error) }
        }
    }

    private fun confirmarSalida() {
        val state = _uiState.value
        val persona = state.personaSalidaSeleccionada ?: return
        if (!state.mostrarSalida || state.registrandoSalida || state.cargandoSalidas || state.salidaRegistrada) return
        _uiState.update { it.copy(registrandoSalida = true, mensajeSalida = null) }
        exitJob = viewModelScope.launch {
            when (val result = AppGraph.repository.registerExit(persona.id)) {
                is RegisterExitResult.Success -> _uiState.update {
                    it.copy(
                        registrandoSalida = false,
                        salidaRegistrada = true,
                        mensajeSalida = "Salida de ${result.visitorName} registrada el ${result.time}."
                    )
                }
                is RegisterExitResult.Failure -> _uiState.update {
                    it.copy(registrandoSalida = false, mensajeSalida = result.message)
                }
            }
        }
    }

    private fun cerrarSalidas() {
        if (_uiState.value.registrandoSalida) return
        exitJob?.cancel()
        _uiState.update {
            it.copy(
                mostrarSalida = false,
                cargandoSalidas = false,
                personaSalidaSeleccionada = null,
                mensajeSalida = null,
                errorListaSalida = null,
                salidaRegistrada = false,
                estaEscaneando = false
            )
        }
    }
}
