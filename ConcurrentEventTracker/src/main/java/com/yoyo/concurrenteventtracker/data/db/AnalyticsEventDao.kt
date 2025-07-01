package com.yoyo.concurrenteventtracker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<AnalyticsEvent>)

    @Query("SELECT * FROM analytics_events ORDER BY timestamp")
    suspend fun getEvents(): List<AnalyticsEvent>

    @Query("DELETE FROM analytics_events")
    suspend fun delete()

    @Query("SELECT COUNT(*) FROM analytics_events")
    suspend fun getEventCount(): Int
}
