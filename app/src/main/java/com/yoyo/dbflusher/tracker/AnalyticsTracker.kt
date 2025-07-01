package com.yoyo.dbflusher.tracker

import android.content.Context
import com.yoyo.dbflusher.data.db.AnalyticsEvent
import com.yoyo.dbflusher.data.repository.AnalyticsRepository
import com.yoyo.dbflusher.di.ApplicationScope
import com.yoyo.dbflusher.flusher.AnalyticsFlusher
import com.yoyo.dbflusher.worker.FlushWorker
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationScope  applicationScope: CoroutineScope
) {

    val scope = applicationScope

    /**
     * Log an analytics event with optional attributes.
     * @param name Event name (e.g. "item_clicked")
     * @param params Optional attributes associated with the event (e.g. "item_id:123")
     */
     fun logEvent(name: String, params: String) {
         scope.launch {
             val event = AnalyticsEvent(
                 name = name,
                 params = params
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
}
