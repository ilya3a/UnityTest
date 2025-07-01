package com.yoyo.concurrenteventtracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represents an analytics event that will be stored in the local database.
 */
@Entity(tableName = "analytics_events")
data class AnalyticsEvent(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Unique ID for the event

    val name: String, // Name of the event, e.g., "login_click"
    val metadata: Map<String, String> = emptyMap(), // JSON string with parameters (e.g., userId, screenName)
    val timestamp: Long = System.currentTimeMillis() // When the event was created
)
