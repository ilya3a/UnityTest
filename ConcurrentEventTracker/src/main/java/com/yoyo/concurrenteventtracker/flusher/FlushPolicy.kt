package com.yoyo.concurrenteventtracker.flusher

import javax.inject.Singleton

/**
 * Configuration class that defines when to trigger a flush.
 */
@Singleton
class FlushPolicy(
    val maxEvents: Int = 5, // Trigger flush when this number of events is reached
    val maxFlushBatchSize: Int = 20, // How many events to send at once
    val timerToFlush: Long = 1000 * 10 // How long to wait before flushing
)
