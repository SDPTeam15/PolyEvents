package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.google.android.gms.maps.model.LatLng

/**
 * Object that can be attached by an edge when a vertex point is close to it
 */
interface Attachable {
    /**
     * get the point on the attachable that must be projected
     */
    fun getAttachedNewPoint(position: LatLng, angle: Double? = null): Pair<RouteNode, Double>

    /**
     * split the newEdges on an intersection
     * @param newEdges
     * @param removeEdges
     */
    fun splitOnIntersection(newEdges: MutableList<RouteEdge>, removeEdges: MutableList<RouteEdge>)
}