package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.angle
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.divide
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.minus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.norm
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.plus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.scalar
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.time
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sqrt
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.pow

const val THRESHOLD = 0.001
const val MAGNET_DISTANCE_THRESHOLD = 0.001

object RouteMapHelper {
    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()
    val zones = ObservableList<Zone>()

    var map: MapsInterface? = null
    val toDeleteLines: MutableList<Polyline> = ArrayList()
    val lineToEdge: MutableMap<RouteEdge, Polyline> = mutableMapOf()
    val idToEdge: MutableMap<RouteEdge, Polyline> = mutableMapOf()

    var deleteMode = false
    var tempUid = 0

    /**
     * Add a line to dataBase
     * TODO
     */
    fun addLine(start: LatLng, end: LatLng) {
        val s = RouteNode.fromLatLong(start, null)
        val e = RouteNode.fromLatLong(end, null)
        nodes.add(s)
        nodes.add(e)
        val l = RouteEdge("Edge ${tempUid++}", s.id, e.id)
        l.start = s
        l.end = e
        edges.add(l)
    }

    /**
     * Add a line from dataBase
     * TODO
     */
    fun removeLine(edge: RouteEdge) {
        TODO()
    }

    /**
     * Returns the shortest path from a point on the map to the given Zone
     * @param startPosition the person starting position
     * @param targetZoneId the Zone where the person wants to go to
     * @return The list of points that the person needs to follow, null if there is no path nearby
     */
    fun getShortestPath(startPosition: LatLng, targetZoneId: String): List<LatLng>? {

        val nearestPos = getPosOnNearestAttachable(startPosition)
        // if nothing to attach to, no path can be found
        if (nearestPos.second == null) {
            return null
        }
        val nodesForShortestPath = mutableSetOf<RouteNode>()
        val edgesForShortestPath = mutableSetOf<RouteEdge>()
        val startingNode = RouteNode.fromLatLong(startPosition)
        val targetZoneCenter =
            RouteNode.fromLatLong(zones.first { it.zoneId == targetZoneId }.getZoneCenter())

        /**
         * Setup everything for the
         */
        fun setUpGraph() {

            nodesForShortestPath.addAll(nodes)
            edgesForShortestPath.addAll(edges)

            // Add current position as starting node

            nodesForShortestPath.add(startingNode)
            when (nearestPos.second) {
                is RouteNode -> {
                    edgesForShortestPath.add(
                        RouteEdge(
                            null,
                            startingNode,
                            RouteNode.fromLatLong(nearestPos.first.toLatLng())
                        )
                    )
                }
                is RouteEdge -> {
                    edgesForShortestPath.remove(nearestPos.second)
                    val splitNode = RouteNode.fromLatLong(nearestPos.first.toLatLng())
                    nodesForShortestPath.add(splitNode)
                    edgesForShortestPath.add(
                        RouteEdge(
                            null,
                            (nearestPos.second as RouteEdge).start,
                            splitNode
                        )
                    )
                    edgesForShortestPath.add(
                        RouteEdge(
                            null,
                            (nearestPos.second as RouteEdge).end,
                            splitNode
                        )
                    )
                    edgesForShortestPath.add(RouteEdge(null, startingNode, splitNode))
                }
                is Zone -> {
                    val areaNode =
                        RouteNode.fromLatLong(
                            nearestPos.first.toLatLng(),
                            (nearestPos.second as Zone).zoneId
                        )
                    nodesForShortestPath.add(areaNode)
                    edgesForShortestPath.add(RouteEdge(null, startingNode, areaNode))
                }
            }

            //consider zones as fully connected graphs
            val areasnodes = nodesForShortestPath.groupBy { it.areaId }
            for (nodesByArea in areasnodes.filter { it.key != targetZoneId && it.key != null }.values) {
                for (node in nodesByArea) {
                    for (node2 in nodesByArea) {
                        val edge = RouteEdge(null, node, node2)
                        if (!edgesForShortestPath.contains(edge) && node != node2) {
                            edgesForShortestPath.add(edge)
                        }
                    }
                }
            }
            //we consider target Zone center as the target node

            nodesForShortestPath.add(targetZoneCenter)
            for (node in areasnodes[targetZoneId]!!) {
                val edge = RouteEdge(null, node, targetZoneCenter)
                edge.weight = 0.0
                edgesForShortestPath.add(edge)
            }

        }

        fun dijkstra(
            nodes: Set<RouteNode>,
            edges: Set<RouteEdge>,
            start: RouteNode
        ): Map<RouteNode, RouteNode?> {
            if (start !in nodes) throw IllegalArgumentException("Start route not in set of nodes for Dijkstra")
            val done: MutableSet<RouteNode> = mutableSetOf()

            val adjList = toAdjacencyList(edges)

            // costs to get to each node
            val costs = nodes.map { it to Double.MAX_VALUE }.toMap().toMutableMap()
            costs[start] = 0.0

            val previous: MutableMap<RouteNode, RouteNode?> =
                nodes.map { it to null }.toMap().toMutableMap()

            while (done != nodes) {
                // get node with lowest cost
                val v: RouteNode = costs
                    .filter { !done.contains(it.key) }
                    .minByOrNull { it.value }!!
                    .key

                for (neighbor in adjList[v]!!.filter { !done.contains(it.first) }) {
                    val newPath = costs[v]!! + neighbor.second
                    if (newPath < costs[neighbor.first]!!) {
                        costs[neighbor.first] = newPath
                        previous[neighbor.first] = v
                    }
                }
                done.add(v)
            }

            return previous
        }

        setUpGraph()
        val shortestPaths = dijkstra(nodesForShortestPath, edgesForShortestPath, startingNode)

        // convert the path to the tree to the target node into a list of point
        val pointlist: MutableList<LatLng> = mutableListOf()
        var lastnode = targetZoneCenter
        while (lastnode != startingNode) {
            //If previous node doesn't exist, that means that there is no path to the node, then return true
            lastnode = shortestPaths[lastnode] ?: return null
            pointlist.add(lastnode.toLatLng())
        }
        return pointlist.reversed()
    }

    fun toAdjacencyList(edges: Set<RouteEdge>): Map<RouteNode, Set<Pair<RouteNode, Double>>> {
        val adjList: MutableMap<RouteNode, MutableSet<Pair<RouteNode, Double>>> = mutableMapOf()
        for (edge in edges) {
            if (!adjList.containsKey(edge.start)) {
                adjList[edge.start] = mutableSetOf()
            }
            adjList[edge.start]!!.add(Pair(edge.end, edge.weight!!))
            if (!adjList.containsKey(edge.end)) {
                adjList[edge.end] = mutableSetOf()
            }
            adjList[edge.end]!!.add(Pair(edge.start, edge.weight!!))
        }
        return adjList
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
        val angle = angle(start, end)
        val firstStart = getPosOnNearestAttachable(start, angle)
        val firstEnd = getPosOnNearestAttachable(start, angle)

        if (firstStart.third != null && firstEnd.third != null) {
            if (firstStart.third!! < MAGNET_DISTANCE_THRESHOLD) {
                if (firstEnd.third!! < MAGNET_DISTANCE_THRESHOLD) {
                    if (firstStart.second == firstEnd.second) {
                        val secondStart = getPosOnNearestAttachable(start, angle, firstEnd.second)
                        val secondEnd = getPosOnNearestAttachable(start, angle, firstStart.second)
                        if (secondStart.third != null && secondStart.third!! < MAGNET_DISTANCE_THRESHOLD) {
                            if (secondEnd.third != null && secondEnd.third!! < MAGNET_DISTANCE_THRESHOLD &&
                                secondStart.second != secondEnd.second
                            ) {
                                return if (secondStart.third!! + firstEnd.third!! < firstStart.third!! + secondEnd.third!!)
                                    Pair(secondStart.first.toLatLng(), firstEnd.first.toLatLng())
                                else
                                    Pair(firstStart.first.toLatLng(), secondEnd.first.toLatLng())
                            }
                            return Pair(firstStart.first.toLatLng(), secondEnd.first.toLatLng())
                        } else if (secondEnd.third != null && secondEnd.third!! < MAGNET_DISTANCE_THRESHOLD)
                            return Pair(firstStart.first.toLatLng(), secondEnd.first.toLatLng())
                        return if (firstEnd.third!! < firstStart.third!!) Pair(
                            start,
                            firstEnd.first.toLatLng()
                        )
                        else Pair(firstStart.first.toLatLng(), end)
                    }
                    return Pair(firstStart.first.toLatLng(), firstEnd.first.toLatLng())
                }
                return Pair(firstStart.first.toLatLng(), end)
            } else if (firstEnd.third!! < MAGNET_DISTANCE_THRESHOLD)
                return Pair(start, firstEnd.first.toLatLng())
        }
        return Pair(start, end)
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

    /**
     * TODO
     */
    fun getPosOnNearestAttachable(
        point: LatLng,
        angle: Double? = null,
        exclude: Attachable? = null
    ): Triple<RouteNode, Attachable?, Double?> {
        var res: Triple<RouteNode, Attachable?, Double?> =
            Triple(RouteNode.fromLatLong(point), null, null)
        val found: (Attachable) -> Unit = {
            if (it != exclude) {
                val pair = it.getAttachedNewPoint(point, angle)
                if (res.second == null || pair.second < res.third!!)
                    res = Triple(pair.first, it, pair.second)
            }
        }
        for (e in nodes) found(e)
        for (e in edges) found(e)
        for (e in zone) found(e)
        return res
    }

    /**
     * TODO
     */
    fun getNodesAndEdgesFromDB(lifecycleOwner: LifecycleOwner): Observable<Boolean> {
        edges.observeAdd(lifecycleOwner) {
            edgeAddedNotification(it.value)
        }
        edges.observeRemove(lifecycleOwner) {
            edgeRemovedNotification(it.value)
        }
        return Observable(true)
    }

    private fun addEdgeToMaps() {

    }

    fun edgeRemovedNotification(edge: RouteEdge) {
        lineToEdge[edge]!!.remove()
        lineToEdge.remove(edge)
    }

    fun edgeAddedNotification(edge: RouteEdge) {
        removeAllLinesToRemove()
        val option = PolylineOptions()
        option.add(edge.start!!.toLatLng())
        option.add(edge.end!!.toLatLng())
        val route = map!!.addPolyline(option)
        route.tag = edge.id
        lineToEdge[edge] = route
    }

    fun removeAllLinesToRemove() {
        for (line in toDeleteLines) {
            line.remove()
        }
        toDeleteLines.clear()
    }

    fun createNewRoute(context: Context?) {
        deleteMode = false
        if (tempPolyline != null) {
            tempPolyline!!.remove()
            tempVariableClear()
        }
        setupEditLine(context, map!!.cameraPosition!!.target)
    }

    fun saveNewRoute() {
        deleteMode = false
        toDeleteLines.add(tempPolyline!!)
        //tempPolyline!!.isClickable = true
        tempPolyline!!.color = Color.GREEN
        addLine(tempLatLng[0]!!, tempLatLng[1]!!)
        tempVariableClear()
    }

    fun removeRoute(context: Context?) {
        if (tempPolyline != null) {
            tempPolyline!!.remove()
            tempVariableClear()
        } else {
            deleteMode = !deleteMode
            HelperFunctions.showToast("Deletion mode is $deleteMode", context)
        }
    }

    fun tempVariableClear() {
        startMarker!!.remove()
        endMarker!!.remove()

        startMarker = null
        endMarker = null
        tempPolyline = null
        tempLatLng.clear()
    }

    var startMarker: Marker? = null
    var endMarker: Marker? = null

    var tempPolyline: Polyline? = null
    var tempLatLng: MutableList<LatLng?> = ArrayList()

    /**
     * Sets up the edition of the line
     * @param context context
     * @param pos position to create the new line
     */
    fun setupEditLine(context: Context?, pos: LatLng) {
        val zoom = map!!.cameraPosition!!.zoom
        val divisor = 2.0.pow(zoom.toDouble())
        val longDiff = 188.0 / divisor / 2
        val pos1 = LatLng(pos.latitude, pos.longitude - longDiff)
        val pos2 = LatLng(pos.latitude, pos.longitude + longDiff)
        val option = PolylineOptions().add(pos1).add(pos2).color(Color.RED)
        tempPolyline = map!!.addPolyline(option)
        tempLatLng.clear()
        tempLatLng.add(pos1)
        tempLatLng.add(pos2)

        setupModifyMarkers(context)
    }

    /**
     * Sets up the markers for the modification of the polyline
     * @param context context
     */
    fun setupModifyMarkers(context: Context?) {
        val points = tempPolyline!!.points
        val startPos = points[0]
        val endPos = points[1]

        val anchor = IconAnchor(0.5f, 0.5f)
        val bound = IconBound(0, 0, 100, 100)
        val dimension = IconDimension(100, 100)

        startMarker = map!!.addMarker(
            GoogleMapHelper.newMarker(
                context,
                startPos,
                anchor,
                PolygonAction.MARKER_START.toString(),
                null,
                true,
                R.drawable.ic_locate_me,
                bound,
                dimension
            )
        )

        endMarker = map!!.addMarker(
            GoogleMapHelper.newMarker(
                context,
                endPos,
                anchor,
                PolygonAction.MARKER_END.toString(),
                null,
                true,
                R.drawable.ic_locate_me,
                bound,
                dimension
            )
        )
    }

    /**
     * Moves the marker and redraws the polyline
     * @param marker marker that moved
     */
    fun moveMarker(marker: Marker) {
        when (marker.snippet) {
            PolygonAction.MARKER_START.toString() -> {

            }

            PolygonAction.MARKER_END.toString() -> {

            }
        }
        tempPolyline!!.points = listOf(startMarker!!.position, endMarker!!.position)
    }

    fun polylineClick(polyline: Polyline) {
        if (deleteMode) {
            Log.d("DELETE", "POLYLINE TO DELETE")
        }
    }
}