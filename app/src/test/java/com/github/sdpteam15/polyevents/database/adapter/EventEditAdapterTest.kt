package com.github.sdpteam15.polyevents.database.adapter


import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.EventConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventEditAdapter
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class EventEditAdapterTest {
    val eventId = "xxxEventxxx"
    val eventName = "someEvent"
    val organizer = "Student Association"
    val zoneName = "Zone A"
    val zoneId = "id"
    val description = "A nice little event"
    val icon = null

    val startTime = LocalDateTime.now()
    val endTime = null

    val tag1 = "GOOD"
    val tag2 = "BAD"
    val eventEditId = "1"
    val eventEditMessage = "adsasdas"
    val status = Event.EventStatus.ACCEPTED


    lateinit var event: Event

    @Before
    fun setupEvent() {
        event = Event(
            eventId = eventId,
            eventName = eventName,
            organizer = organizer,
            zoneName = zoneName,
            zoneId = zoneId,
            description = description,
            icon = icon,
            startTime = startTime,
            endTime = endTime,
            adminMessage = eventEditMessage,
            status = status,
            eventEditId = eventEditId
        )

        event.addTag(tag1)
        event.addTag(tag2)
    }

    @Test
    fun conversionOfEventToDocumentPreservesData() {
        val document = EventEditAdapter.toDocument(event)

        assertEquals(document[EVENT_NAME.value], event.eventName)
        assertEquals(document[EVENT_ZONE_ID.value], event.zoneId)
        assertEquals(document[EVENT_ORGANIZER.value], event.organizer)
        assertEquals(document[EVENT_ZONE_NAME.value], event.zoneName)
        assertEquals(document[EVENT_ICON.value], event.icon)
        assertEquals(
            document[DatabaseConstant.EventEditConstant.EVENT_EDIT_ADMIN_MESSAGE.value],
            event.adminMessage
        )
        assertEquals(
            document[EVENT_DOCUMENT_ID.value],
            event.eventId
        )
        assertEquals(
            document[DatabaseConstant.EventEditConstant.EVENT_EDIT_STATUS.value],
            event.status!!.ordinal
        )


        val storedEventTags = document[EVENT_TAGS.value] as List<String>
        assertTrue(storedEventTags.contains(tag1))
        assertTrue(storedEventTags.contains(tag2))
    }

    @Test
    fun conversionOfDocumentToEventPreservesData() {
        val tags = event.tags.toList()

        val testEventWithoutTimes = event.copy(
            startTime = null
        )

        val eventDocumentData: HashMap<String, Any?> = hashMapOf(
            EVENT_NAME.value to testEventWithoutTimes.eventName,
            EVENT_ORGANIZER.value to testEventWithoutTimes.organizer,
            EVENT_ZONE_ID.value to testEventWithoutTimes.zoneId,
            EVENT_ZONE_NAME.value to testEventWithoutTimes.zoneName,
            EVENT_DESCRIPTION.value to testEventWithoutTimes.description,
            EVENT_START_TIME.value to testEventWithoutTimes.startTime,
            EVENT_END_TIME.value to testEventWithoutTimes.endTime,
            EVENT_TAGS.value to tags,
            EVENT_DOCUMENT_ID.value to event.eventId,
            DatabaseConstant.EventEditConstant.EVENT_EDIT_ADMIN_MESSAGE.value to event.adminMessage,
            DatabaseConstant.EventEditConstant.EVENT_EDIT_STATUS.value to event.status!!.ordinal.toLong(),
            EVENT_INVENTORY.value to testEventWithoutTimes.inventory,
            EVENT_LIMITED.value to testEventWithoutTimes.isLimitedEvent(),
            EVENT_MAX_SLOTS.value to testEventWithoutTimes.getMaxNumberOfSlots(),
            EVENT_PARTICIPANTS.value to testEventWithoutTimes.getParticipants().toList()
        )

        val obtainedEvent =
            EventEditAdapter.fromDocument(eventDocumentData, testEventWithoutTimes.eventEditId!!)
        assertEquals(testEventWithoutTimes, obtainedEvent)
    }
}