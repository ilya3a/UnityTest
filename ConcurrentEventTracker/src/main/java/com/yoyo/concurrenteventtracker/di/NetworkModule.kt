package com.yoyo.concurrenteventtracker.di

import com.yoyo.concurrenteventtracker.network.AnalyticsApi
import com.yoyo.concurrenteventtracker.network.MockAnalyticsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): AnalyticsApi {
        return MockAnalyticsApi()
    }
}