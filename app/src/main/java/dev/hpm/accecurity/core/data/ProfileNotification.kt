package dev.hpm.accecurity.core.data

import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.PaseData
import dev.hpm.accecurity.core.model.SolicitudData
import dev.hpm.accecurity.core.model.TipoActividad
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Avisos sobre el estado actual de las visitas creadas por la cuenta activa. */
fun avisosDeMisPases(pases: List<PaseData>): List<String> = pases.mapNotNull { pase ->
    val estado = when (pase.estadoAcceso) {
        "PENDING" -> "Solicitud pendiente"
        "EXPECTED" -> "Visita aprobada"
        "REJECTED" -> "Solicitud rechazada"
        "INSIDE" -> "Entrada registrada"
        "LEFT" -> "Salida registrada"
        "EXPIRED" -> "Pase vencido"
        else -> return@mapNotNull null
    }
    val detalle = if (pase.estadoAcceso == "INSIDE" || pase.estadoAcceso == "LEFT") pase.hora
    else pase.horarioAutorizado
    "$estado: ${pase.nombre}." + if (detalle.isNotBlank()) "\n$detalle" else ""
}

/** Solicitudes que el jefe todavía tiene por revisar. */
fun avisosDeSolicitudes(solicitudes: List<SolicitudData>): List<String> = solicitudes
    .sortedByDescending { it.esUrgente }
    .map { solicitud ->
        "Solicitud pendiente: ${solicitud.nombre}." +
                if (solicitud.horario.isNotBlank()) "\n${solicitud.horario}" else ""
    }

/** Movimientos de garita registrados, con los más recientes primero. */
fun avisosDeGarita(actividades: List<ActividadData>): List<String> = actividades
    .filter { it.tipo == TipoActividad.ENTRADA || it.tipo == TipoActividad.SALIDA }
    .sortedByDescending { actividad ->
        listOf("dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm").firstNotNullOfOrNull { formato ->
            runCatching { LocalDateTime.parse(actividad.hora, DateTimeFormatter.ofPattern(formato)) }.getOrNull()
        } ?: LocalDateTime.MIN
    }
    .take(20)
    .map { actividad ->
        val accion = if (actividad.tipo == TipoActividad.ENTRADA) "Entrada" else "Salida"
        "$accion: ${actividad.descripcion}." + if (actividad.hora.isNotBlank()) "\n${actividad.hora}" else ""
    }
