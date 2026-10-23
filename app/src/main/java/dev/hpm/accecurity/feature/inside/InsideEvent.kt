package dev.hpm.accecurity.feature.inside

/**
 * Eventos e intenciones del usuario en la pantalla de personas Adentro.
 */
sealed interface InsideEvent {
    data object RefrescarPersonasAdentro : InsideEvent
}
