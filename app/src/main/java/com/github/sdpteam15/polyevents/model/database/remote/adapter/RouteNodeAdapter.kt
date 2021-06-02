package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.*
import com.github.sdpteam15.polyevents.model.entity.RouteNode

/**
 * A class for converting between route node entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
object RouteNodeAdapter : AdapterInterface<RouteNode> {
    override fun toDocument(element: RouteNode): HashMap<String, Any?> =
        hashMapOf(
            NODE_ID.value to element.id,
            LATITUDE.value to element.latitude,
            LONGITUDE.value to element.longitude,
            AREA_ID.value to element.areaId
        )

    override fun fromDocument(document: Map<String, Any?>, id: String) = RouteNode(
        id = id,
        latitude = document[LATITUDE.value] as Double,
        longitude = document[LONGITUDE.value] as Double,
        areaId = document[AREA_ID.value] as String?,
    )
}