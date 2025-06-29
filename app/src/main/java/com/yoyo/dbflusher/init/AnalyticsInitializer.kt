package com.yoyo.dbflusher.init

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.yoyo.dbflusher.worker.FlushWorker.Companion.scheduleFlushWorker
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Initializes analytics flusher and worker when the app starts.
 */
class AnalyticsInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Log.d("AnalyticsInitializer", "Initializing Analytics...")

        // Access Hilt dependencies manually
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            AnalyticsInitializerEntryPoint::class.java
        )

        // Start observing events
        val flusher = entryPoint.analyticsFlusher()
        flusher.startObserving(
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )

        // Schedule periodic flush worker
        scheduleFlushWorker(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
