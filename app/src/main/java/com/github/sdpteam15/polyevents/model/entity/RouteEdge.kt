package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.getIntersection
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.getNearestPoint
import com.github.sdpteam15.polyevents.model.map.THRESHOLD
import com.google.android.gms.maps.model.LatLng

/**
 * Undirected edge between two RouteNodes
 * The weight of the node is computed by the distance between both nodes
 * @param id RouteEdge id
 * @param startId RouteNode id
 * @param endId RouteNode id
 */
data class RouteEdge(
    var id: String?,
    private val startInitId: String? = null,
    private val endInitId: String? = null
) : Attachable {
    val startId : String?
        get() = start?.id ?: startInitId
    val endId : String?
        get() = end?.id ?: endInitId

    var start: RouteNode? = null
    var end: RouteNode? = null

    var weight :Double? = null
        get() = field ?: if(start != null && end != null){
        euclideanDistance(
            start!!.longitude,
            start!!.latitude,
            end!!.longitude,
            end!!.latitude)}
        else{
                0.0
        }

        set (value) { field = value }


    companion object {

        /**
         * Creates a RouteEdge from two RouteNodes instead of using their id
         * Directly sets the start and end nodes instead of setting them later
         * @param start first RouteNode
         * @param end second RouteNode
         * @param id optionally set the edge id
         * @return the resulting RouteEdge
         */
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

    override fun splitOnIntersection(
        newEdges: MutableList<RouteEdge>,
        removeEdges: MutableList<RouteEdge>
    ) {
        for (e in newEdges.toList()) {
            if (e.start != start &&
                e.end != end &&
                e.end != start &&
                e.start != end
            ) {
                val intersection = getIntersection(
                    e.start!!.toLatLng(),
                    e.end!!.toLatLng(),
                    start!!.toLatLng(),
                    end!!.toLatLng()
                )
                if (intersection != null) {
                    removeEdges.add(this)
                    when {
                        euclideanDistance(e.start!!.toLatLng(), intersection) < THRESHOLD -> {
                            newEdges.add(fromRouteNode(start!!, e.start!!))
                            newEdges.add(fromRouteNode(e.start!!, end!!))
                        }
                        euclideanDistance(e.end!!.toLatLng(), intersection) < THRESHOLD -> {
                            newEdges.add(fromRouteNode(start!!, e.end!!))
                            newEdges.add(fromRouteNode(e.end!!, end!!))
                        }
                        else -> {
                            newEdges.remove(e)
                            val mid = RouteNode.fromLatLong(intersection)
                            newEdges.add(fromRouteNode(start!!, mid))
                            newEdges.add(fromRouteNode(end!!, mid))
                            newEdges.add(fromRouteNode(e.start!!, mid))
                            newEdges.add(fromRouteNode(e.end!!, mid))
                        }
                    }
                }
            }
        }
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
