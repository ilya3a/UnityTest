package com.yoyo.concurrenteventtracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AnalyticsEvent::class], version = 2)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun analyticsEventDao(): AnalyticsEventDao
}
