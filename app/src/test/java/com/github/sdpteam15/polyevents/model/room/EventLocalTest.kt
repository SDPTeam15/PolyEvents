package com.github.sdpteam15.polyevents.model.room

import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.entity.Event
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class EventLocalTest {
    val eventId = "xxxEventxxx"
    val eventName = "someEvent"
    val organizer = "Student Association"
    val zoneName = "Zone A"
    val description = "A nice little event"
    val icon = null
    val startTime =
        LocalDateTime.of(
            2021, 3, 18, 18, 30
        )
    val endTime: LocalDateTime? = null
    val tag1 = "GOOD"
    val tag2 = "BAD"

    private lateinit var event: Event
    private lateinit var localEvent: EventLocal

    @Before
    fun setup() {
        event = Event(
            eventId = eventId,
            eventName = eventName,
            organizer = organizer,
            zoneName = zoneName,
            description = description,
            icon = icon,
            startTime = startTime,
            endTime = endTime,
            tags = mutableSetOf(tag1, tag2)
        )

        localEvent = EventLocal(
            eventId = eventId,
            eventName = eventName,
            organizer = organizer,
            zoneName = zoneName,
            description = description,
            startTime = startTime,
            endTime = endTime
        )
    }

    @Test
    fun testEventLocalCorrectlyConstructed() {
        assertEquals(localEvent.eventId, eventId)
        assertEquals(localEvent.eventName, eventName)
        assertEquals(localEvent.description, description)
        assertEquals(localEvent.zoneName, zoneName)
        assertEquals(localEvent.organizer, organizer)
        assertEquals(localEvent.startTime, startTime)
        assertEquals(localEvent.endTime, endTime)
    }

    @Test
    fun testEventToEventLocalConversionWorks() {
        assertEquals(EventLocal.fromEvent(event), localEvent)
    }

    @Test
    fun testEventLocalToEventConversionWorks() {
        val eventObtained = localEvent.toEvent()
        assertEquals(eventObtained, localEvent.toEvent())
    }
}