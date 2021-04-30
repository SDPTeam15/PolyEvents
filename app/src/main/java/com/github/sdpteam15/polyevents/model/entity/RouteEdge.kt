package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper
import com.google.android.gms.maps.model.LatLng

/**
 * TODO
 */
data class RouteEdge(
    val id: String?,
    val start: RouteNode,
    val end: RouteNode
) : Attachable {
    var weight :Double? = null
        get() = field ?: RouteMapHelper.euclideanDistance(
            start.longitude,
            start.latitude,
            end.longitude,
            end.latitude)
        set (value) { field = value }


    override fun getAttachedNewPoint(position: LatLng, angle: Double?): Pair<LatLng, Double>? {
        TODO("Not yet implemented")
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
