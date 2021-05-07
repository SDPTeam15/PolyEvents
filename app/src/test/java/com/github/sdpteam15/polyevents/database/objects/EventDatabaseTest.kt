package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
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
private const val rate = 4.5
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
        assert(getList.matcher != null)
        assertEquals(RATING_COLLECTION, getList.collection)
        assertEquals(RatingAdapter, getList.adapter)
    }

    @Test
    fun getEventListWithMatcher() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")
        val matcher = Matcher{ q: Query -> q.limit(1000) }

        HelperTestFunction.nextBoolean(true)
        mockedEventdatabase.getEvents(matcher, null, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(events, getList.element)
        assertNotNull(getList.matcher)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }

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