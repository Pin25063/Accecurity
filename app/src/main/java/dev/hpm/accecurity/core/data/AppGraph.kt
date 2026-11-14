package dev.hpm.accecurity.core.data

import android.content.Context
import dev.hpm.accecurity.core.database.AccecurityDatabase

/**
 * Punto único para que los ViewModel usen el mismo repositorio.
 */
object AppGraph {
    lateinit var repository: AccessRepository
        private set

    fun init(context: Context) {
        if (::repository.isInitialized) return
        repository = AccessRepository(AccecurityDatabase.get(context))
        repository.startSyncWithAuth()
    }
}
