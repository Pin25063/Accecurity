package dev.hpm.accecurity.feature.pases

import androidx.lifecycle.ViewModel
import dev.hpm.accecurity.core.data.obtenerPasesFalsos
import dev.hpm.accecurity.core.model.PaseData
import dev.hpm.accecurity.core.ui.theme.StatusExpected
import dev.hpm.accecurity.core.ui.theme.StatusExpectedContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y eventos de Pases bajo arquitectura MVI.
 */
class PasesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasesState())
    val uiState: StateFlow<PasesState> = _uiState.asStateFlow()

    init {
        cargarPases()
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
                        horaVisita = "",
                        errorFormulario = null
                    )
                }
            }

            PasesEvent.CancelarCreacion -> {
                _uiState.update {
                    it.copy(
                        mostrarFormulario = false,
                        nombreVisitante = "",
                        descripcionVisita = "",
                        horaVisita = "",
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

            is PasesEvent.CambiarHora -> {
                _uiState.update {
                    it.copy(
                        horaVisita = event.hora,
                        errorFormulario = null
                    )
                }
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
     * Carga los pases de prueba y calcula las estadísticas del listado completo.
     */
    private fun cargarPases() {
        val pases = obtenerPasesFalsos()

        _uiState.update { state ->
            state.copy(
                pases = pases,
                pasesFiltrados = filtrarPases(
                    pases = pases,
                    filtro = state.filtroSeleccionado
                ),
                totalEsperados = pases.count { it.estado == "Esperado" },
                totalAdentro = pases.count { it.estado == "Dentro" },
                totalSalieron = pases.count { it.estado == "Salió" },
                isLoading = false
            )
        }
    }

    /**
     * Valida los campos y agrega un pase esperado al listado de la pantalla.
     */
    private fun guardarPase() {
        val state = _uiState.value

        if (!state.mostrarFormulario) return

        val nombre = state.nombreVisitante.trim()
        val descripcion = state.descripcionVisita.trim()
        val hora = state.horaVisita.trim()

        if (nombre.isBlank() || descripcion.isBlank() || hora.isBlank()) {
            _uiState.update {
                it.copy(errorFormulario = "Completa todos los campos.")
            }
            return
        }

        if (!Regex("([01][0-9]|2[0-3]):[0-5][0-9]").matches(hora)) {
            _uiState.update {
                it.copy(
                    errorFormulario = "Ingresa una hora válida en formato HH:mm."
                )
            }
            return
        }

        val iniciales = nombre.split(Regex("\\s+"))
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }

        val nuevoPase = PaseData(
            iniciales = iniciales,
            nombre = nombre,
            descripcion = descripcion,
            estado = "Esperado",
            hora = hora,
            colorEstado = StatusExpectedContainer,
            colorPunto = StatusExpected
        )

        _uiState.update { actual ->
            val pases = listOf(nuevoPase) + actual.pases

            actual.copy(
                pases = pases,
                pasesFiltrados = pases,
                filtroSeleccionado = "Todos",
                totalEsperados = pases.count { it.estado == "Esperado" },
                totalAdentro = pases.count { it.estado == "Dentro" },
                totalSalieron = pases.count { it.estado == "Salió" },
                mostrarFormulario = false,
                nombreVisitante = "",
                descripcionVisita = "",
                horaVisita = "",
                errorFormulario = null
            )
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