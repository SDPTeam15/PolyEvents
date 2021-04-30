package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.google.android.gms.maps.model.LatLng

/**
 * Object that can be attached by an edge when a vertex point is close to it
 */
interface Attachable {
    /**
     * get the point on the attachable that must be projected
     */
    fun getAttachedNewPoint(position: LatLng, angle: Double ?): Pair<RouteNode, Double>

}