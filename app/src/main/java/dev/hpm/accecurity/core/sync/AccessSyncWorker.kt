package dev.hpm.accecurity.core.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.hpm.accecurity.core.data.AppGraph
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException

class AccessSyncWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        AppGraph.init(applicationContext)
        return try {
            if (AppGraph.repository.syncPendingMovements()) Result.success() else Result.retry()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

object AccessSyncScheduler {
    fun enqueue(context: Context) {
        val request = OneTimeWorkRequestBuilder<AccessSyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()
        // APPEND_OR_REPLACE no pierde movimientos agregados cuando otro worker está terminando.
        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork("accecurity-access-sync", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }
}
