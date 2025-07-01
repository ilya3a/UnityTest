package com.yoyo.concurrenteventtracker.tracker

import android.content.Context
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.data.repository.AnalyticsRepository
import com.yoyo.concurrenteventtracker.di.ApplicationScope
import com.yoyo.concurrenteventtracker.flusher.AnalyticsFlusher
import com.yoyo.concurrenteventtracker.worker.FlushWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Public entry point for logging analytics events.
 * This class hides internal logic and exposes a clean API.
 */
@Singleton
class AnalyticsTracker @Inject constructor(
    private val repository: AnalyticsRepository,
    private val flusher: AnalyticsFlusher,
    @ApplicationScope applicationScope: CoroutineScope
) : ConcurrentEventTracker{

    val scope = applicationScope

    /**
     * Log an analytics event with optional attributes.
     * @param name Event name (e.g. "item_clicked")
     * @param params Optional attributes associated with the event (e.g. "item_id:123")
     */
     fun logEvent(name: String, metadata: Map<String, String>? = null) {
         scope.launch {
             val event = AnalyticsEvent(
                 name = name,
             )
             repository.logEvent(event)
             flusher.flush()
         }

    }

    /**
     * Schedule a worker to flush events.
     */
    fun startWorker(context: Context) {
        FlushWorker.scheduleFlushWorker(context)
    }

    override fun trackEvent(event: AnalyticsEvent){

    }
    override fun shutdown(){
        // Gracefully cancels background work
    }

    override suspend fun uploadFlushedEvents(){

    }

}
