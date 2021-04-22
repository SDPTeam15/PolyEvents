package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.exceptions.MaxAttendeesException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

class EventTest {
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
    val endTime = null
    val tag1 = "GOOD"
    val tag2 = "BAD"

    lateinit var event: Event
    lateinit var eventWithNullStartTime: Event

    val userUid = "testUid"

    @Before
    fun setupEvent() {
        event = Event(
            eventId = eventId,
            eventName = eventName,
            organizer = organizer,
            zoneName = zoneName,
            description = description,
            icon = icon,
            startTime = startTime,
            endTime = endTime
        )

        eventWithNullStartTime = event.copy(
            startTime = null
        )
    }

    @Test
    fun testEventProperties() {
        assertEquals(event.eventId, eventId)
        assertEquals(event.eventName, eventName)
        assertEquals(event.description, description)
        assertEquals(event.organizer, organizer)
        assertEquals(event.zoneName, zoneName)
        assertEquals(event.icon, icon)
        assertEquals(event.startTime, startTime)
        assertEquals(event.endTime, endTime)
    }


    @Test
    fun testEventTags() {
        event.addTag(tag1)
        assertTrue(event.tags.contains(tag1))
        event.addTag(tag2)
        assertTrue(event.tags.contains(tag2))
    }

    @Test
    fun testRemoveEventTags() {
        event.addTag(tag1)
        event.removeTag(tag1)
        assertFalse(event.tags.contains(tag1))
    }

    @Test
    fun testAddItemToEventInventory() {
        val item = Item(null, "micro1", "MICROPHONE")
        event.addItem(item)
        assertTrue(event.hasItem(item))
    }

    @Test
    fun testAddRemoveItem() {
        val item = Item(null, "micro1", "MICROPHONE")
        event.addItem(item)
        event.removeItem(item)
        assertFalse(event.hasItem(item))
    }

    @Test
    fun testRemoveItemBasedOnItemEquality() {
        val itemId = "micro1"
        val itemType = "MICROPHONE"
        val item = Item(null, itemId, itemType)
        event.addItem(item)

        event.removeItem(Item(null, itemId, itemType))
        assertFalse(event.hasItem(item))
    }

    @Test
    fun formattedStartTimeReturnsCorrectTime() {
        assertEquals(event.formattedStartTime(), "18:30")
        val ret = eventWithNullStartTime.formattedStartTime()
        assertEquals(ret, "")
    }

    @Test
    fun checkTimeStampToLocalDateTimeConversion() {
        // TODO
    }

    @Test
    fun testMakeEventLimited() {
        assertNull(event.getMaxNumberOfSlots())
        event.makeLimitedEvent(3)
        assert(event.isLimitedEvent())
        assertEquals(event.getMaxNumberOfSlots(), 3)
    }

    @Test
    fun testAddAndRemoveParticipantsToEvent() {
        event.makeLimitedEvent(3)
        event.addParticipant(userUid)
        assertFalse(event.getParticipants().isEmpty())
        assert(event.getParticipants().contains(userUid))

        event.removeParticipant(userUid)
        assert(event.getParticipants().isEmpty())
        assertFalse(event.getParticipants().contains(userUid))

        event.addParticipant(userUid)
        event.addParticipant("user2")
        assertEquals(event.getParticipants().size, 2)

        event.clearParticipants()
        assert(event.getParticipants().isEmpty())

        // No exceptions thrown
        event.removeParticipant(userUid)
    }

    @Test(expected = MaxAttendeesException::class)
    fun testAddParticipantsToAnAlreadyFullEvent() {
        event.makeLimitedEvent(1)
        event.addParticipant(userUid)

        event.addParticipant("user2")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddParticipantToANotLimitedEventShouldThrowException() {
        event.addParticipant((userUid))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMakeLimitedEventWithNegativeNumberThrowsException() {
        event.makeLimitedEvent(-1)
    }

}