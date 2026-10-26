package dev.hpm.accecurity.core.data

import dev.hpm.accecurity.core.model.PersonaAdentroData

/**
 * Datos QUEMADOS de personas adentro para desarrollo y previews.
 */
fun obtenerPersonasAdentroFalsas(): List<PersonaAdentroData> = listOf(
    PersonaAdentroData(
        id = "adentro-001",
        nombre = "Laura Méndez",
        descripcion = "Visita familiar",
        destino = "Secretaría",
        horaEntrada = "09:12"
    ),
    PersonaAdentroData(
        id = "adentro-002",
        nombre = "Mensajero DHL",
        descripcion = "Entrega de paquete",
        destino = "Recepción",
        horaEntrada = "09:40"
    ),
    PersonaAdentroData(
        id = "adentro-003",
        nombre = "Diego Salas",
        descripcion = "Mantenimiento",
        destino = "Área técnica",
        horaEntrada = "10:00"
    ),
    PersonaAdentroData(
        id = "adentro-004",
        nombre = "Prof. Ramírez",
        descripcion = "Reunión",
        destino = "Dirección",
        horaEntrada = "10:15"
    ),
    PersonaAdentroData(
        id = "adentro-005",
        nombre = "Sofía Cano",
        descripcion = "Visita",
        destino = "Secretaría",
        horaEntrada = "10:30"
    )
)