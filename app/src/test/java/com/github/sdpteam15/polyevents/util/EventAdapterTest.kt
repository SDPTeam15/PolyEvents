package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_DESCRIPTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_END_TIME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_ICON
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_INVENTORY
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_ORGANIZER
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_START_TIME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_TAGS
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_ZONE_NAME
import com.github.sdpteam15.polyevents.event.EVENT_DEFAULT_DURATION
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

        assertEquals(document[EVENT_NAME], event.eventName)
        assertEquals(document[EVENT_ORGANIZER], event.organizer)
        assertEquals(document[EVENT_ZONE_NAME], event.zoneName)
        assertEquals(document[EVENT_ICON], event.icon)

        val storedEventInventory = document[EVENT_INVENTORY] as MutableMap<String, Int>
        assertEquals(storedEventInventory["COCA"], event.inventory[Item.COCA])
        assertEquals(storedEventInventory["ELECTRIC_PLUG"], event.inventory[Item.ELECTRIC_PLUG])

        val storedEventTags = document[EVENT_TAGS] as List<String>
        assertTrue(storedEventTags.contains(tag1))
        assertTrue(storedEventTags.contains(tag2))
    }

    @Test
    fun conversionOfDocumentToEventPreservesData() {
        val tags = event.tags.toList()
        val inventory = event.inventory.mapKeys { it.key.toString() }
        val eventDocumentData: HashMap<String, Any?> = hashMapOf(
            EVENT_NAME to event.eventName,
            EVENT_ORGANIZER to event.organizer,
            EVENT_ZONE_NAME to event.zoneName,
            EVENT_DESCRIPTION to event.description,
            EVENT_START_TIME to currentTime,
            EVENT_END_TIME to event.endTime,
            EVENT_TAGS to tags,
            EVENT_INVENTORY to inventory
        )

        val obtainedEvent = EventAdapter.toEventEntity(eventDocumentData)
        assertEquals(event, obtainedEvent)
        assertEquals(obtainedEvent.inventory[Item.COCA], event.inventory[Item.COCA])
        assertTrue(obtainedEvent.tags.contains(tag1))
    }

    @Test
    fun testConversionWithNullValues() {
        val document = EventAdapter.toEventDocument(event)
        assertNull(document[EVENT_END_TIME])
    }

    private fun localDateTimeToDate(from: LocalDateTime) =
        Date.from(from.atZone(ZoneId.systemDefault()).toInstant())
}