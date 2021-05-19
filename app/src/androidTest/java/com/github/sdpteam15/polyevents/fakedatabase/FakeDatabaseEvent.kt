package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import java.time.LocalDateTime

object FakeDatabaseEvent : EventDatabaseInterface {
    init {
        initEvents()
    }

    lateinit var events: MutableMap<String, Event>
    private fun initEvents() {
        events = mutableMapOf()
        events["event1"] =
            Event(
                eventId = "event1",
                eventName = "Sushi demo",
                description = "Super hungry activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                organizer = "The fish band",
                zoneName = "Kitchen",
                tags = mutableSetOf("sushi", "japan", "cooking")
            )


        events["event2"] =
            Event(
                eventId = "event2",
                eventName = "Saxophone demo",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
                organizer = "The music band",
                zoneName = "Concert Hall"
            )


        events["event3"] =
            Event(
                eventId = "event3",
                eventName = "Aqua Poney",
                description = "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
                organizer = "The Aqua Poney team",
                zoneName = "Swimming pool",
            )
        events["event3"]!!.makeLimitedEvent(3)

    }

    override fun createEvent(event: Event, userAccess: UserProfile?): Observable<Boolean> {
        val eventId = FakeDatabase.generateRandomKey()
        val b = events.put(
            eventId,
            event.copy(eventId = eventId)
        ) == null
        return Observable(b, this)
    }

    override fun updateEvent(event: Event, userAccess: UserProfile?): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (event.eventId == null) return createEvent(event, profile)
        events[event.eventId!!] = event
        return Observable(true, this)
    }

    override fun removeEvent(eventId: String, userAccess: UserProfile?): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val event = events[id]
        if (event != null)
            returnEvent.postValue(event, this)
        return Observable(event != null, this)
    }


    override fun getEvents(
        matcher: Matcher?,
        limit: Long?,
        eventList: ObservableList<Event>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        eventList.clear(this)

        eventList.addAll(events.values, this)
        return Observable(true, this)
    }

    override fun getRatingsForEvent(
        eventId: String,
        limit: Long?,
        ratingList: ObservableList<Rating>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        ratingList.add(Rating("TEST", 2f, "TEXT"))
        return Observable(true)
    }

    override fun addRatingToEvent(rating: Rating, userAccess: UserProfile?): Observable<Boolean> {
        return Observable(true)
    }

    override fun removeRating(rating: Rating, userAccess: UserProfile?): Observable<Boolean> {
        return Observable(true)
    }

    override fun updateRating(rating: Rating, userAccess: UserProfile?): Observable<Boolean> {
        return Observable(true)
    }

    override fun getMeanRatingForEvent(
        eventId: String,
        mean: Observable<Float>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        mean.postValue(4f, this)
        return Observable(true)
    }

    override fun getUserRatingFromEvent(
        userId: String,
        eventId: String,
        returnedRating: Observable<Rating>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return Observable(true)
    }

    override fun getEventsByZoneId(
        zoneId: String,
        limit: Long?,
        events: ObservableList<Event>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return Observable(true)
    }
}