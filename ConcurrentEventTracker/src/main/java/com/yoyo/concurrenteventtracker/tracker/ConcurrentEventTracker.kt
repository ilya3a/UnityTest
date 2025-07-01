package com.yoyo.concurrenteventtracker.tracker

import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent

interface ConcurrentEventTracker {
    fun trackEvent(event: AnalyticsEvent)
    fun shutdown()
    suspend fun uploadFlushedEvents()
}
