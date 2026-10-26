package dev.hpm.accecurity.core.data

import dev.hpm.accecurity.core.model.ActividadData
import dev.hpm.accecurity.core.model.TipoActividad

/**
 * Datos QUEMADOS de actividad para desarrollo y previews.
 */
fun obtenerActividadFalsa(): List<ActividadData> = listOf(
    ActividadData(
        id = "actividad-001",
        titulo = "Laura Méndez ingresó",
        descripcion = "Visita principal · QR verificado",
        hora = "09:12",
        tipo = TipoActividad.ENTRADA
    ),
    ActividadData(
        id = "actividad-002",
        titulo = "Pase digital creado",
        descripcion = "Para Diego Salas · 11:00",
        hora = "08:47",
        tipo = TipoActividad.PASE_CREADO
    ),
    ActividadData(
        id = "actividad-003",
        titulo = "Carlos Núñez salió",
        descripcion = "Garita principal",
        hora = "08:35",
        tipo = TipoActividad.SALIDA
    ),
    ActividadData(
        id = "actividad-004",
        titulo = "Código vencido rechazado",
        descripcion = "Garita de servicio · ACC-4070",
        hora = "08:21",
        tipo = TipoActividad.RECHAZO
    )
)