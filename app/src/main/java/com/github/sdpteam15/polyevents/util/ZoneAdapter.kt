package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.model.Zone
import com.google.firebase.firestore.DocumentSnapshot

/**
 * A class for converting between zone entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of UserEntity.
 */
class ZoneAdapter {
    companion object {
        /**
         * Convert a zone entity to an intermediate mapping
         * of fields to their values, that we can pass to the document directly.
         * Firestore document keys are always strings.
         * @param zone the entity we're converting
         * @return a hashmap of the entity fields to their values
         */
        fun toZoneDocument(zone: Zone) : HashMap<String, Any?> =
                hashMapOf(
                    "zoneName" to zone.zoneName,
                    "location" to zone.location
                )

        /**
         * Convert document data to a zone entity in our model.
         * Data retrieved from Firestore documents are always of the form of a mutable mapping,
         * that maps strings - which are the names of the fields of our entity - to their values,
         * which can be of any type..
         * @param documentData this is the data we retrieve from the document.
         * @return the corresponding userEntity.
         */
        fun toZoneEntity(documentData: MutableMap<String, Any?>): Zone =
                Zone(
                        zoneName = documentData["zoneName"] as String?,
                        location = documentData["location"] as String?
                )
    }
}