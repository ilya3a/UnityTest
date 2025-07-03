package com.yoyo.concurrenteventtracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.data.db.AppDatabase
import com.yoyo.concurrenteventtracker.data.repository.AnalyticsRepository
import com.yoyo.concurrenteventtracker.flusher.AnalyticsFlusher
import com.yoyo.concurrenteventtracker.network.AnalyticsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsFlusherTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: AnalyticsRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AnalyticsRepository(db.analyticsEventDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun sendEventsclearsDBonsuccess() = runTest {
        val api = FakeApi(true)
        val flusher = AnalyticsFlusher(repository, api)
        repository.logEvents(listOf(AnalyticsEvent(name = "a"), AnalyticsEvent(name = "b")))
        flusher.sendEvents()
        advanceUntilIdle()
        Assert.assertEquals(2, api.received.size)
        Assert.assertEquals(0, repository.getEventsForFlush().size)
    }

    @Test
    fun sendEventskeepseventsonfailure() = runTest {
        val api = FakeApi(false)
        val flusher = AnalyticsFlusher(repository, api)
        repository.logEvents(listOf(AnalyticsEvent(name = "a")))
        flusher.sendEvents()
        advanceUntilIdle()
        Assert.assertEquals(1, api.received.size)
        Assert.assertEquals(1, repository.getEventsForFlush().size)
    }

    private class FakeApi(private val success: Boolean) : AnalyticsApi {
        var received: List<AnalyticsEvent> = emptyList()
        override suspend fun send(events: List<AnalyticsEvent>): Boolean {
            received = events
            return success
        }
    }
}