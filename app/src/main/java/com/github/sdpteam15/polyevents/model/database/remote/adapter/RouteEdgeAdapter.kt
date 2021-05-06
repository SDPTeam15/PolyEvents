package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.entity.RouteEdge

object RouteEdgeAdapter : AdapterInterface<RouteEdge> {
    override fun toDocument(element: RouteEdge): HashMap<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): RouteEdge {
        TODO("Not yet implemented")
    }
}