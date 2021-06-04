package com.github.sdpteam15.polyevents.model.database.remote.adapter

import androidx.navigation.ActivityNavigatorExtras
import com.github.sdpteam15.polyevents.helper.HelperFunctions.toInt
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.entity.Zone

/**
 * A class for converting between zone entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of UserEntity.
 */
@Suppress("UNCHECKED_CAST")
object ZoneAdapter : AdapterInterface<Zone> {
    override fun toDocumentWithoutNull(element: Zone): HashMap<String, Any?> {
        val map = hashMapOf(
            ZONE_NAME.value to element.zoneName,
            ZONE_LOCATION.value to element.location,
            ZONE_DESCRIPTION.value to element.description,
            //Firestore doesn't store integers
            ZONE_STATUS.value to element.status.ordinal.toLong()
        ) as HashMap<String, Any?>
        if (element.zoneId != null) {
            map[ZONE_DOCUMENT_ID.value] = element.zoneId!!
        }
        return map
    }

    override fun fromDocument(document: Map<String, Any?>, id: String): Zone = Zone(
        zoneId = id,
        zoneName = document[ZONE_NAME.value] as String?,
        location = document[ZONE_LOCATION.value] as String?,
        description = document[ZONE_DESCRIPTION.value] as String?,
        status = Zone.Status.fromOrdinal(document[ZONE_STATUS.value].toInt()!!)
    )
}