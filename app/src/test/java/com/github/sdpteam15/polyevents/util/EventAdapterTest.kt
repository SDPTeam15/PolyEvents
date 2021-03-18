package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.HashMap

class EventAdapterTest {
    val eventName = "someEvent"
    val organizer = "Student Association"
    val zoneName = "Zone A"
    val description = "A nice little event"
    val icon = null

    val currentTime = Timestamp.now()
    val startTime = currentTime.toDate()
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

        event.addTag(tag1)
        event.addTag(tag2)
        event.addItem(Item.COCA, 2)
        event.addItem(Item.ELECTRIC_PLUG, 3)
    }

    @Test
    fun conversionOfEventToDocumentPreservesData() {
        val document = EventAdapter.toEventDocument(event)

        assertEquals(document["eventName"], event.eventName)
        assertEquals(document["organizer"], event.organizer)
        assertEquals(document["zoneName"], event.zoneName)
        assertEquals(document["icon"], event.icon)

        val storedEventInventory = document["inventory"] as MutableMap<String, Int>
        assertEquals(storedEventInventory["COCA"], event.inventory[Item.COCA])
        assertEquals(storedEventInventory["ELECTRIC_PLUG"], event.inventory[Item.ELECTRIC_PLUG])

        val storedEventTags = document["tags"] as List<String>
        assertTrue(storedEventTags.contains(tag1))
        assertTrue(storedEventTags.contains(tag2))
    }

    @Test
    fun conversionOfDocumentToEventPreservesData() {
        val tags = event.tags.toList()
        val inventory = event.inventory.mapKeys { it.key.toString() }
        val eventDocumentData: HashMap<String, Any?> = hashMapOf(
            "eventName" to event.eventName,
            "organizer" to event.organizer,
            "zoneName" to event.zoneName,
            "description" to event.description,
            "startTime" to currentTime,
            "endTime" to event.endTime,
            "tags" to tags,
            "inventory" to inventory
        )

        val obtainedEvent = EventAdapter.toEventEntity(eventDocumentData)
        assertEquals(event, obtainedEvent)
        assertEquals(obtainedEvent.inventory[Item.COCA], event.inventory[Item.COCA])
        assertTrue(obtainedEvent.tags.contains(tag1))
    }

    @Test
    fun testConversionWithNullValues() {
        val document = EventAdapter.toEventDocument(event)
        assertNull(document["endTime"])
    }

    private fun localDateTimeToDate(from: LocalDateTime) =
        Date.from(from.atZone(ZoneId.systemDefault()).toInstant())
}