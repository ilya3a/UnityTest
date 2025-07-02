package com.yoyo.concurrenteventtracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.data.db.AppDatabase
import com.yoyo.concurrenteventtracker.data.repository.AnalyticsRepository
import com.yoyo.concurrenteventtracker.flusher.AnalyticsFlusher
import com.yoyo.concurrenteventtracker.flusher.FlushPolicy
import com.yoyo.concurrenteventtracker.network.AnalyticsApi
import com.yoyo.concurrenteventtracker.tracker.AnalyticsTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsTrackerTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: AnalyticsRepository
    private lateinit var dispatcher: TestDispatcher
    private lateinit var flusher: RecordingFlusher
    private lateinit var tracker: AnalyticsTracker
    private lateinit var flushPolicy: FlushPolicy

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AnalyticsRepository(db.analyticsEventDao())
        dispatcher = StandardTestDispatcher()
        flushPolicy = FlushPolicy(maxEvents = 3, timerToFlush = 1000)
        flusher = RecordingFlusher(repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun flushes_when_max_events_reached() = runTest {
        tracker = AnalyticsTracker(flusher, flushPolicy, this)

        repeat(flushPolicy.maxEvents) {
            tracker.trackEvent(AnalyticsEvent(name = "e$it"))
        }
        advanceUntilIdle()
        tracker.shutdown()
        Assert.assertEquals(1, flusher.flushCount)
        Assert.assertEquals(flushPolicy.maxEvents, repository.getEventsForFlush().size)
        Assert.assertEquals(0, trackerBufferSize())
    }

    @Test
    fun periodic_timer_triggers_flush() = runTest {
        tracker = AnalyticsTracker(flusher, flushPolicy, this)
        tracker.trackEvent(AnalyticsEvent(name = "single"))
        advanceTimeBy(flushPolicy.timerToFlush)
        advanceUntilIdle()
        tracker.shutdown()
        Assert.assertEquals(1, flusher.flushCount)
        Assert.assertEquals(1, repository.getEventsForFlush().size)
    }

    @Test
    fun concurrent_trackEvent_calls_are_thread_safe() = runTest {
        tracker = AnalyticsTracker(flusher, flushPolicy, this)
        val count = 20

        repeat(count) { index ->
            launch {
                tracker.trackEvent(AnalyticsEvent(name = "n$index"))
            }
        }
        advanceUntilIdle()

        // shutdown() now internally waits for jobs to finish!
        tracker.shutdown()

        advanceUntilIdle()

        val stored = repository.getEventsForFlush()
        Assert.assertEquals(count, stored.size)
    }


    private fun trackerBufferSize(): Int {
        val field = AnalyticsTracker::class.java.getDeclaredField("eventBuffer")
        field.isAccessible = true
        val list = field.get(tracker) as MutableList<*>
        return list.size
    }

    private class RecordingFlusher(repository: AnalyticsRepository) : AnalyticsFlusher(repository, object : AnalyticsApi {
        override suspend fun send(events: List<AnalyticsEvent>): Boolean = true
    }) {
        var flushCount = 0
        override suspend fun flush(eventBuffer: MutableList<AnalyticsEvent>) {
            flushCount++
            super.flush(eventBuffer)
        }
    }
}