package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_DESCRIPTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_DOCUMENT_ID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_END_TIME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_INVENTORY
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_ORGANIZER
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_START_TIME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_TAGS
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_ZONE_NAME
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
        EVENT_DOCUMENT_ID to element.eventId,
        EVENT_NAME to element.eventName,
        EVENT_ORGANIZER to element.organizer,
        EVENT_ZONE_NAME to element.zoneName,
        EVENT_DESCRIPTION to element.description,
        // LocalDateTime instances can be directly stored to Firestore without need of conversion
        // UPDATE: LocalDateTime will not be stored as a Timestamp instance, instead it will be
        // stored as a hashmap representing the LocalDateTime instance
        EVENT_START_TIME to HelperFunctions.LocalDateToTimeToDate(element.startTime),
        EVENT_END_TIME to HelperFunctions.LocalDateToTimeToDate(element.endTime),
        EVENT_INVENTORY to element.inventory,
        EVENT_TAGS to element.tags.toList()
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String) = Event(
        eventId = id,
        eventName = document[EVENT_NAME] as String?,
        organizer = document[EVENT_ORGANIZER] as String?,
        zoneName = document[EVENT_ZONE_NAME] as String?,
        description = document[EVENT_DESCRIPTION] as String?,
        startTime = HelperFunctions.DateToLocalDateTime(
            (document[EVENT_START_TIME] as Timestamp?)?.toDate()
        ),
        endTime = HelperFunctions.DateToLocalDateTime(
            // TODO: test if start time is null (remove ? from Timestamp)
            (document[EVENT_END_TIME] as Timestamp?)?.toDate()
        ),
        // TODO: Check how item is stored in Firestore, and check if conversion worked
        inventory = (document[EVENT_INVENTORY] as List<Item>).toMutableList(),
        tags = (document[EVENT_TAGS] as List<String>).toMutableSet()
    )
}