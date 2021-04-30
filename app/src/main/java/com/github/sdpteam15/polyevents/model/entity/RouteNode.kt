package com.github.sdpteam15.polyevents.model.entity

import com.google.type.LatLng

/**
 *  TODO
 */
data class RouteNode(
    val id: String?,
    val latitude: Double,
    val longitude: Double,
    val areaId: String ? = null
) {
    fun toLatLng(): LatLng {
        TODO()
    }

    companion object {
        fun fromLatLong(latLng: LatLng): RouteNode {
            TODO()
        }
    }
}