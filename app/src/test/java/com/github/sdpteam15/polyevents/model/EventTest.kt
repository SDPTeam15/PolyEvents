package com.github.sdpteam15.polyevents.model

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class EventTest {
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

    @Before
    fun setupEvent() {
        event = Event(
            eventName = eventName,
            organizer = organizer,
            zoneName = zoneName,
            description = description,
            icon = icon,
            startTime = startTime,
            endTime = endTime
        )
    }

    @Test
    fun testEventProperties() {
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
        val item = Item("micro1", ItemType.MICROPHONE)
        event.addItem(item)
        assertTrue(event.hasItem(item))
    }

    @Test
    fun testAddRemoveItem() {
        val item = Item("micro1", ItemType.MICROPHONE)
        event.addItem(item)
        event.removeItem(item)
        assertFalse(event.hasItem(item))
    }

    @Test
    fun testRemoveItemBasedOnItemEquality() {
        val itemId = "micro1"
        val itemType = ItemType.MICROPHONE
        val item = Item(itemId, itemType)
        event.addItem(item)

        event.removeItem(Item(itemId, itemType))
        assertFalse(event.hasItem(item))
    }

    @Test
    fun formattedStartTimeReturnsCorrectTime() {
        assertEquals(event.formattedStartTime(), "18:30")
    }

    @Test
    fun checkTimeStampToLocalDateTimeConversion() {
        // TODO
    }

}