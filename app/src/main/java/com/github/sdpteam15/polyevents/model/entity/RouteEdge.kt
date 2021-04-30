package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.getNearestPoint
import com.google.android.gms.maps.model.LatLng
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper

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

    var weight :Double? = null
        get() = field ?: if(start != null && end != null){
        RouteMapHelper.euclideanDistance(
            start.longitude,
            start.latitude,
            end.longitude,
            end.latitude)}
        else{
                0.0
        }

        set (value) { field = value }


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
    override fun equals(other: Any?): Boolean {
        return (other != null && other is RouteEdge && ((start == other.start && end == other.end) || (start == other.end && end == other.start)))
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }

}
