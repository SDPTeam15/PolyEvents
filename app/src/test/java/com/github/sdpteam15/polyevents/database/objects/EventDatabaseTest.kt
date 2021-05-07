package com.github.sdpteam15.polyevents.database.objects

import android.util.Log
import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.RATING_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.RatingAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.firebase.firestore.Query
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private const val eventId = "event ID"
private const val ratingId = "rating id"
private const val eventName = "event name"
private const val eventDesc = "event desc"
private const val organizer = "The fish band"
private const val zoneName = "Kitchen"
private const val zoneId = "KitchenId"
private const val userId = "UserId"
private const val rate = 4.5F
private const val feedback = "feedback "
private val startTime = LocalDateTime.of(2021, 3, 7, 12, 15)
private val endTime = LocalDateTime.of(2021, 3, 7, 12, 45)
private val tags = mutableSetOf("sushi", "japan", "cooking")


@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
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
    fun addEvent() {
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
        mockedEventdatabase.getEvents(null, null, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(events, getList.element)
        assert(getList.matcher != null)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }

    @Test
    fun updateRating() {
        val rating = Rating(
            ratingId = ratingId,
            eventId = eventId,
            userId = userId,
            rate = rate,
            feedback = feedback
        )
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.updateRating(rating, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.setEntityQueue.poll()!!

        assertEquals(rating, set.element)
        assertEquals(rating.ratingId, set.id)
        assertEquals(RATING_COLLECTION, set.collection)
        assertEquals(RatingAdapter, set.adapter)
    }

    @Test
    fun addRating() {
        val rating = Rating(
            ratingId = ratingId,
            eventId = eventId,
            userId = userId,
            rate = rate,
            feedback = feedback
        )
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.addRatingToEvent(rating, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.addEntityQueue.poll()!!

        assertEquals(rating, set.element)
        assertEquals(RATING_COLLECTION, set.collection)
        assertEquals(RatingAdapter, set.adapter)
    }

    @Test
    fun getRatingList() {
        val ratings = ObservableList<Rating>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getRatingsForEvent(eventId, null, ratings, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(ratings, getList.element)
        assertNotNull(getList.matcher)
        assertEquals(RATING_COLLECTION, getList.collection)
        assertEquals(RatingAdapter, getList.adapter)
    }

    @Test
    fun getEventListWithMatcher() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")
        val matcher = Matcher{ q: Query -> q.limit(1000L) }

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getEvents(matcher, null, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(events, getList.element)
        assertNotNull(getList.matcher)
        val mockQuery = Mockito.mock(Query::class.java)
        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            assertEquals(it.arguments[0] as Long,1000L)
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }
    @Test
    fun getEventListWithMatcherAndLimitNotNull() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")
        val matcher = Matcher{ q: Query -> q.limitToLast(1000L) }

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getEvents(matcher, 20, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        val mockQuery = Mockito.mock(Query::class.java)
        assertNotNull(getList.matcher)

        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            assertEquals(it.arguments[0] as Long,20L)
            mockQuery
        }
        Mockito.`when`(mockQuery.limitToLast(anyOrNull())).then {
            assertEquals(it.arguments[0] as Long,1000L)
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(events, getList.element)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }

    @Test
    fun removeRating() {
        val rating = Rating(
            ratingId = ratingId,
            eventId = eventId,
            userId = userId,
            rate = rate,
            feedback = feedback
        )
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.removeRating(rating, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val del = HelperTestFunction.deleteEntityQueue.poll()!!

        assertEquals(rating.ratingId, del.id)
        assertEquals(RATING_COLLECTION, del.collection)
    }

    @Test
    fun getRatingWithoutLimit() {
        val ratings = ObservableList<Rating>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getRatingsForEvent(eventId, null, ratings, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val getList = HelperTestFunction.getListEntityQueue.poll()!!
        val mockQuery = Mockito.mock(Query::class.java)
        assertNotNull(getList.matcher)

        assertEquals(ratings, getList.element)
        assertEquals(RATING_COLLECTION, getList.collection)
        assertEquals(RatingAdapter, getList.adapter)
    }
    @Test
    fun getRatingWithLimit() {
        val ratings = ObservableList<Rating>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getRatingsForEvent(eventId, 100L, ratings, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(ratings, getList.element)
        assertNotNull(getList.matcher)
        assertEquals(RATING_COLLECTION, getList.collection)
        assertEquals(RatingAdapter, getList.adapter)
    }
/*
    @Test
    fun getRatingMean() {
        val mean = Observable<Float>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getMeanRatingForEvent(eventId, mean, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


    }*/

    @Test
    fun getEventFromId() {
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