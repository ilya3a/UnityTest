package com.yoyo.dbflusher.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: AnalyticsEvent)

    @Query("SELECT * FROM analytics_events ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getOldestEvents(limit: Int): List<AnalyticsEvent>

    @Query("DELETE FROM analytics_events WHERE id IN (:ids)")
    suspend fun deleteEventsByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM analytics_events")
    fun observeEventCount(): Flow<Int>
}
