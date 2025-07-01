package com.yoyo.concurrenteventtracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AnalyticsEvent::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analyticsEventDao(): AnalyticsEventDao
}
