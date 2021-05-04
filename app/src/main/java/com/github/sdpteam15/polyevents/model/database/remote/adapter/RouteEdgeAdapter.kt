package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.EDGE_ID
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.START_ID
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.END_ID
import com.github.sdpteam15.polyevents.model.entity.RouteEdge

object RouteEdgeAdapter : AdapterInterface<RouteEdge> {
    override fun toDocument(element: RouteEdge): HashMap<String, Any?>  = hashMapOf(
        EDGE_ID.value to element.id,
        START_ID.value to element.startId,
        END_ID.value to element.endId
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String) = RouteEdge(
        id = id,
        startInitId = document[START_ID.value] as String?,
        endInitId = document[END_ID.value] as String?
    )
}