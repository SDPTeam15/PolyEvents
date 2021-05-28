package com.github.sdpteam15.polyevents.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.model.database.local.dao.EventDao
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.room.EventLocal
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class EventDaoTest {
    private lateinit var eventDao: EventDao
    private lateinit var localDatabase: LocalDatabase

    private val event_uid = "1"
    private val testEventLocal = EventLocal(
            eventId = event_uid,
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

    @Test
    @Throws(Exception::class)
    fun testInsertEventLocal() = runBlocking {
        eventDao.insert(testEventLocal)
        val eventsLocal = eventDao.getAll()
        val retrievedEvent = eventsLocal[0]
        assertEquals(retrievedEvent, testEventLocal)
    }

    @Test
    @Throws(Exception::class)
    fun testGetEventById() = runBlocking {
        eventDao.insert(testEventLocal)
        val retrievedEvent = eventDao.getEventById(testEventLocal.eventId)
        assertFalse(retrievedEvent.isEmpty())
        assertEquals(
                retrievedEvent[0],
                testEventLocal
        )
    }

    @Test
    @Throws(Exception::class)
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
    @Throws(Exception::class)
    fun testInsertingEventWithSameIdReplacesOldOne() = runBlocking {
        assert(eventDao.getAll().isEmpty())
        eventDao.insert(testEventLocal)
        assertEquals(eventDao.getAll().size, 1)

        val otherEventName = "another event"
        val testEventLocal2 = testEventLocal.copy(eventName = otherEventName)
        assertEquals(testEventLocal2.eventName, otherEventName)
        eventDao.insert(testEventLocal2)
        val retrievedEvents = eventDao.getAll()
        assertEquals(retrievedEvents.size, 1)
        assertEquals(retrievedEvents[0], testEventLocal2)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertingAll() = runBlocking {
        assert(eventDao.getAll().isEmpty())
        val otherEventName = "another event"
        val newUid = event_uid + "1"
        val testEventLocal2 = testEventLocal.copy(
                eventId = newUid,
                eventName = otherEventName)

        eventDao.insertAll(listOf(testEventLocal, testEventLocal2))

        val retrievedEvents = eventDao.getAll()
        assertEquals(retrievedEvents.size, 2)

        val retrievedFirstEvent = eventDao.getEventById(event_uid)[0]
        assertEquals(testEventLocal, retrievedFirstEvent)

        val retrievedSecondEvent = eventDao.getEventById(newUid)[0]
        assertEquals(testEventLocal2, retrievedSecondEvent)
    }

    @Test
    @Throws(Exception::class)
    fun testGettingNonExistentEvent() = runBlocking {
        // TODO: recheck (getEventById) returns list or single element?
        //val retrievedEvent = eventDao.getEventById("1")
        //assertEquals(retrievedEvent, null)
    }

}