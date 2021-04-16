package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import java.time.LocalDateTime

object FakeDatabaseEvent:EventDatabaseInterface {
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
                zoneName = "Swimming pool"
            )

    }

    override fun createEvent(event: Event, profile: UserProfile?): Observable<Boolean> {
        val eventId = FakeDatabase.generateRandomKey()
        val b = events.put(
            eventId,
            Event(
                eventId,
                event.eventName,
                event.organizer,
                event.zoneName,
                event.description,
                event.icon,
                event.startTime,
                event.endTime,
                event.inventory,
                event.tags
            )
        ) == null
        return Observable(b, this)
    }

    override fun updateEvents(event: Event, profile: UserProfile?): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (event.eventId == null) return createEvent(event, profile)
        events[event.eventId!!] = event
        return Observable(true, this)
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        profile: UserProfile?
    ): Observable<Boolean> {
        val event = events[id]
        if (event != null)
            returnEvent.postValue(event, this)
        return Observable(event != null, this)
    }


    override fun getListEvent(
        matcher: Matcher?,
        number: Long?,
        eventList: ObservableList<Event>,
        profile: UserProfile?
    ): Observable<Boolean> {
        eventList.clear(this)

        eventList.addAll(events.values, this)
        return Observable(true, this)
    }
}