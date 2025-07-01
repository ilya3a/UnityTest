package com.yoyo.concurrenteventtracker.flusher

import android.util.Log
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.data.repository.AnalyticsRepository
import com.yoyo.concurrenteventtracker.network.AnalyticsApi
import kotlinx.coroutines.Job
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
     * Flushes events to the server, deletes them if sent successfully.
     */
    suspend fun flush() {
        Log.d(TAG,"Flushing events...${Thread.currentThread().name}")
        flushMutex.withLock {
            val currentCount = repository.getEventCount()
            if(currentCount >= policy.maxEvents){
                Log.d(TAG,"Flushing events in mutex ${Thread.currentThread().name}")
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
                Log.d(TAG,"Flushing events done ${Thread.currentThread().name}")
            }else{
                Log.d(TAG,"Flushing events  skipped ${currentCount}${Thread.currentThread().name}")
            }
        }
    }

}
