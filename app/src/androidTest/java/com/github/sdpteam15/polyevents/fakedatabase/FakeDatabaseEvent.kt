package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import java.time.LocalDateTime

object FakeDatabaseEvent : EventDatabaseInterface {
    init {
        initEvents()
    }

    lateinit var events: MutableMap<String, Event>
    lateinit var eventEdits: MutableMap<String, Event>
    lateinit var ratings: MutableMap<String, Rating>

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
                tags = mutableListOf("sushi", "japan", "cooking")
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
        eventEdits = mutableMapOf()
        ratings = mutableMapOf("TEST" to Rating("TEST", 2f, "TEXT"))
    }

    override fun createEvent(event: Event): Observable<Boolean> {
        val eventId = FakeDatabase.generateRandomKey()
        val b = events.put(
            eventId,
            event.copy(eventId = eventId)
        ) == null
        return Observable(b, FakeDatabase)
    }

    override fun updateEvent(event: Event): Observable<Boolean> {
        events[event.eventId!!] = event
        return Observable(true, FakeDatabase)
    }

    override fun removeEvent(eventId: String): Observable<Boolean> {
        return Observable(events.remove(eventId) != null, FakeDatabase)
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>
    ): Observable<Boolean> {
        val event = events[id]
        if (event != null)
            returnEvent.postValue(event, this)
        return Observable(event != null, FakeDatabase)
    }


    override fun getEvents(
        eventList: ObservableList<Event>,
        limit: Long?,
        matcher: Matcher?
    ): Observable<Boolean> {
        eventList.clear(FakeDatabase)

        eventList.addAll(events.values, FakeDatabase)
        return Observable(true, FakeDatabase)
    }

    override fun createEventEdit(event: Event): Observable<Boolean> {
        eventEdits[FakeDatabase.generateRandomKey()] = event
        return Observable(true, FakeDatabase)
    }

    override fun updateEventEdit(event: Event): Observable<Boolean> {
        eventEdits[event.eventId!!] = event
        return Observable(true, FakeDatabase)
    }

    override fun removeEventEdit(eventId: String): Observable<Boolean> {
        return Observable(eventEdits.remove(eventId) != null, FakeDatabase)
    }

    override fun getEventEditFromId(
        id: String,
        returnEvent: Observable<Event>
    ): Observable<Boolean> {
        returnEvent.postValue(eventEdits[id]?.copy(eventId = id), FakeDatabase)
        return Observable(true, FakeDatabase)
    }

    override fun getEventEdits(
        eventList: ObservableList<Event>,
        matcher: Matcher?
    ): Observable<Boolean> {

        eventList.addAll(eventEdits.entries.map { it.value.copy(eventId = it.key) })
        return Observable(true, FakeDatabase)
    }

    override fun getRatingsForEvent(
        eventId: String,
        limit: Long?,
        ratingList: ObservableList<Rating>
    ): Observable<Boolean> {
        ratingList.addAll(ratings.filter { it.value.eventId == eventId }.entries.map {
            it.value.copy(
                ratingId = it.key
            )
        })
        return Observable(true, FakeDatabase)
    }

    override fun addRatingToEvent(rating: Rating): Observable<Boolean> {
        ratings[FakeDatabase.generateRandomKey()] = rating
        return Observable(true, FakeDatabase)
    }

    override fun removeRating(rating: Rating): Observable<Boolean> {
        ratings.remove(rating.ratingId)
        return Observable(true, FakeDatabase)
    }

    override fun updateRating(rating: Rating): Observable<Boolean> {
        ratings[rating.ratingId!!] = rating
        return Observable(true, FakeDatabase)
    }

    override fun getMeanRatingForEvent(
        eventId: String,
        mean: Observable<Float>
    ): Observable<Boolean> {
        mean.postValue(
            ratings.values.filter { it.eventId == eventId }.fold(
                Pair(0.0F, 0),
                { a, b ->
                    Pair(
                        (a.first * a.second + b.rate!!) / (a.second + 1),
                        a.second + 1
                    )
                }).first, FakeDatabase
        )
        return Observable(true, FakeDatabase)
    }

    override fun getUserRatingFromEvent(
        userId: String,
        eventId: String,
        returnedRating: Observable<Rating>
    ): Observable<Boolean> {
        returnedRating.postValue(ratings.entries.first { it.value.eventId == eventId && it.value.userId == userId }
            .let { it.value.copy(ratingId = it.key) }, FakeDatabase)
        return Observable(true, FakeDatabase)
    }

    override fun getEventsByZoneId(
        zoneId: String,
        limit: Long?,
        events: ObservableList<Event>
    ): Observable<Boolean> {
        events.addAll(this.events.entries.filter { it.value.zoneId == zoneId }
            .map { it.value.copy(eventId = it.key) })
        return Observable(true, FakeDatabase)
    }
}