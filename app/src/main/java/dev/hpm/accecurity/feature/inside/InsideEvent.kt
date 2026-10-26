package dev.hpm.accecurity.feature.inside

/**
 * Eventos e intenciones del usuario en la pantalla de personas Adentro.
 */
sealed interface InsideEvent {
    data class ConfigurarPantalla(
        val numeroSolicitudesJefe: Int
    ) : InsideEvent

    data object RefrescarPersonasAdentro : InsideEvent
}