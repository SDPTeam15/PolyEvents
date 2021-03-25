package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.exceptions.InsufficientAmountException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class EventTest {
    val eventName = "someEvent"
    val organizer = "Student Association"
    val zoneName = "Zone A"
    val description = "A nice little event"
    val icon = null
    val startTime =
        localDateTimeToDate(LocalDateTime.of(
            2021, 3, 18, 18, 30
        ))
    val endTime = null
    val tag1 = "GOOD"
    val tag2 = "BAD"

    lateinit var event: Event
    lateinit var eventWithNullStartTime: Event
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

        eventWithNullStartTime =  Event(
            eventName = eventName,
            organizer = organizer,
            zoneName = zoneName,
            description = description,
            icon = icon,
            startTime = null,
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
    fun testAddItemToInventory() {
        val currentAmountOfCoca = event.inventory.getOrDefault(Item.COCA, 0)
        event.addItem(Item.COCA)
        assertEquals(event.inventory.get(Item.COCA), currentAmountOfCoca + 1)

        val currentAmountOfPlugs = event.inventory.getOrDefault(Item.ELECTRIC_PLUG, 0)
        event.addItem(Item.ELECTRIC_PLUG, 2)
        assertEquals(event.inventory.get(Item.ELECTRIC_PLUG), currentAmountOfPlugs + 2)
    }

    @Test
    fun testTakeItemRemovesItemFromInventory() {
        event.setItemAmount(Item.COCA, 1)
        event.takeItem(Item.COCA)
        assertEquals(event.inventory[Item.COCA], 0)

        event.setItemAmount(Item.ELECTRIC_PLUG, 3)
        val currentAmount = event.inventory.getOrDefault(Item.ELECTRIC_PLUG, 0)
        event.takeItem(Item.ELECTRIC_PLUG, 2)
        assertEquals(event.inventory[Item.ELECTRIC_PLUG], currentAmount - 2)
    }

    @Test(expected=IllegalArgumentException::class)
    fun settingItemAmountWithNegativeNumberShouldThrowException() {
        event.setItemAmount(Item.COCA, -3)
    }

    @Test(expected = InsufficientAmountException::class)
    fun takingItemNotAvailableThrowsException() {
        val currentAmount = 0
        event.inventory.put(Item.COCA, 0)
        event.takeItem(Item.COCA, currentAmount + 1)
    }

    @Test
    fun formattedStartTimeReturnsCorrectTime() {
        assertEquals(event.formattedStartTime(), "18:30")
        val ret =eventWithNullStartTime.formattedStartTime()
        assertEquals(ret, "")
    }

    private fun localDateTimeToDate(from: LocalDateTime) =
        Date.from(from.atZone(ZoneId.systemDefault()).toInstant())

}