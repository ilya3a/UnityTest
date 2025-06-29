package com.yoyo.dbflusher.di

import com.yoyo.dbflusher.flusher.FlushPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides configuration for when to flush analytics events.
 */
@Module
@InstallIn(SingletonComponent::class)
object FlushPolicyModule {

    @Provides
    @Singleton
    fun provideFlushPolicy(): FlushPolicy {
        return FlushPolicy()
    }
}
