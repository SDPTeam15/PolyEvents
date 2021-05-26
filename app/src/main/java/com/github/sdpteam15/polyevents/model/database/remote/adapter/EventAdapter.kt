package com.github.sdpteam15.polyevents.model.database.remote.adapter


import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.EventConstant.*
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import com.google.firebase.Timestamp

// TODO: Save icon bitmap in Google cloud storage
/**
 * A class for converting between event entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of Event.
 */
@Suppress("UNCHECKED_CAST")
object EventAdapter : AdapterInterface<Event> {
    override fun toDocument(element: Event): HashMap<String, Any?> = hashMapOf(
        EVENT_NAME.value to element.eventName,
        EVENT_ORGANIZER.value to element.organizer,
        EVENT_ZONE_ID.value to element.zoneId,
        EVENT_ZONE_NAME.value to element.zoneName,
        EVENT_DESCRIPTION.value to element.description,
        // LocalDateTime instances can be directly stored to the database without need of conversion
        // UPDATE: LocalDateTime will not be stored as a Timestamp instance, instead it will be
        // stored as a hashmap representing the LocalDateTime instance
        EVENT_START_TIME.value to HelperFunctions.localDateTimeToDate(element.startTime),
        EVENT_END_TIME.value to HelperFunctions.localDateTimeToDate(element.endTime),
        EVENT_INVENTORY.value to element.inventory,
        EVENT_TAGS.value to element.tags.toList(),
        EVENT_MAX_SLOTS.value to element.getMaxNumberOfSlots(),
        EVENT_LIMITED.value to element.isLimitedEvent(),
        EVENT_PARTICIPANTS.value to element.getParticipants().toList()
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Event {
        return Event(
            eventId = id,
            eventName = document[EVENT_NAME.value] as String?,
            organizer = document[EVENT_ORGANIZER.value] as String?,
            zoneName = document[EVENT_ZONE_NAME.value] as String?,
            zoneId =  document[EVENT_ZONE_ID.value] as String?,
            description = document[EVENT_DESCRIPTION.value] as String?,
            startTime = HelperFunctions.dateToLocalDateTime(
                (document[EVENT_START_TIME.value] as Timestamp?)?.toDate()
            ),
            endTime = HelperFunctions.dateToLocalDateTime(
                // TODO: test if start time is null (remove ? from Timestamp)
                (document[EVENT_END_TIME.value] as Timestamp?)?.toDate()
            ),
            // TODO: Check how item is stored in Firestore, and check if conversion worked
            inventory = (document[EVENT_INVENTORY.value] as List<Item>).toMutableList(),
            tags = (document[EVENT_TAGS.value] as List<String>).toMutableSet(),
            limitedEvent = document[EVENT_LIMITED.value] as Boolean,
            maxNumberOfSlots = (document[EVENT_MAX_SLOTS.value] as Long?)?.toInt(),
            participants = (document[EVENT_PARTICIPANTS.value] as List<String>).toMutableSet()
        )
    }
}