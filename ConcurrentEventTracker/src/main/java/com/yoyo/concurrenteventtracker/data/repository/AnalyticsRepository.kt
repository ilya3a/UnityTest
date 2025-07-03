package com.yoyo.concurrenteventtracker.data.repository

import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEventDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles analytics event storage and access.
 * All access is protected with a Mutex to ensure thread safety.
 */
@Singleton
internal class AnalyticsRepository @Inject constructor(
    private val dao: AnalyticsEventDao
) {
    // Mutex to prevent concurrent write/read/flush operations
    private val mutex = Mutex()

    /**
     * Inserts a new event into the local database.
     */
    suspend fun logEvents(events: List<AnalyticsEvent>) {
        mutex.withLock {
            dao.insert(events)
        }
    }

    /**
     * Retrieves the oldest events up to the specified limit.
     */
    suspend fun getEventsForFlush(): List<AnalyticsEvent> {
        return mutex.withLock {
            dao.getEvents()
        }
    }

    /**
     * Deletes events by their IDs after successful send.
     */
    suspend fun deleteEvents() {
        mutex.withLock {
            dao.delete()
        }
    }

}
