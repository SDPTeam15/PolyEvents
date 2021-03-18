package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

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
        fun toEventDocument(event: Event): HashMap<String, Any?> =
                hashMapOf(
                        "eventName" to event.eventName,
                        "organizer" to event.organizer,
                        "zoneName" to event.zoneName,
                        "description" to event.description,
                        "startTime" to event.startTime,
                        "endTime" to event.endTime,
                        "inventory" to event.inventory.mapKeys { it.key.toString() },
                        "tags" to event.tags.toList()
                )

        /**
         * Convert document data to an event entity in our model.
         * Data retrieved from Firestore documents are always of the form of a mutable mapping,
         * that maps strings - which are the names of the fields of our entity - to their values,
         * which can be of any type..
         * @param documentData this is the data we retrieve from the document.
         * @return the corresponding Event entity.
         */
        fun toEventEntity(documentData: MutableMap<String, Any?>): Event =
                Event(
                        eventName = documentData["eventName"] as String?,
                        organizer = documentData["organizer"] as String?,
                        zoneName = documentData["zoneName"] as String?,
                        description = documentData["description"] as String?,
                        startTime = (documentData["startTime"] as Timestamp?)?.toDate(),
                        endTime = (documentData["endTime"] as Timestamp?)?.toDate(),
                        inventory = (documentData["inventory"] as MutableMap<String, Int>)
                                .mapKeys { Item.valueOf(it.key) }.toMutableMap(),
                        tags = (documentData["tags"] as List<String>).toMutableSet()
                )
}