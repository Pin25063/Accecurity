package dev.hpm.accecurity.core.data

import android.content.Context
import dev.hpm.accecurity.core.database.AccecurityDatabase
import dev.hpm.accecurity.core.sync.NetworkMonitor
import dev.hpm.accecurity.core.sync.AccessSyncScheduler

/**
 * Punto único para que los ViewModel usen el mismo repositorio.
 */
object AppGraph {
    lateinit var repository: AccessRepository
        private set

    @Synchronized
    fun init(context: Context) {
        if (::repository.isInitialized) return
        val appContext = context.applicationContext
        repository = AccessRepository(
            database = AccecurityDatabase.get(appContext),
            network = NetworkMonitor(appContext),
            scheduleSync = { AccessSyncScheduler.enqueue(appContext) }
        )
        repository.startSyncWithAuth()
    }
}
