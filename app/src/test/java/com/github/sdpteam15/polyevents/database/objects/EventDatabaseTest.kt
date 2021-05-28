package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.*
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.RATING_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventEditAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.RatingAdapter
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Query
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
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
private val tags = mutableListOf("sushi", "japan", "cooking")

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class EventDatabaseTest {
    lateinit var mockedEventdatabase: EventDatabase


    @Before
    fun setup() {
        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
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

        HelperTestFunction.nextSetEntity { true }
        mockedEventdatabase.updateEvent(event, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastSetEntity()!!

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

        HelperTestFunction.nextAddEntity { true }
        mockedEventdatabase.createEvent(event, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastAddEntity()!!

        assertEquals(event, set.element)
        assertEquals(EVENT_COLLECTION, set.collection)
        assertEquals(EventAdapter, set.adapter)
    }

    @Test
    fun getEventList() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getEvents(null, null, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(events, getList.element)
        assert(getList.matcher != null)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }


    @Test
    fun updateEventEdit() {
        val event = Event(
            eventId = eventId,
            eventName = eventName,
            description = eventDesc,
            organizer = organizer,
            zoneName = zoneName,
            zoneId = zoneId,
            startTime = startTime,
            endTime = endTime,
            tags = tags,
            eventEditId = eventId
        )
        val userAccess = UserProfile()

        HelperTestFunction.nextSetEntity { true }
        mockedEventdatabase.updateEventEdit(event, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(event, set.element)
        assertEquals(event.eventEditId, set.id)
        assertEquals(DatabaseConstant.CollectionConstant.EVENT_EDIT_COLLECTION, set.collection)
        assertEquals(EventEditAdapter, set.adapter)
    }

    @Test
    fun addEventEdit() {
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

        HelperTestFunction.nextAddEntity { true }
        mockedEventdatabase.createEventEdit(event, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastAddEntity()!!

        assertEquals(event, set.element)
        assertEquals(DatabaseConstant.CollectionConstant.EVENT_EDIT_COLLECTION, set.collection)
        assertEquals(EventEditAdapter, set.adapter)
    }

    @Test
    fun getEventEditList() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getEventEdits(null, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(events, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.EVENT_EDIT_COLLECTION, getList.collection)
        assertEquals(EventEditAdapter, getList.adapter)
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

        HelperTestFunction.nextSetEntity { true }
        mockedEventdatabase.updateRating(rating, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastSetEntity()!!

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

        HelperTestFunction.nextAddEntity { true }
        mockedEventdatabase.addRatingToEvent(rating, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastAddEntity()!!

        assertEquals(rating, set.element)
        assertEquals(RATING_COLLECTION, set.collection)
        assertEquals(RatingAdapter, set.adapter)
    }

    @Test
    fun getRatingList() {
        val ratings = ObservableList<Rating>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getRatingsForEvent(eventId, null, ratings, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(ratings, getList.element)
        assertNotNull(getList.matcher)
        assertEquals(RATING_COLLECTION, getList.collection)
        assertEquals(RatingAdapter, getList.adapter)
    }

    @Test
    fun getEventListWithMatcher() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")
        val matcher = Matcher { q: Query -> q.limit(1000L) }

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            assertEquals(it.arguments[0] as Long, 1000L)
            mockQuery
        }

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getEvents(matcher, null, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(events, getList.element)
        assertNotNull(getList.matcher)

        getList.matcher.match(mockQuery)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }

    @Test
    fun getEventListWithMatcherAndLimitNotNull() {
        val events = ObservableList<Event>()
        val userAccess = UserProfile("uid")
        val matcher = Matcher { q: Query -> q.limit(1000L) }

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getEvents(matcher, 20, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            assert(it.arguments[0] as Long in listOf(20L, 1000L))
            mockQuery
        }

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getEvents(matcher, 20, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        assertNotNull(getList.matcher)

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

        HelperTestFunction.nextSetEntity { true }
        mockedEventdatabase.removeRating(rating, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val del = HelperTestFunction.lastDeleteEntity()!!

        assertEquals(rating.ratingId, del.id)
        assertEquals(RATING_COLLECTION, del.collection)
    }


    @Test
    fun getCurrentUserReturnCorrectOne() {
        val mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(UserEntity(""))
        Database.currentDatabase = mockedDatabase
        assertEquals(mockedEventdatabase.currentUser, UserEntity(""))
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun getRatingWithoutLimit() {
        val ratings = ObservableList<Rating>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetEntity { true }
        mockedEventdatabase.getRatingsForEvent(eventId, null, ratings, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)
        assertNotNull(getList.matcher)

        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull(), anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(ratings, getList.element)
        assertEquals(RATING_COLLECTION, getList.collection)
        assertEquals(RatingAdapter, getList.adapter)
    }

    @Test
    fun getRatingWithLimit() {
        val ratings = ObservableList<Rating>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getRatingsForEvent(eventId, 20L, ratings, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)
        assertNotNull(getList.matcher)

        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            assertEquals(it.arguments[0] as Long, 20L)
            mockQuery
        }
        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull<String>(), anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(ratings, getList.element)
        assertEquals(RATING_COLLECTION, getList.collection)
        assertEquals(RatingAdapter, getList.adapter)
    }

    @Test
    fun getRatingMean() {
        val mean = Observable<Float>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity {
            it.element.add(Rating(rate = 1.0F))
            it.element.add(Rating(rate = 2.0F))

            true
        }
        mockedEventdatabase.getMeanRatingForEvent(eventId, mean, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull(), anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(mean.value, 1.5F)
    }

    @Test
    fun getRatingZeroMean() {
        val mean = Observable<Float>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity {
            true
        }
        mockedEventdatabase.getMeanRatingForEvent(eventId, mean, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull<String>(), anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(mean.value, 0.0F)
    }

    @Test
    fun getRatingMeanFailedDoesntChange() {
        val mean = Observable<Float>(-1.0F)
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { false }
        mockedEventdatabase.getMeanRatingForEvent(eventId, mean, userAccess)
            .observeOnce { assert(!it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull<String>(), anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(mean.value, -1.0F)
    }

    @Test
    fun getUserRatingFromEventFailedDoesntChange() {
        val rat = Observable(Rating("default", 0.0F))
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { false }
        mockedEventdatabase.getUserRatingFromEvent(userId, eventId, rat, userAccess)
            .observeOnce { assert(!it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull<String>(), anyOrNull())).then {
            mockQuery
        }
        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(rat.value!!.ratingId, "default")
        assertEquals(rat.value!!.rate, 0.0F)
    }

    @Test
    fun getUserRatingFromEventFailedToGetAnyEvent() {
        val rat = Observable(Rating("default", 0.0F))
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getUserRatingFromEvent(userId, eventId, rat, userAccess)
            .observeOnce { assert(!it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull<String>(), anyOrNull())).then {
            mockQuery
        }
        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(rat.value!!.ratingId, "default")
        assertEquals(rat.value!!.rate, 0.0F)
    }

    @Test
    fun getUserRatingFromEventRetrieveCorrectRating() {
        val rat = Observable(Rating("default", 0.0F))
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity {
            it.element.add(Rating(ratingId = "default", rate = 3.5F))
            true
        }
        mockedEventdatabase.getUserRatingFromEvent(userId, eventId, rat, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val getList = HelperTestFunction.lastGetListEntity()!!
        assertNotNull(getList.matcher)

        val mockQuery = Mockito.mock(Query::class.java)

        Mockito.`when`(mockQuery.whereEqualTo(anyOrNull<String>(), anyOrNull())).then {
            mockQuery
        }
        Mockito.`when`(mockQuery.limit(anyOrNull())).then {
            mockQuery
        }
        getList.matcher.match(mockQuery)
        assertEquals(rat.value!!.ratingId, "default")
        assertEquals(rat.value!!.rate, 3.5F)
    }


    @Test
    fun getEventFromId() {
        val events = Observable<Event>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetEntity { true }
        mockedEventdatabase.getEventFromId(eventId, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getEntity = HelperTestFunction.lastGetEntity()!!

        assertEquals(events, getEntity.element)
        assertEquals(eventId, getEntity.id)
        assertEquals(EVENT_COLLECTION, getEntity.collection)
        assertEquals(EventAdapter, getEntity.adapter)
    }

    @Test
    fun getEventEditFromId() {
        val events = Observable<Event>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetEntity { true }
        mockedEventdatabase.getEventEditFromId(eventId, events, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getEntity = HelperTestFunction.lastGetEntity()!!

        assertEquals(events, getEntity.element)
        assertEquals(eventId, getEntity.id)
        assertEquals(
            DatabaseConstant.CollectionConstant.EVENT_EDIT_COLLECTION,
            getEntity.collection
        )
        assertEquals(EventEditAdapter, getEntity.adapter)
    }

    @Test
    fun testGetEventsByZoneId() {
        val events = ObservableList<Event>()
        HelperTestFunction.nextGetListEntity { true }
        mockedEventdatabase.getEventsByZoneId(zoneId, null, events)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(events, getList.element)
        assertNotNull(getList.matcher)
        assertEquals(EVENT_COLLECTION, getList.collection)
        assertEquals(EventAdapter, getList.adapter)
    }
}