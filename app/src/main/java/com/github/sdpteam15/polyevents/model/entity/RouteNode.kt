package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.LatLngOperator
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.isOnSegment
import com.github.sdpteam15.polyevents.model.map.THRESHOLD
import com.google.android.gms.maps.model.LatLng

/**
 *  TODO
 */
data class RouteNode(
    var id: String?,
    val latitude: Double,
    val longitude: Double,
    var areaId: String ? = null
) : Attachable {
    fun toLatLng(): LatLng {
        return LatLng(latitude,longitude)
    }

    companion object {
        fun fromLatLong(latLng: LatLng, areaId: String? = null): RouteNode =
            RouteNode(null,latLng.latitude,latLng.longitude,areaId)
    }

    override fun getAttachedNewPoint(position: LatLng, angle: Double?): Pair<RouteNode, Double>
        = Pair(this, euclideanDistance(position, toLatLng()))

    override fun splitOnIntersection(
        newEdges: MutableList<RouteEdge>,
        removeEdges: MutableList<RouteEdge>
    ) {
        for (e in newEdges.toList()) {
            if (e.start != this &&
                e.end !=  this &&
                isOnSegment(e.start!!.toLatLng(),e.end!!.toLatLng(), toLatLng())
            ) {
                newEdges.remove(e)
                newEdges.add(RouteEdge.fromRouteNode(e.start!!, this))
                newEdges.add(RouteEdge.fromRouteNode(e.end!!, this))
            }
        }
    }
}