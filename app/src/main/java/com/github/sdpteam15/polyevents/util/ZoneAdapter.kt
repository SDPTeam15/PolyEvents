package com.github.sdpteam15.polyevents.util

import android.icu.text.DateTimePatternGenerator.ZONE
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_DESCRIPTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_DOCUMENT_ID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_LOCATION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_NAME
import com.github.sdpteam15.polyevents.model.Zone

/**
 * A class for converting between zone entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of UserEntity.
 */
object ZoneAdapter {
    /**
     * Convert a zone entity to an intermediate mapping
     * of fields to their values, that we can pass to the document directly.
     * Firestore document keys are always strings.
     * @param zone the entity we're converting
     * @return a hashmap of the entity fields to their values
     */
    fun toZoneDocument(zone: Zone) : HashMap<String, Any?>{
        val hash:HashMap<String,Any?> = hashMapOf(
            ZONE_NAME to zone.zoneName,
            ZONE_LOCATION to zone.location,
            ZONE_DESCRIPTION to zone.description
        )
        if(zone.zoneId!=null)
            hash[ZONE_DOCUMENT_ID] = zone.zoneId
        return hash
    }


    /**
     * Convert document data to a zone entity in our model.
     * Data retrieved from Firestore documents are always of the form of a mutable mapping,
     * that maps strings - which are the names of the fields of our entity - to their values,
     * which can be of any type..
     * @param documentData this is the data we retrieve from the document.
     * @return the corresponding userEntity.
     */
    fun toZoneEntity(documentData: MutableMap<String, Any?>, id:String): Zone =
        Zone(
            zoneId = id as String?,
            zoneName = documentData[ZONE_NAME] as String?,
            location = documentData[ZONE_LOCATION] as String?,
            description = documentData[ZONE_DESCRIPTION] as String?
        )
}