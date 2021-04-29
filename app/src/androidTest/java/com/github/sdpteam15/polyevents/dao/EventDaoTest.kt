package com.github.sdpteam15.polyevents.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.database.dao.EventDao
import com.github.sdpteam15.polyevents.database.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.room.EventLocal
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime
import kotlin.test.assertEquals

class EventDaoTest {
    private lateinit var eventDao: EventDao
    private lateinit var localDatabase: LocalDatabase

    private val testEventLocal = EventLocal(
        eventId = "1",
        eventName = "testEvent",
        description = "this is a test event",
        organizer = "some organizer",
        startTime = LocalDateTime.now()
    )

    @Before
    fun createDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        localDatabase = Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        eventDao = localDatabase.eventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        localDatabase.close()
    }

    private fun checkEventLocalEquals(eventLocal: EventLocal, expected: EventLocal) {
        assertEquals(eventLocal.eventId, expected.eventId)
        assertEquals(eventLocal.eventName, expected.eventName)
        assertEquals(eventLocal.organizer, expected.organizer)
        assertEquals(eventLocal.zoneName, expected.zoneName)
        assertEquals(eventLocal.description, expected.description)
        assertEquals(eventLocal.startTime, expected.startTime)
        assertEquals(eventLocal.endTime, expected.endTime)

        //TODO: add set equality for tags
    }

    @Test
    @Throws(Exception::class)
    fun testInsertEventLocal() = runBlocking {
        eventDao.insert(testEventLocal)
        val eventsLocal = eventDao.getAll()
        val retrievedEvent = eventsLocal[0]
        checkEventLocalEquals(retrievedEvent, testEventLocal)
    }

    @Test
    fun testGetEventById() = runBlocking {
        eventDao.insert(testEventLocal)
        checkEventLocalEquals(
            eventDao.getEventById(testEventLocal.eventId),
            testEventLocal
        )
    }

    @Test
    fun testGetAllLocalEvents() = runBlocking {
        assert(eventDao.getAll().isEmpty())
        eventDao.insert(testEventLocal)
        assertEquals(eventDao.getAll().size, 1)

        eventDao.insert(testEventLocal.copy(eventId = testEventLocal.eventId + "1"))
        assertEquals(eventDao.getAll().size, 2)

        eventDao.deleteAll()
        assert(eventDao.getAll().isEmpty())
    }

    @Test
    fun testInsertingEventWithSameIdReplacesOldOne() = runBlocking {
        assert(eventDao.getAll().isEmpty())
        eventDao.insert(testEventLocal)
        val otherEventName = "another event"
        assertEquals(eventDao.getAll().size, 1)

        val testEventLocal2 = testEventLocal.copy(eventName = otherEventName)
        assertEquals(testEventLocal2.eventName, otherEventName)
        eventDao.insert(testEventLocal2)
        val retrievedEvents = eventDao.getAll()
        assertEquals(retrievedEvents.size, 1)
        checkEventLocalEquals(retrievedEvents[0], testEventLocal2)
    }

}