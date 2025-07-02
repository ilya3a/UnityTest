package com.yoyo.concurrenteventtracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.data.db.AppDatabase
import com.yoyo.concurrenteventtracker.data.repository.AnalyticsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsRepositoryTest {
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
    fun repository_CRUD_operations() = runTest {
        val events = listOf(
            AnalyticsEvent(name = "a", timestamp = 1),
            AnalyticsEvent(name = "b", timestamp = 2)
        )
        repository.logEvents(events)
        val loaded = repository.getEventsForFlush()
        Assert.assertEquals(listOf("a", "b"), loaded.map { it.name })
        repository.deleteEvents()
        Assert.assertEquals(0, db.analyticsEventDao().getEventCount())
    }

    @Test
    fun concurrent_logging_is_thread_safe() = runTest {
        val count = 100
        repeat(count) { index ->
            launch { repository.logEvents(listOf(AnalyticsEvent(name = "n$index"))) }
        }
        advanceUntilIdle()
        val events = repository.getEventsForFlush()
        Assert.assertEquals(count, events.size)
    }
}