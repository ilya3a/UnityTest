package com.yoyo.concurrenteventtracker.flusher

import android.util.Log
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.data.repository.AnalyticsRepository
import com.yoyo.concurrenteventtracker.network.AnalyticsApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Handles observing event count and triggering flush when needed.
 */
@Singleton
 class AnalyticsFlusher @Inject constructor(
    private val repository: AnalyticsRepository,
    private val api: AnalyticsApi
) {

    val TAG = "AnalyticsFlusher"
    private val flushMutex = Mutex()

    /**
     * Flushes events to the server, deletes them if sent successfully.
     */
    suspend fun sendEvents() {
        Log.d(TAG, "Flushing events...${Thread.currentThread().name}")
        flushMutex.withLock {
            val events: List<AnalyticsEvent> = repository.getEventsForFlush()
            if (events.isEmpty()) {
                Log.d(TAG, "No events to flush")
                return
            }

            val success = api.send(events)
            if (success) {
                repository.deleteEvents()
            } else {
                // Log failure and retry later (simple retry mechanism)
                Log.d(TAG, "Flush failed. Will retry later.")
                // Optional: delay or schedule retry with WorkManager
            }
            Log.d(TAG, "Flushing events done ${Thread.currentThread().name}")
        }
    }

    suspend fun flush(eventBuffer: MutableList<AnalyticsEvent>) {
        repository.logEvents(eventBuffer)
    }

}
