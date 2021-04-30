package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.euclideanDistance
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
        TODO()
    }

    companion object {
        fun fromLatLong(latLng: LatLng, areaId: String ? = null): RouteNode {
            TODO()
        }
    }

    override fun getAttachedNewPoint(position: LatLng, angle: Double?): Pair<RouteNode, Double>
        = Pair(this, euclideanDistance(position, toLatLng()))
}