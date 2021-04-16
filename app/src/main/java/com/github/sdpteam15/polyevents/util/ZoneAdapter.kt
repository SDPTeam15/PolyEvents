package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_BIRTH_DATE
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
object ZoneAdapter : AdapterInterface<Zone> {
    override fun toDocument(element: Zone): HashMap<String, Any?> = hashMapOf(
        ZONE_DOCUMENT_ID to element.zoneId,
        ZONE_NAME to element.zoneName,
        ZONE_LOCATION to element.location,
        DatabaseConstant.ZONE_DESCRIPTION to element.description
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Zone = Zone(
        zoneId = id,
        zoneName = document[ZONE_NAME] as String?,
        location = document[ZONE_LOCATION] as String?,
        description = document[DatabaseConstant.ZONE_DESCRIPTION] as String?
    )
}