package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.google.android.gms.maps.model.LatLng

/**
 *  TODO
 */
data class RouteNode(
    val id: String?,
    val latitude: Double,
    val longitude: Double,
    val areaId: String ? = null
) : Attachable {
    fun toLatLng(): LatLng {
        TODO()
    }

    companion object {
        fun fromLatLong(latLng: LatLng): RouteNode {
            TODO()
        }
    }

    override fun getAttachedNewPoint(position: LatLng, angle: Double?): Pair<LatLng, Double>? {
        TODO("Not yet implemented")
    }
}