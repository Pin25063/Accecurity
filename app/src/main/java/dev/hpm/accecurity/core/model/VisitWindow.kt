package dev.hpm.accecurity.core.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Ventana de ingreso en la fecha y hora local del dispositivo, dentro del mismo día. */
fun validateVisitWindow(
    date: String,
    start: String,
    end: String,
    now: LocalDateTime = LocalDateTime.now()
): String? {
    val visitDate = runCatching { LocalDate.parse(date) }.getOrNull()
        ?: return "Selecciona una fecha válida."
    if (visitDate.isBefore(now.toLocalDate())) return "La fecha no puede ser anterior a hoy."
    val timePattern = Regex("([01][0-9]|2[0-3]):[0-5][0-9]")
    if (!timePattern.matches(start) || !timePattern.matches(end)) {
        return "Selecciona ambas horas en formato HH:mm."
    }
    val startTime = LocalTime.parse(start)
    val endTime = LocalTime.parse(end)
    if (!endTime.isAfter(startTime)) return "La hora final debe ser posterior a la inicial, el mismo día."
    if (!visitDate.atTime(endTime).isAfter(now)) return "El horario de ingreso ya terminó."
    return null
}

fun displayVisitDate(date: String): String = runCatching {
    LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}.getOrDefault(date)

fun displayVisitWindow(date: String, start: String, end: String, fallback: String): String {
    return if (date.isNotBlank() && start.isNotBlank() && end.isNotBlank()) {
        "${displayVisitDate(date)} · $start - $end"
    } else fallback
}
