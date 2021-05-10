package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.isOnSegment
import com.google.android.gms.maps.model.LatLng

/**
 *  Node from the undirected graph representing the route between areas on the map
 *  @param id Node id
 *  @param latitude latitude of the node
 *  @param longitude longitude of the node
 *  @param areaId sets the area to which this node is attached if there is one
 */
data class RouteNode(
    var id: String?,
    val latitude: Double,
    val longitude: Double,
    var areaId: String ? = null
) : Attachable {
    /**
     * Returns the latitude and longitude of the RouteNode
     */
    fun toLatLng(): LatLng {
        return LatLng(latitude,longitude)
    }

    companion object {
        /**
         * Creates a RouteNode from a latitude / longitude and eventually an area id
         * @param latLng the latitude / longitude of the node to create
         * @param areaId sets the area to which this node is attached if there is one
         * @return the resulting RouteNode
         */
        fun fromLatLong(latLng: LatLng, areaId: String? = null): RouteNode =
            RouteNode(null,latLng.latitude,latLng.longitude,areaId)
    }

    override fun getAttachedNewPoint(position: LatLng, angle: Double?): Pair<RouteNode, Double>
        = Pair(this, euclideanDistance(position, toLatLng()))

    override fun splitOnIntersection(
        newEdges: MutableList<RouteEdge>,
        removeEdges: MutableList<RouteEdge>
    ) { }
}