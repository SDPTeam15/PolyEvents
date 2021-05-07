package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import kotlin.test.assertEquals

private const val eventId = "event ID"
private const val ratingId = "rating id"
private const val eventName = "event name"
private const val eventDesc = "event desc"
private const val organizer = "The fish band"
private const val zoneName = "Kitchen"
private const val zoneId = "KitchenId"
private val startTime = LocalDateTime.of(2021, 3, 7, 12, 15)
private val endTime = LocalDateTime.of(2021, 3, 7, 12, 45)
private val tags = mutableSetOf("sushi", "japan", "cooking")


@Suppress("UNCHECKED_CAST")
class EventDatabaseTest {
    lateinit var mockedEventdatabase: EventDatabase


    @Before
    fun setup() {
        val mockDatabaseInterface = HelperTestFunction.mockFor()
        mockedEventdatabase = EventDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun updateEvent() {
        val event = Event(
            eventId = eventId,
            eventName = eventName,
            description = eventDesc,
            organizer = organizer,
            zoneName = zoneName,
            zoneId = zoneId,
            startTime = startTime,
            endTime = endTime,
            tags = tags
        )
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.updateEvents(event, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.setEntityQueue.poll()!!

        assertEquals(event, set.element)
        assertEquals(event.eventId, set.id)
        assertEquals(EVENT_COLLECTION, set.collection)
        assertEquals(EventAdapter, set.adapter)
    }

    @Test
    fun addEvent(){
        val event = Event(
            eventId = eventId,
            eventName = eventName,
            description = eventDesc,
            organizer = organizer,
            zoneName = zoneName,
            zoneId = zoneId,
            startTime = startTime,
            endTime = endTime,
            tags = tags
        )
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.createEvent(event, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.addEntityQueue.poll()!!

        assertEquals(event, set.element)
        assertEquals(EVENT_COLLECTION, set.collection)
        assertEquals(EventAdapter, set.adapter)
    }

    @Test
    fun getEventList() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getEvents(null, null,events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(events, getList.element)
        assert(getList.matcher!=null)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }

    @Test
    fun getEventFromId(){
        val events = Observable<Event>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getEventFromId(eventId, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getEntity = HelperTestFunction.getEntityQueue.poll()!!

        assertEquals(events, getEntity.element)
        assertEquals(eventId, getEntity.id)
        assertEquals(EVENT_COLLECTION, getEntity.collection)
        assertEquals(EventAdapter, getEntity.adapter)
    }
}