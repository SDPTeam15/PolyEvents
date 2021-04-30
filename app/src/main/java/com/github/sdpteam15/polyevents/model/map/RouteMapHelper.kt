package com.github.sdpteam15.polyevents.model.map

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.divide
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.minus
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.norm
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.plus
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.scalar
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.time
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng

const val THRESHOLD = 0.001

object RouteMapHelper {
    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()
    val zone = ObservableList<Zone>()

    /**
     * Add a line to dataBase
     */
    fun addLine(start: LatLng, end: LatLng) {
        TODO()
    }

    /**
     * TODO
     */
    fun removeLine(edge: RouteEdge) {
        TODO()
    }

    /**
     * Returns the shortest path from a point on the map to the given Zone
     * @param startPosition the person starting position
     * @param targetZoneId the Zone where the person wants to go to
     * @return The list of points that the person needs to follow
     */
    fun getShortestPath(startPosition: LatLng, targetZoneId: String): List<LatLng>? {
        TODO()
    }

    /**
     * TODO
     */
    fun drawRoute() {
        TODO()
    }

    /**
     * TODO
     */
    fun getEdgeOnNearestAttachable(start: LatLng, end: LatLng): Pair<LatLng, LatLng> {
        // TODO
        return Pair(start, end)
    }

    /**
     * TODO
     */
    fun getPosOnNearestAttachable(
        point: LatLng,
        angle: Double? = null,
        exclude: Attachable? = null
    ): Pair<RouteNode, Attachable?> {
        var res: Pair<Pair<RouteNode, Attachable?>, Double?> =
            Pair(Pair(RouteNode.fromLatLong(point), null), null)
        val found: (Attachable) -> Unit = {
            if (it != exclude) {
                val pair = it.getAttachedNewPoint(point, angle)
                if (res.second == null || pair.second < res.second!!)
                    res = Pair(Pair(pair.first, it), pair.second)
            }
        }
        for (e in nodes) found(e)
        for (e in edges) found(e)
        for (e in zone) found(e)
        return res.first
    }

    /**
     * TODO
     */
    fun getNodesAndEdgesFromDB(lifecycle: LifecycleOwner): Observable<Boolean> {
        TODO()
    }

    /**
     * TODO
     */
    fun getNearestPoint(start: RouteNode, end: RouteNode, point: LatLng): RouteNode {
        var line = minus(end.toLatLng(), start.toLatLng())
        val lineNorm = norm(line)
        line = divide(line, lineNorm)
        val p = minus(point, start.toLatLng())
        val dif = scalar(line, p)
        if (dif <= THRESHOLD)
            return start
        if (THRESHOLD <= lineNorm - dif)
            return end
        return RouteNode.fromLatLong(plus(start.toLatLng(), time(line, dif)))
    }
}