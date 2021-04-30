package com.github.sdpteam15.polyevents.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RouteMapHelperTest {

    @Before
    fun setup() {
        val node1 = RouteNode("1", 2.0, 5.0)
        val node2 = RouteNode("2", 2.0, 1.5, "1")
        val node3 = RouteNode("3", 1.5, 2.0, "1")
        val node4 = RouteNode("4", 6.0, 1.0)
        val node5 = RouteNode("5", 5.0, 5.5, "2")
        val node6 = RouteNode("6", 5.5, 5.0, "2")
        val edge1 = RouteEdge.fromRouteNode(node1, node3,"1")
        val edge2 = RouteEdge.fromRouteNode(node1, node5,"2")
        val edge3 = RouteEdge.fromRouteNode(node1, node4,"3")
        val edge4 = RouteEdge.fromRouteNode(node4, node6,"4")
        val edge5 = RouteEdge.fromRouteNode(node4, node2,"5")
        RouteMapHelper.zones.addAll(
            mutableListOf(
                Zone(
                    "1",
                    "zone1",
                    "1.0|1.0!2.0|1.0!2.0|2.0!1.0|2.0",
                    "coolZone"
                ), Zone(
                    "2",
                    "zone2",
                    "5.0|5.0!5.0|6.0!6.0|6.0!6.0|5.0",
                    "coolZone"
                )
            )
        )


        RouteMapHelper.nodes.addAll(
            mutableListOf(
                node1, node2, node3, node4, node5, node6
            )
        )
        RouteMapHelper.edges.addAll(
            mutableListOf(
                edge1, edge2, edge3, edge4, edge5
            )
        )
    }

    @Test
    fun getShortestPathReturnsCorrectPath() {
        val result = RouteMapHelper.getShortestPath(LatLng(5.5, 7.0), "1")!!
        val expected = listOf(
            LatLng(5.5, 7.0),
            LatLng(5.5, 6.0),
            LatLng(5.0, 5.5),
            LatLng(2.0, 5.0),
            LatLng(1.5, 2.0)
        )
        assertEquals(expected,result)
    }
}