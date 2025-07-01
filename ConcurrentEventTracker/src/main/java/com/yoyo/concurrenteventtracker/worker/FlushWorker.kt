package com.yoyo.concurrenteventtracker.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.yoyo.concurrenteventtracker.flusher.AnalyticsFlusher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Periodic worker that flushes analytics events even if the app is in background.
 */
@HiltWorker
class FlushWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val flusher: AnalyticsFlusher
) : CoroutineWorker(context, params) {

    companion object{
        fun scheduleFlushWorker(context: Context) {
            val request = PeriodicWorkRequestBuilder<FlushWorker>(
                15, TimeUnit.MINUTES // Minimal interval allowed for periodic work
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "flush_worker",
                ExistingPeriodicWorkPolicy.KEEP, // Don't override if already running
                request
            )
        }
    }
    override suspend fun doWork(): Result {
        return try {
            Log.d("FlushWorker", "Attempting background flush...")
            flusher.flush()
            Result.success()
        } catch (e: Exception) {
            Log.e("FlushWorker", "Flush failed in background", e)
            Result.retry() // Try again later if it fails
        }
    }
}
