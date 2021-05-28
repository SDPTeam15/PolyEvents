package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.model.database.remote.adapter.RouteNodeAdapter
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import org.junit.Test
import kotlin.test.assertEquals

class RouteNodeAdapterTest {
    @Test
    fun serializationWorks() {
        val id = "id"
        val latitude = 1.0
        val longitude = 1.0
        val areaId = "aid"

        val from = RouteNode(
            id = id,
            latitude = latitude,
            longitude = longitude,
            areaId = areaId,
        )
        val to = RouteNodeAdapter.fromDocument(RouteNodeAdapter.toDocument(from)!!, id)

        assertEquals(from, to)
    }
}