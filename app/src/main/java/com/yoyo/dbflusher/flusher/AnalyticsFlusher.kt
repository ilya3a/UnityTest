package com.yoyo.dbflusher.flusher

import android.util.Log
import com.yoyo.dbflusher.data.repository.AnalyticsRepository
import com.yoyo.dbflusher.data.db.AnalyticsEvent
import com.yoyo.dbflusher.network.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles observing event count and triggering flush when needed.
 */
@Singleton
class AnalyticsFlusher @Inject constructor(
    private val repository: AnalyticsRepository,
    private val api: AnalyticsApi,
    private val policy: FlushPolicy
) {

    val TAG = "AnalyticsFlusher"

    private val flushMutex = Mutex()
    private var flushJob: Job? = null

    /**
     * Starts observing the database and triggers flush based on policy.
     */
    fun startObserving(scope: CoroutineScope) {
        flushJob?.cancel() // Cancel any previous job
        flushJob = scope.launch {
            repository.observeEventCount()
                .debounce(300) // Avoid reacting to every tiny change
                .collectLatest { count ->
                    // collectLatest ensures that if a new count arrives while flush() is still running,
                    // the previous flush is cancelled and only the latest count is processed.
                    // This prevents overlapping flushes and unnecessary processing.
                    Log.d(TAG,"Event count changed: $count")
                    if (count >= policy.maxEvents) {
                        flush()
                    }
                }
        }
    }

    /**
     * Flushes events to the server, deletes them if sent successfully.
     */
    suspend fun flush() {

        flushMutex.withLock {
            val events: List<AnalyticsEvent> = repository.getEventsForFlush(policy.maxFlushBatchSize)
            if (events.isEmpty()) return

            val success = api.send(events)
            if (success) {
                repository.deleteEventsByIds(events.map { it.id })
            } else {
                // Log failure and retry later (simple retry mechanism)
                Log.d(TAG,"Flush failed. Will retry later.")
                // Optional: delay or schedule retry with WorkManager
            }
        }
    }
}
