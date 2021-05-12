package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.divide
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.minus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.norm
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.plus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.scalar
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.time
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.fragments.MapsFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.pow

const val THRESHOLD = 0.00002
const val MAGNET_DISTANCE_THRESHOLD = 0.00005

private val ROUTE_COLOR = Color.rgb(0, 162, 232)
private val DEFAULT_ROAD_COLOR = Color.argb(50, 0, 0, 0)

object RouteMapHelper {

    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()
    val zones = ObservableList<Zone>()

    var map: MapsInterface? = null
    val toDeleteLines: MutableList<Polyline> = ArrayList()
    val lineToEdge: MutableMap<RouteEdge, Polyline> = mutableMapOf()
    val idToEdge: MutableMap<String, RouteEdge> = mutableMapOf()

    var attachables: Pair<Attachable?, Attachable?> = Pair(null, null)

    var deleteMode = false

    //var routing = false
    var currentTarget: LatLng? = null
    var chemin: MutableList<LatLng> = mutableListOf()
    var route: MutableList<Polyline> = mutableListOf()

    /**
     * Add a line to dataBase
     * @param start pair containing the Position of the first point and eventually an attached object
     * @param end pair containing the Position of the second point and eventually an attached object
     */
    fun addLine(
        start: Pair<LatLng, Attachable?>,
        end: Pair<LatLng, Attachable?>
    ) : Observable<Boolean> {
        val newEdges = mutableListOf<RouteEdge>()
        val removeEdges = mutableListOf<RouteEdge>()

        val from = if (start.second != null)
            start.second!!.getAttachedNewPoint(start.first).first
        else
            RouteNode.fromLatLong(start.first)
        val to = if (end.second != null)
            end.second!!.getAttachedNewPoint(end.first).first
        else
            RouteNode.fromLatLong(end.first)

        val edge = RouteEdge(null)
        edge.start = from
        edge.end = to
        newEdges.add(edge)

        for (e in edges) e.splitOnIntersection(newEdges, removeEdges)
        for (e in zones) e.splitOnIntersection(newEdges, removeEdges)

        return Database.currentDatabase.routeDatabase!!.updateEdges(newEdges, removeEdges, edges, nodes)
    }

    /**
     * Removes a line from the dataBase
     * @param edge the line to add to the database
     */
    fun removeLine(edge: RouteEdge) =
        Database.currentDatabase.routeDatabase!!.removeEdge(edge, edges, nodes)

    /**
     * Returns the shortest path from a point on the map to the given Zone
     * @param startPosition the person starting position
     * @param targetZoneId the Zone where the person wants to go to
     * @return The list of points that the person needs to follow, null if there is no path nearby
     */
    fun getShortestPath(startPosition: LatLng, targetZoneId: String): List<LatLng>? {

        // gets the closest point on the map where we can go from our current position
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
         * Setup the graph for the execution of the shortest path
         */
        fun setUpGraph() {

            nodesForShortestPath.addAll(nodes)
            edgesForShortestPath.addAll(edges)

            // Add current position as starting node and link to a node on the nearest attachable

            nodesForShortestPath.add(startingNode)

            when (nearestPos.second) {
                is RouteNode -> {
                    // If the nearest attachable is a node, just draw an edge between it and the starting node
                    edgesForShortestPath.add(
                        RouteEdge.fromRouteNode(
                            startingNode,
                            nearestPos.first
                        )
                    )
                }
                is RouteEdge -> {
                    // If the nearest attachable is an edge, we split it in two parts at the point closest to our starting node
                    // and we link our starting node to the splitting point
                    edgesForShortestPath.remove(nearestPos.second)
                    val splitNode = RouteNode.fromLatLong(nearestPos.first.toLatLng())
                    nodesForShortestPath.add(splitNode)
                    edgesForShortestPath.add(
                        RouteEdge.fromRouteNode(
                            (nearestPos.second as RouteEdge).start!!,
                            splitNode
                        )
                    )
                    edgesForShortestPath.add(
                        RouteEdge.fromRouteNode(
                            (nearestPos.second as RouteEdge).end!!,
                            splitNode
                        )
                    )
                    edgesForShortestPath.add(RouteEdge.fromRouteNode(startingNode, splitNode))
                }
                is Zone -> {
                    // If the nearest attachable is an area, we create a node on it with its area id
                    // and we link our starting point to it
                    val areaNode =
                        RouteNode.fromLatLong(
                            nearestPos.first.toLatLng(),
                            (nearestPos.second as Zone).zoneId
                        )
                    nodesForShortestPath.add(areaNode)
                    edgesForShortestPath.add(RouteEdge.fromRouteNode(startingNode, areaNode))
                }
            }

            //consider zones as fully connected graphs, so that the person can go from one entrance to all the other
            val areasnodes = nodesForShortestPath.groupBy { it.areaId }
            for (nodesByArea in areasnodes.filter { it.key != targetZoneId && it.key != null }.values) {
                for (node in nodesByArea) {
                    for (node2 in nodesByArea) {
                        val edge = RouteEdge.fromRouteNode(node, node2)
                        if (!edgesForShortestPath.contains(edge) && node != node2) {
                            edgesForShortestPath.add(edge)
                        }
                    }
                }
            }
            //we consider target Zone center as the target node

            nodesForShortestPath.add(targetZoneCenter)
            for (node in areasnodes[targetZoneId]!!) {
                val edge = RouteEdge.fromRouteNode(node, targetZoneCenter)
                edge.weight = 0.0
                edgesForShortestPath.add(edge)
            }

        }

        /**
         * Applies Dijkstra algorithm on the given graph to find the shortest path from the starting
         * node to each other node of the graph
         * @param nodes set of nodes in the graph
         * @param edges set of edges in the graph
         * @param start starting node of the graph
         * @return a map describing the shortest path tree, the root being the starting node,
         * linking each node to its previous node on the tree
         */
        fun dijkstra(
            nodes: Set<RouteNode>,
            edges: Set<RouteEdge>,
            start: RouteNode
        ): Map<RouteNode, RouteNode?> {
            if (start !in nodes) throw IllegalArgumentException("Start route not in set of nodes for Dijkstra")
            val done: MutableSet<RouteNode> = mutableSetOf()

            /**
             * converts a set of edges to the adjacency list of a node
             * @param edges set of edges
             * @return adjacency list with each key being the node and the value their corresponding set of adjacent nodes
             */
            fun toAdjacencyList(edges: Set<RouteEdge>): Map<RouteNode, Set<Pair<RouteNode, Double>>> {
                val adjList: MutableMap<RouteNode, MutableSet<Pair<RouteNode, Double>>> =
                    mutableMapOf()
                for (edge in edges) {
                    if (!adjList.containsKey(edge.start!!)) {
                        adjList[edge.start!!] = mutableSetOf()
                    }
                    adjList[edge.start]!!.add(Pair(edge.end!!, edge.weight!!))
                    if (!adjList.containsKey(edge.end)) {
                        adjList[edge.end!!] = mutableSetOf()
                    }
                    adjList[edge.end]!!.add(Pair(edge.start!!, edge.weight!!))
                }
                return adjList
            }

            val adjList = toAdjacencyList(edges)

            // costs to get to each node
            val costs = nodes.map { it to Double.MAX_VALUE }.toMap().toMutableMap()
            costs[start] = 0.0

            // set of predecessors on the shortest path tree
            val previous: MutableMap<RouteNode, RouteNode?> =
                nodes.map { it to null }.toMap().toMutableMap()

            while (done != nodes) {
                // get remaining node with lowest cost
                val v: RouteNode = costs
                    .filter { !done.contains(it.key) }
                    .minByOrNull { it.value }!!
                    .key

                // compute the cost to get to neighboring nodes and update the weight and previous node if a shorter path is found

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


    /**
     * Draws a new route from the "chemin" variable, a list of LatLng and converts it into a Polyline
     */
    fun drawRoute() {
        undrawRoute()
        if (chemin.isNotEmpty()) {
            var start = chemin[0]
            currentTarget = chemin[1]
            val cheminTemp = chemin.drop(1)
            for (end in cheminTemp) {
                route.add(
                    map!!.addPolyline(
                        PolylineOptions().add(start).add(end).color(ROUTE_COLOR)
                            .width(15f)
                    )
                )
                start = end
            }
        }
    }
/*
    /**
    TODO consider using this function to update a route while walking
    */
    fun updateRoute() {
        if (route.isNotEmpty()) {
            val position = minus(LatLng(0.0, 0.0), currentTarget!!)
            val position2 = minus(chemin[1], currentTarget!!)
            if (norm(minus(position, position2)) < LatLngOperator.epsilon) {
                route.first().remove()
                route = route.drop(1).toMutableList()
                chemin = chemin.drop(1).toMutableList()
                if (chemin.size > 1) {
                    currentTarget = chemin[1]
                } else {
                    chemin.clear()
                    currentTarget = null
                }
            } else {
                //DO Projection and redraw the line with route[0].points = listOf(...)
            }
        }
    }
*/
    /**
     * Undraws the route
     */
    fun undrawRoute() {
        for (r in route) {
            r.remove()
        }
        route.clear()
    }

    /**
     * Gets the location of the point on the nearest attachable object.
     * Used when drawing a new line on the map,
     * @param fixed Location of the fix point of the line we are drawing
     * @param moving Location of the point we are moving
     * @param exclude eventually the attachable to exclude, for example we can't connect a zone to itself
     * @return A pair containing the coordinate to the point on the nearest attachable
     * (the same point as moving if there is no attachable around) and the attached object if any.
     */
    fun getPosOnNearestAttachableFrom(
        fixed: LatLng,
        moving: LatLng,
        exclude: Attachable?
    ): Pair<LatLng, Attachable?> {
        val v = getPosOnNearestAttachable(moving, LatLngOperator.angle(fixed, moving), exclude)
        return if (v.third != null && v.third!! < MAGNET_DISTANCE_THRESHOLD)
            Pair(v.first.toLatLng(), v.second)
        else
            Pair(moving, null)
    }

    /**
     * Gets the closest point (projection) of the point given the edge going from start to end
     * @param start the first point of the edge
     * @param end the second point of the edge
     * @param point the point to project on the edge
     * @return a RouteNode which is the closest point on the edge
     */
    fun getNearestPoint(start: RouteNode, end: RouteNode, point: LatLng): RouteNode {
        var line = minus(end.toLatLng(), start.toLatLng())
        val lineNorm = norm(line)
        line = divide(line, lineNorm)
        val p = minus(point, start.toLatLng())
        val dif = scalar(line, p)
        if (dif <= THRESHOLD)
            return start
        if (-THRESHOLD <= dif - lineNorm)
            return end
        return RouteNode.fromLatLong(plus(start.toLatLng(), time(line, dif)))
    }

    /**
     * Gets the closest point (projection) of the point to the nearest attachable
     * If the nearest attachable is a vertex, gets the vertex
     * If the nearest attachable is an edge, gets the projection on the edge
     * If the nearest attachable is a zone, gets the projection on an edge or the nearest corner of the zone
     * If the given angle is too small (< 20°)and we are attaching to an area, we decide to not attach to help the admin
     * drawing lines near zones.
     * @param point the point
     * @param angle an eventual angle which represents the orientation of a line we are drawing
     * @param point the point to project on the edge
     * @return a triple containing :
     * A RouteNode represents
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
        for (e in zones) found(e)
        return res
    }

    /**
     * Gets the list of existing nodes and edges from the database
     * @param context the current context
     * @param lifecycleOwner the lifecycleowner to update observables
     * @return Observable which is set at true when the request has been sent
     */
    fun getNodesAndEdgesFromDB(
        context: Context?,
        lifecycleOwner: LifecycleOwner
    ): Observable<Boolean> {
        Database.currentDatabase.routeDatabase!!.getRoute(nodes, edges, zones)
        edges.observeAdd(lifecycleOwner) {
            edgeAddedNotification(context, it.value)
        }
        edges.observeRemove(lifecycleOwner) {
            edgeRemovedNotification(it.value)
        }
        return Observable(true)
    }


    /**
     * Function that handles the remove of a route from the database
     * @param edge deleted edge
     */
    fun edgeRemovedNotification(edge: RouteEdge) {
        lineToEdge[edge]?.remove()
        lineToEdge.remove(edge)
        idToEdge.remove(edge.id)
    }

    /**
     * Function that handles the addition of a route from the database
     * @param edge new edge
     */
    fun edgeAddedNotification(context: Context?, edge: RouteEdge) {
        //Remove all creation lines when we get an answer from the database
        removeAllLinesToRemove()
        val option = PolylineOptions()
        option.add(edge.start!!.toLatLng())
        option.add(edge.end!!.toLatLng())
        option.color(DEFAULT_ROAD_COLOR)
        option.clickable(true)
        val route = map!!.addPolyline(option)

        //tag used to know which polyline has been clicked
        if (context != null) {
            route.tag = edge.id
        }

        lineToEdge[edge] = route
        idToEdge[edge.id!!] = edge
    }

    /**
     * Remove all the lines used for creation from the map when we get an answer from the database
     */
    fun removeAllLinesToRemove() {
        for (line in toDeleteLines) {
            line.remove()
        }
        toDeleteLines.clear()
    }

    /**
     * Adds a new route to google map and to the temporary variables
     * @param context the current context
     */
    fun createNewRoute(context: Context?) {
        deleteMode = false
        if (tempPolyline != null) {
            tempPolyline!!.remove()
            tempVariableClear()
        }
        setupEditLine(context, map!!.cameraPosition!!.target)
        MapsFragment.instance?.showSaveButton()
    }

    /**
     * Saves the route that was being created
     */
    fun saveNewRoute() {
        deleteMode = false
        toDeleteLines.add(tempPolyline!!)
        tempPolyline!!.color = Color.GREEN
        addLine(Pair(tempLatLng[0]!!, attachables.first), Pair(tempLatLng[1]!!, attachables.second))
        tempVariableClear()
        MapsFragment.instance?.showSaveButton()
    }

    /**
     * Either removes the route that is being created, or activates the remove mode
     */
    fun removeRoute() {
        if (tempPolyline != null) {
            tempPolyline!!.remove()
            tempVariableClear()
        } else {
            deleteMode = !deleteMode
            MapsFragment.instance?.switchIconDelete()
        }
        MapsFragment.instance?.showSaveButton()
    }

    /**
     * Clears the variables
     */
    fun tempVariableClear() {
        startMarker!!.remove()
        endMarker!!.remove()

        startMarker = null
        endMarker = null
        tempPolyline = null
        tempLatLng.clear()
        attachables = Pair(null, null)
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
     * @param dragMode what function called the moveMarker method (DRAG_START, DRAG, DRAG_END)
     */
    fun moveMarker(marker: Marker, dragMode: MarkerDragMode) {
        if (dragMode == MarkerDragMode.DRAG || dragMode == MarkerDragMode.DRAG_START) {
            //Changes the coordinates of the polyline to where it can be displayed
            val points = tempPolyline!!.points
            when (marker.snippet) {
                PolygonAction.MARKER_START.toString() -> {
                    val res = getPosOnNearestAttachableFrom(
                        endMarker!!.position,
                        startMarker!!.position,
                        attachables.second
                    )
                    tempLatLng[0] = startMarker!!.position
                    attachables = Pair(res.second, attachables.second)
                    tempPolyline!!.points = listOf(res.first, points[1])
                }

                PolygonAction.MARKER_END.toString() -> {
                    val res = getPosOnNearestAttachableFrom(
                        startMarker!!.position,
                        endMarker!!.position,
                        attachables.first
                    )
                    tempLatLng[1] = endMarker!!.position
                    attachables = Pair(attachables.first, res.second)
                    tempPolyline!!.points = listOf(points[0], res.first)
                }
            }
        } else if (dragMode == MarkerDragMode.DRAG_END) {
            //On end drag, we set the position of the markers to the position of the line
            val points = tempPolyline!!.points
            startMarker!!.position = points[0]
            endMarker!!.position = points[1]
            tempLatLng[0] = startMarker!!.position
            tempLatLng[1] = endMarker!!.position
        }
    }

    /**
     * Handles the click on a polyline : if on delete mode, deletes the polyline
     * @param polyline polyline clicked
     */
    fun polylineClick(polyline: Polyline) {
        if (deleteMode) {
            removeLine(idToEdge[polyline.tag]!!)
        }
    }
}