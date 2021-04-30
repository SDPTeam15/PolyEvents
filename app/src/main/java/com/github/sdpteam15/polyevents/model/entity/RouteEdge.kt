package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.google.android.gms.maps.model.LatLng

/**
 * TODO
 */
data class RouteEdge(
    val id: String?,
    val start: RouteNode,
    val end: RouteNode
)  : Attachable{
    val weight: Double
        get() {
            TODO()
        }

    override fun getAttachedNewPoint(position: LatLng, angle: Double?): Pair<LatLng, Double>? {
        TODO("Not yet implemented")
    }

}