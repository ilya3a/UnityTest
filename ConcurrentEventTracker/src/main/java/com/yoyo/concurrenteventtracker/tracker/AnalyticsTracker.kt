package com.yoyo.concurrenteventtracker.tracker

import android.util.Log
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.di.ApplicationScope
import com.yoyo.concurrenteventtracker.flusher.AnalyticsFlusher
import com.yoyo.concurrenteventtracker.flusher.FlushPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Public entry point for logging analytics events.
 * This class hides internal logic and exposes a clean API.
 */
@Singleton
class AnalyticsTracker @Inject constructor(
    private val flusher: AnalyticsFlusher,
    private val flushPolicy: FlushPolicy,
    @param:ApplicationScope private val scope: CoroutineScope
) : ConcurrentEventTracker {

    private var eventBuffer = mutableListOf<AnalyticsEvent>()
    private val trackerMutex = Mutex()
    private var periodicFlushJob: Job? = null

    init {
        startPeriodicFlush()
    }

    private fun startPeriodicFlush() {
        periodicFlushJob?.cancel() // in case already running
        periodicFlushJob = scope.launch {
            while (true) {
                delay(flushPolicy.timerToFlush) // wait 10 seconds
                Log.d("AnalyticsTracker", "Periodic flush check triggered")
                trackerMutex.withLock {
                    if (eventBuffer.isNotEmpty()) {
                        flusher.flush(eventBuffer)
                        eventBuffer.clear()
                    }
                }
            }
        }
    }


    override fun trackEvent(event: AnalyticsEvent) {
        scope.launch {
            trackerMutex.withLock {
                if (periodicFlushJob == null || !periodicFlushJob!!.isActive) {
                    startPeriodicFlush()
                }
                eventBuffer.add(event)
                if (eventBuffer.size >= flushPolicy.maxEvents) {
                    flusher.flush(eventBuffer)
                    eventBuffer.clear()
                }
            }
        }
    }


    override fun shutdown() {
        scope.launch {
            trackerMutex.withLock {
                periodicFlushJob?.cancel()
                Log.d("AnalyticsTracker", "Shutting down")
                if (eventBuffer.isNotEmpty()) {
                    flusher.flush(eventBuffer)
                    eventBuffer.clear()
                }
            }
        }
    }

    override suspend fun uploadFlushedEvents() {
        flusher.sendEvents()
    }

}
