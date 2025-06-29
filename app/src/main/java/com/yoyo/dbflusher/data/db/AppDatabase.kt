package com.yoyo.dbflusher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AnalyticsEvent::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analyticsEventDao(): AnalyticsEventDao
}
