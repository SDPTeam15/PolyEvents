package com.github.sdpteam15.polyevents.util


import com.github.sdpteam15.polyevents.database.DatabaseConstant.EventConstant.*
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.google.firebase.Timestamp

// TODO: Save icon bitmap in Google cloud storage
/**
 * A class for converting between event entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of Event.
 */
object EventAdapter : AdapterInterface<Event> {
    override fun toDocument(element: Event): HashMap<String, Any?> = hashMapOf(
        EVENT_DOCUMENT_ID.value to element.eventId,
        EVENT_NAME.value to element.eventName,
        EVENT_ORGANIZER.value to element.organizer,
        EVENT_ZONE_NAME.value to element.zoneName,
        EVENT_DESCRIPTION.value to element.description,
        // LocalDateTime instances can be directly stored to the database without need of conversion
        // UPDATE: LocalDateTime will not be stored as a Timestamp instance, instead it will be
        // stored as a hashmap representing the LocalDateTime instance
        EVENT_START_TIME.value to HelperFunctions.LocalDateToTimeToDate(element.startTime),
        EVENT_END_TIME.value to HelperFunctions.LocalDateToTimeToDate(element.endTime),
        EVENT_INVENTORY.value to element.inventory,
        EVENT_TAGS.value to element.tags.toList()
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String) = Event(
        eventId = id,
        eventName = document[EVENT_NAME.value] as String?,
        organizer = document[EVENT_ORGANIZER.value] as String?,
        zoneName = document[EVENT_ZONE_NAME.value] as String?,
        description = document[EVENT_DESCRIPTION.value] as String?,
        startTime = HelperFunctions.DateToLocalDateTime(
            (document[EVENT_START_TIME.value] as Timestamp?)?.toDate()
        ),
        endTime = HelperFunctions.DateToLocalDateTime(
            // TODO: test if start time is null (remove ? from Timestamp)
            (document[EVENT_END_TIME.value] as Timestamp?)?.toDate()
        ),
        // TODO: Check how item is stored in Firestore, and check if conversion worked
        inventory = (document[EVENT_INVENTORY.value] as List<Item>).toMutableList(),
        tags = (document[EVENT_TAGS.value] as List<String>).toMutableSet()
    )
}