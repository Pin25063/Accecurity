package dev.hpm.accecurity.feature.pases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hpm.accecurity.core.data.AppGraph
import dev.hpm.accecurity.core.model.validateVisitWindow
import dev.hpm.accecurity.core.model.PaseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para gestionar el estado y eventos de Pases bajo arquitectura MVI.
 */
class PasesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasesState())
    val uiState: StateFlow<PasesState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AppGraph.repository.observePasses().collect { pases ->
                _uiState.update { state ->
                    state.copy(
                        pases = pases,
                        // El detalle debe reflejar cambios de estado mientras está abierto.
                        paseSeleccionado = state.paseSeleccionado?.let { seleccionado ->
                            pases.find { it.id == seleccionado.id }
                        },
                        pasesFiltrados = filtrarPases(pases, state.filtroSeleccionado),
                        totalEsperados = pases.count { it.estado == "Esperado" },
                        totalAdentro = pases.count { it.estado == "Dentro" },
                        totalSalieron = pases.count { it.estado == "Salió" },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: PasesEvent) {
        when (event) {
            is PasesEvent.CambiarFiltro -> {
                _uiState.update { state ->
                    state.copy(
                        filtroSeleccionado = event.filtro,
                        pasesFiltrados = filtrarPases(
                            pases = state.pases,
                            filtro = event.filtro
                        )
                    )
                }
            }

            PasesEvent.CrearPase -> {
                _uiState.update {
                    it.copy(
                        mostrarFormulario = true,
                        nombreVisitante = "",
                        descripcionVisita = "",
                        destinoVisita = "",
                        horaVisita = "",
                        fechaVisita = "",
                        horaFinVisita = "",
                        errorFormulario = null,
                        mensaje = null
                    )
                }
            }

            PasesEvent.CancelarCreacion -> {
                _uiState.update {
                    it.copy(
                        mostrarFormulario = false,
                        nombreVisitante = "",
                        descripcionVisita = "",
                        destinoVisita = "",
                        horaVisita = "",
                        fechaVisita = "",
                        horaFinVisita = "",
                        errorFormulario = null
                    )
                }
            }

            is PasesEvent.CambiarNombre -> {
                _uiState.update {
                    it.copy(
                        nombreVisitante = event.nombre,
                        errorFormulario = null
                    )
                }
            }

            is PasesEvent.CambiarDescripcion -> {
                _uiState.update {
                    it.copy(
                        descripcionVisita = event.descripcion,
                        errorFormulario = null
                    )
                }
            }

            is PasesEvent.CambiarDestino -> {
                _uiState.update { it.copy(destinoVisita = event.destino, errorFormulario = null) }
            }

            is PasesEvent.CambiarHora -> {
                _uiState.update {
                    it.copy(
                        horaVisita = event.hora,
                        errorFormulario = null
                    )
                }
            }

            is PasesEvent.CambiarFecha -> {
                _uiState.update { it.copy(fechaVisita = event.fecha, errorFormulario = null) }
            }
            is PasesEvent.CambiarHoraFin -> {
                _uiState.update { it.copy(horaFinVisita = event.hora, errorFormulario = null) }
            }

            is PasesEvent.VerDetalle -> {
                _uiState.update {
                    it.copy(paseSeleccionado = event.pase)
                }
            }

            PasesEvent.CerrarDetalle -> {
                _uiState.update {
                    it.copy(paseSeleccionado = null)
                }
            }

            PasesEvent.GuardarPase -> guardarPase()
        }
    }

    /**
     * Valida los campos y envía una solicitud pendiente a Firestore.
     */
    private fun guardarPase() {
        val state = _uiState.value

        if (!state.mostrarFormulario || state.guardando) return

        val nombre = state.nombreVisitante.trim()
        val descripcion = state.descripcionVisita.trim()
        val destino = state.destinoVisita.trim()
        val hora = state.horaVisita.trim()
        val fecha = state.fechaVisita.trim()
        val horaFin = state.horaFinVisita.trim()

        if (nombre.isBlank() || descripcion.isBlank() || destino.isBlank() || fecha.isBlank() || hora.isBlank() || horaFin.isBlank()) {
            _uiState.update {
                it.copy(errorFormulario = "Completa todos los campos.")
            }
            return
        }

        val errorHorario = validateVisitWindow(fecha, hora, horaFin)
        if (errorHorario != null) {
            _uiState.update { it.copy(errorFormulario = errorHorario) }
            return
        }

        _uiState.update { it.copy(guardando = true, errorFormulario = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val saved = AppGraph.repository.createAccessRequest(
                name = nombre,
                description = descripcion,
                date = fecha,
                start = hora,
                end = horaFin,
                location = destino
            )
            withContext(Dispatchers.Main) {
                _uiState.update { actual ->
                    if (!saved) {
                        actual.copy(
                            guardando = false,
                            errorFormulario = "No se pudo enviar la solicitud."
                        )
                    } else {
                        actual.copy(
                            guardando = false,
                            mostrarFormulario = false,
                            nombreVisitante = "",
                            descripcionVisita = "",
                            destinoVisita = "",
                            horaVisita = "",
                            fechaVisita = "",
                            horaFinVisita = "",
                            errorFormulario = null,
                            mensaje = "Solicitud de $nombre enviada. El jefe debe autorizarla."
                        )
                    }
                }
            }
        }
    }

    /**
     * Obtiene los pases que corresponden al filtro seleccionado.
     */
    private fun filtrarPases(
        pases: List<PaseData>,
        filtro: String
    ): List<PaseData> {
        return when (filtro) {
            "Esperados" -> pases.filter { it.estado == "Esperado" }
            "Adentro" -> pases.filter { it.estado == "Dentro" }
            "Salieron" -> pases.filter { it.estado == "Salió" }
            else -> pases
        }
    }
}
