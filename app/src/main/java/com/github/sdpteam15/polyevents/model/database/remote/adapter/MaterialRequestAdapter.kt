package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.MaterialRequestConstant.*
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.google.firebase.Timestamp

/**
 * A class for converting between material request entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
@Suppress("UNCHECKED_CAST")
object MaterialRequestAdapter : AdapterInterface<MaterialRequest> {
    override fun toDocument(element: MaterialRequest): HashMap<String, Any?> {
        val map = hashMapOf(
            MATERIAL_REQUEST_LIST.value to element.items,
            MATERIAL_REQUEST_TIME.value to HelperFunctions.localDateTimeToDate(element.time),
            MATERIAL_REQUEST_USER_ID.value to element.userId,
            MATERIAL_REQUEST_EVENT_ID.value to element.eventId,
            MATERIAL_REQUEST_STATUS.value to element.status.ordinal,

            )
        if (element.adminMessage != null) map[MATERIAL_REQUEST_ADMIN_MESSAGE.value] =
            element.adminMessage
        if (element.staffInChargeId != null) map[MATERIAL_REQUEST_STAFF_IN_CHARGE.value] =
            element.staffInChargeId
        return map
    }

    override fun fromDocument(document: Map<String, Any?>, id: String): MaterialRequest {
        return MaterialRequest(
            id,
            (document[MATERIAL_REQUEST_LIST.value] as Map<String, Long>).mapValues { it.value.toInt() },
            HelperFunctions.dateToLocalDateTime((document[MATERIAL_REQUEST_TIME.value] as Timestamp?)?.toDate()),
            document[MATERIAL_REQUEST_USER_ID.value] as String,
            document[MATERIAL_REQUEST_EVENT_ID.value] as String,
            MaterialRequest.Status.fromOrdinal(
                ((document[MATERIAL_REQUEST_STATUS.value] ?: 0) as Long).toInt()
            )!!,
            document[MATERIAL_REQUEST_ADMIN_MESSAGE.value] as String?,
            document[MATERIAL_REQUEST_STAFF_IN_CHARGE.value] as String?
        )
    }
}