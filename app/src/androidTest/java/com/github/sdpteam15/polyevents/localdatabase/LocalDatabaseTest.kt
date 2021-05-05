package com.github.sdpteam15.polyevents.localdatabase

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.EventLocal
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When
import java.io.IOException
import kotlin.test.assertEquals

class LocalDatabaseTest {
    private lateinit var localDatabase: LocalDatabase

    private lateinit var mockedRemoteDatabase: DatabaseInterface
    private lateinit var mockedEventDatabase: EventDatabaseInterface

    private lateinit var eventsLocalObservable: ObservableList<EventLocal>
    private lateinit var eventsObservable: ObservableList<Event>

    private lateinit var mockUser: UserEntity
    private lateinit var testEvent1: Event
    private lateinit var testEvent2: Event

    @Before
    fun createDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        localDatabase = Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()

        eventsLocalObservable = ObservableList()
        eventsObservable = ObservableList()

        testEvent1 = Event(
                eventId = "1",
                eventName = "event1"
        )

        testEvent2 = Event(
                eventId = "2",
                eventName = "event2"
        )
        testEvent2.makeLimitedEvent(5)

        mockUser = UserEntity(
                uid = "user1",
                username = "John"
        )

        mockedRemoteDatabase = mock(DatabaseInterface::class.java)
        mockedEventDatabase = mock(EventDatabaseInterface::class.java)

        When(mockedRemoteDatabase.eventDatabase).thenReturn(mockedEventDatabase)
        When(mockedRemoteDatabase.currentUser).thenReturn(mockUser)

        When(mockedEventDatabase.getEvents(
                eventList = anyOrNull(),
                matcher = anyOrNull(),
                userAccess = anyOrNull(),
                limit = anyOrNull()
        )).thenAnswer {
            Log.d("LocalDatabaseTest", "getEvents in test")
            // TODO: replace with observable list variable stored in local database
            (it.arguments[2] as ObservableList<Event>).addAll(listOf(testEvent1, testEvent2))
            Observable(true)
        }

        Database.currentDatabase = mockedRemoteDatabase
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        localDatabase.close()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun testPopulateLocalDatabaseWithEvents() = runBlocking {
        LocalDatabase.populateDatabaseWithUserEvents(localDatabase.eventDao(), this)

        val eventsRetrieved = localDatabase.eventDao().getAll()
        assertEquals(2, eventsRetrieved.size)

        val retrievedTestEvent1 = localDatabase.eventDao().getEventById(testEvent1.eventId!!)
        val retrievedTestEvent2 = localDatabase.eventDao().getEventById(testEvent2.eventId!!)

        assertEquals(EventLocal.fromEvent(testEvent1), retrievedTestEvent1)
        assertEquals(EventLocal.fromEvent(testEvent2), retrievedTestEvent2)

    }

    @Test
    fun testPopulateLocalDatabaseWithUserSettings() = runBlocking {

    }

}