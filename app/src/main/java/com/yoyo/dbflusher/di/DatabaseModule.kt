package com.yoyo.dbflusher.di

import android.content.Context
import androidx.room.Room
import com.yoyo.dbflusher.data.db.AnalyticsEventDao
import com.yoyo.dbflusher.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "analytics-db"
        ).build()
    }

    @Provides
    fun provideAnalyticsEventDao(db: AppDatabase): AnalyticsEventDao {
        return db.analyticsEventDao()
    }
}
