package com.github.sdpteam15.polyevents.util

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
        ZONE_NAME to element.zoneName,
        ZONE_LOCATION to element.location
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Zone = Zone(
        zoneName = document[ZONE_NAME] as String?,
        location = document[ZONE_LOCATION] as String?
    )
}