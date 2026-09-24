package dev.hpm.accecurity.data

import dev.hpm.accecurity.model.PaseData
import dev.hpm.accecurity.ui.theme.StatusExpected
import dev.hpm.accecurity.ui.theme.StatusExpectedContainer
import dev.hpm.accecurity.ui.theme.StatusInside
import dev.hpm.accecurity.ui.theme.StatusInsideContainer

/**
 * Datos temporales de pases para desarrollo y previews.
 * Reemplazar por fuente real (API o base de datos) cuando esté disponible.
 */
fun obtenerPasesFalsos(): List<PaseData> = listOf(
    PaseData(
        iniciales = "LM",
        nombre = "Laura Méndez",
        descripcion = "Visita familiar · Apto 4...",
        estado = "Dentro",
        hora = "09:12",
        colorEstado = StatusInsideContainer,
        colorPunto = StatusInside
    ),
    PaseData(
        iniciales = "MD",
        nombre = "Mensajero DHL",
        descripcion = "Entrega de paquete · ...",
        estado = "Dentro",
        hora = "09:40",
        colorEstado = StatusInsideContainer,
        colorPunto = StatusInside
    ),
    PaseData(
        iniciales = "DS",
        nombre = "Diego Salas",
        descripcion = "Mantenimiento · Ap...",
        estado = "Esperado",
        hora = "11:00",
        colorEstado = StatusExpectedContainer,
        colorPunto = StatusExpected
    ),
    PaseData(
        iniciales = "AB",
        nombre = "Ana Beltrán",
        descripcion = "Reunión de padres ·...",
        estado = "Esperado",
        hora = "12:30",
        colorEstado = StatusExpectedContainer,
        colorPunto = StatusExpected
    )
)
