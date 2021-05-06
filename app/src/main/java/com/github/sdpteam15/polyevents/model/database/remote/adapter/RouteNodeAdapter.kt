package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.NODE_ID
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.LATITUDE
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.LONGITUDE
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.AREA_ID
import com.github.sdpteam15.polyevents.model.entity.RouteNode

object RouteNodeAdapter :AdapterInterface<RouteNode> {
    override fun toDocument(element: RouteNode): HashMap<String, Any?>  = hashMapOf(
        NODE_ID.value to element.id,
        LATITUDE.value to element.latitude,
        LONGITUDE.value to element.longitude,
        AREA_ID.value to element.areaId
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String) = RouteNode(
        id = id,
        latitude = document[LATITUDE.value] as Double,
        longitude = document[LONGITUDE.value] as Double,
        areaId = document[AREA_ID.value] as String?,
    )
}