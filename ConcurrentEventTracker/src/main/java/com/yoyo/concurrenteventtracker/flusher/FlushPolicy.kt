package com.yoyo.concurrenteventtracker.flusher

import javax.inject.Singleton

/**
 * Configuration class that defines when to trigger a flush.
 */
@Singleton
class FlushPolicy(
    val maxEvents: Int = 20, // Trigger flush when this number of events is reached
    val maxFlushBatchSize: Int = 20 // How many events to send at once
)
