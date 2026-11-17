package dev.hpm.accecurity

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Prueba instrumentada. Se ejecuta en un dispositivo o emulador Android.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Contexto de la app que se está probando.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("dev.hpm.accecurity", appContext.packageName)
    }
}