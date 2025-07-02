package com.yoyo.concurrenteventtracker.tracker

import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import kotlinx.coroutines.Job

interface ConcurrentEventTracker {
    suspend fun trackEvent(event: AnalyticsEvent)
    suspend fun shutdown()
    suspend fun uploadFlushedEvents()
}
