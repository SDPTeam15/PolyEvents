package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_DESCRIPTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_DOCUMENT_ID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_END_TIME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_INVENTORY
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_MAX_SLOTS
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
object EventAdapter {
    /**
     * Convert an event entity to an intermediate mapping
     * of fields to their values, that we can pass to the document directly.
     * Firestore document keys are always strings.
     * @param event the entity we're converting
     * @return a hashmap of the entity fields to their values
     */
    fun toEventDocument(event: Event): HashMap<String, Any?> {
        val hash : HashMap<String, Any?> = hashMapOf(
            EVENT_NAME to event.eventName,
            EVENT_ORGANIZER to event.organizer,
            EVENT_ZONE_NAME to event.zoneName,
            EVENT_DESCRIPTION to event.description,
            // LocalDateTime instances can be directly stored to Firestore without need of conversion
            // UPDATE: LocalDateTime will not be stored as a Timestamp instance, instead it will be
            // stored as a hashmap representing the LocalDateTime instance
            EVENT_START_TIME to HelperFunctions.localDateTimeToDate(event.startTime),
            EVENT_END_TIME to HelperFunctions.localDateTimeToDate(event.endTime),
            EVENT_INVENTORY to event.inventory,
            EVENT_TAGS to event.tags.toList(),
            EVENT_MAX_SLOTS to event.maxNumberOfSlots
        )
        if(event.eventId != null){
            hash[EVENT_DOCUMENT_ID] = event.eventId
        }
        return hash
    }


    /**
     * Convert document data to an event entity in our model.
     * Data retrieved from Firestore documents are always of the form of a mutable mapping,
     * that maps strings - which are the names of the fields of our entity - to their values,
     * which can be of any type..
     * @param documentData this is the data we retrieve from the document.
     * @return the corresponding Event entity.
     */
    fun toEventEntity(documentData: MutableMap<String, Any?>, id:String): Event {
        return Event(
            eventId = id as String?,
            eventName = documentData[EVENT_NAME] as String?,
            organizer = documentData[EVENT_ORGANIZER] as String?,
            zoneName = documentData[EVENT_ZONE_NAME] as String?,
            description = documentData[EVENT_DESCRIPTION] as String?,
            startTime = HelperFunctions.dateToLocalDateTime(
                (documentData[EVENT_START_TIME] as Timestamp?)?.toDate()
            ),
            endTime = HelperFunctions.dateToLocalDateTime(
                // TODO: test if start time is null (remove ? from Timestamp)
                (documentData[EVENT_END_TIME] as Timestamp?)?.toDate()
            ),
            // TODO: Check how item is stored in Firestore, and check if conversion worked
            inventory = (documentData[EVENT_INVENTORY] as List<Item>).toMutableList(),
            tags = (documentData[EVENT_TAGS] as List<String>).toMutableSet(),
            maxNumberOfSlots = documentData[EVENT_MAX_SLOTS] as Int?
        )
    }
}