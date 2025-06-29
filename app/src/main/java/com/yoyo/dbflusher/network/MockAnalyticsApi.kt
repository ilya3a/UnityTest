package com.yoyo.dbflusher.network

import android.util.Log
import com.yoyo.dbflusher.data.db.AnalyticsEvent
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake implementation of AnalyticsApi that simulates success or failure.
 */
@Singleton
class MockAnalyticsApi @Inject constructor() : AnalyticsApi {

    override suspend fun send(events: List<AnalyticsEvent>): Boolean {
        Log.d("AnalyticsApi", "Sending ${events.size} events to the server...")
        delay(1000) // Simulate network delay
        val success = true // Change to false to simulate failure
        Log.d("AnalyticsApi", "Send result: $success")
        return success
    }
}
