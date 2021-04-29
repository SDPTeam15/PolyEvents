package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.MaterialRequestConstant.*
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.google.firebase.Timestamp
import kotlin.collections.hashMapOf

object MaterialRequestAdapter : AdapterInterface<MaterialRequest> {
    override fun toDocument(element: MaterialRequest) = hashMapOf(
        MATERIAL_REQUEST_LIST.value to element.items,
        MATERIAL_REQUEST_TIME.value to HelperFunctions.localDateTimeToDate(element.time),
        MATERIAL_REQUEST_USER_ID.value to element.userId
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): MaterialRequest {
        return MaterialRequest(
            id ,
            document[MATERIAL_REQUEST_LIST.value] as Map<String, Int>,
            HelperFunctions.dateToLocalDateTime((document[MATERIAL_REQUEST_TIME.value]  as Timestamp?)?.toDate()),
            document[MATERIAL_REQUEST_USER_ID.value] as String
        )
    }
}