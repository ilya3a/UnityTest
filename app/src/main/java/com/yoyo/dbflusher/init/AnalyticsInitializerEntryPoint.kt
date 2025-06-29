package com.yoyo.dbflusher.init

import com.yoyo.dbflusher.flusher.AnalyticsFlusher
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for accessing Hilt dependencies from a non-Hilt class.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsInitializerEntryPoint {
    fun analyticsFlusher(): AnalyticsFlusher
}
