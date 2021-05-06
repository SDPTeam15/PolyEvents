package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.google.android.gms.maps.model.LatLng

/**
 * Object that can be attached by an edge when a vertex point is close to it
 */
interface Attachable {
    /**
     * Get the point on the attachable that must be projected
     * @param position the point initial position
     * @param angle used to determine if we should attach the point or not, depending on the angle
     * @return The remaped point on the attachable and the distance to the previous point if attached, else return null
     */
    fun getAttachedNewPoint(position: LatLng, angle: Double? = null): Pair<RouteNode, Double>

    /**
     * split the newEdges on an intersection
     * @param newEdges
     * @param removeEdges
     */
    fun splitOnIntersection(newEdges: MutableList<RouteEdge>, removeEdges: MutableList<RouteEdge>)
}