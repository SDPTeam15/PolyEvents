package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.entity.RouteNode

object RouteNodeAdapter :AdapterInterface<RouteNode> {
    override fun toDocument(element: RouteNode): HashMap<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): RouteNode {
        TODO("Not yet implemented")
    }
}