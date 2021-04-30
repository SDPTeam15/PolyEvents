package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.getNearestPoint
import com.google.android.gms.maps.model.LatLng

/**
 * TODO
 */
data class RouteEdge(
    val id: String?,
    val startId: String?,
    val endId: String?
) : Attachable {
    var start: RouteNode? = null
    var end: RouteNode? = null

    val weight: Double
        get() {
            TODO()
        }

    companion object {
        fun fromRouteNode(start: RouteNode, end: RouteNode, id: String? = null): RouteEdge {
            val res = RouteEdge(id, start.id, start.id)
            res.start = start
            res.end = end
            return res
        }
    }

    override fun getAttachedNewPoint(position: LatLng, angle: Double?): Pair<RouteNode, Double> {
        val newPoint = getNearestPoint(start!!, end!!, position)
        return Pair(newPoint, euclideanDistance(position, newPoint.toLatLng()))
    }
}