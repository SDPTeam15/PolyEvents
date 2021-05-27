package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.model.database.remote.adapter.RouteEdgeAdapter
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import org.junit.Test
import kotlin.test.assertEquals

class RouteEdgeAdapterTest {
    @Test
    fun serializationWorks() {
        val id = "id"
        val startInitId = "sid"
        val endInitId = "eid"

        val from = RouteEdge(
            id = id,
            startInitId = startInitId,
            endInitId = endInitId,
        )
        val to = RouteEdgeAdapter.fromDocument(RouteEdgeAdapter.toDocument(from)!!, id)

        assertEquals(from, to)
    }
}