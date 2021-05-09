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
    override fun toDocument(element: MaterialRequest) = hashMapOf(
        MATERIAL_REQUEST_LIST.value to element.items,
        MATERIAL_REQUEST_TIME.value to HelperFunctions.localDateTimeToDate(element.time),
        MATERIAL_REQUEST_USER_ID.value to element.userId
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): MaterialRequest {
        return MaterialRequest(
            id,
            document[MATERIAL_REQUEST_LIST.value] as Map<String, Int>,
            HelperFunctions.dateToLocalDateTime((document[MATERIAL_REQUEST_TIME.value] as Timestamp?)?.toDate()),
            document[MATERIAL_REQUEST_USER_ID.value] as String
        )
    }
}