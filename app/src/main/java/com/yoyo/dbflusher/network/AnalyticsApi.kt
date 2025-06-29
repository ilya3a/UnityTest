package com.yoyo.dbflusher.network

import com.yoyo.dbflusher.data.db.AnalyticsEvent

/**
 * Interface for sending analytics events to the remote server.
 */
interface AnalyticsApi {

    /**
     * Sends a list of events to the server.
     * Returns true if successful, false otherwise.
     */
    suspend fun send(events: List<AnalyticsEvent>): Boolean
}
