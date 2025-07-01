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
class AnalyticsRepository @Inject constructor(
    private val dao: AnalyticsEventDao
) {
    // Mutex to prevent concurrent write/read/flush operations
    private val mutex = Mutex()

    /**
     * Inserts a new event into the local database.
     */
    suspend fun logEvent(event: AnalyticsEvent) {
        mutex.withLock {
            dao.insert(event)
        }
    }

    /**
     * Retrieves the oldest events up to the specified limit.
     */
    suspend fun getEventsForFlush(limit: Int): List<AnalyticsEvent> {
        return mutex.withLock {
            dao.getOldestEvents(limit)
        }
    }

    /**
     * Deletes events by their IDs after successful send.
     */
    suspend fun deleteEventsByIds(ids: List<Long>) {
        mutex.withLock {
            dao.deleteEventsByIds(ids)
        }
    }

    /**
     * Retrieves the count of events in the local database.
     */
    suspend fun getEventCount(): Int {
        return dao.getEventCount()
    }
}
