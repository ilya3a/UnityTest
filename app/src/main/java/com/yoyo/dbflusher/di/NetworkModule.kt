package com.yoyo.dbflusher.di

import com.yoyo.dbflusher.network.AnalyticsApi
import com.yoyo.dbflusher.network.MockAnalyticsApi
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