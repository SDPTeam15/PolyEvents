package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sqrt


object RouteMapHelper {
    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()
    val zones = ObservableList<Zone>()

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
        val targetZoneCenter = RouteNode.fromLatLong(zones.first { it.zoneId == targetZoneId }.getZoneCenter())

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
                        RouteNode.fromLatLong(nearestPos.first.toLatLng(), (nearestPos.second as Zone).zoneId)
                    nodesForShortestPath.add(areaNode)
                    edgesForShortestPath.add(RouteEdge(null, startingNode, areaNode))
                }
            }

            //consider zones as fully connected graphs
            val areasnodes = nodesForShortestPath.groupBy { it.areaId }
            for (nodesByArea in areasnodes.filter { it.key != targetZoneId }.values) {
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
            for (node in areasnodes[targetZoneId]!!){
                val edge = RouteEdge(null,node,targetZoneCenter)
                edge.weight = 0.0
                edgesForShortestPath.add(edge)
            }

        }

        setUpGraph()
        val shortestPaths = dijkstra(nodesForShortestPath, edgesForShortestPath, startingNode)

        val pointlist : MutableList<LatLng> = mutableListOf()
        var lastnode = targetZoneCenter
        while(lastnode != startingNode){
            lastnode = shortestPaths[lastnode]!!
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
            adjList[edge.start]!!.add(Pair(edge.end,edge.weight!!))
            if (!adjList.containsKey(edge.end)) {
                adjList[edge.end] = mutableSetOf()
            }
            adjList[edge.end]!!.add(Pair(edge.start,edge.weight!!))
        }
        return adjList
    }

    fun dijkstra(nodes: Set<RouteNode>, edges: Set<RouteEdge>, start: RouteNode): Map<RouteNode,RouteNode?> {
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

            for(neighbor in adjList[v]!!.filter { !done.contains(it.first) })
            {
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

    fun <T> shortestPath(shortestPathTree: Map<T, T?>, start: T, end: T): List<T> {
        fun pathTo(start: T, end: T): List<T> {
            if (shortestPathTree[end] == null) return listOf(end)
            return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
        }

        return pathTo(start, end)
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
    ): Triple<RouteNode, Attachable?, Double?> {
        // TODO
        return Triple(RouteNode(null, 0.0, 0.0, null), RouteNode(null, 0.0, 0.0, null),null)
    }

    /**
     * TODO
     */
    fun getNodesAndEdgesFromDB(): Observable<Boolean> {
        TODO()
    }


    /**
     * Computes the euclidean distance between 2 points
     * @param startX first point x coordinate
     * @param startY first point y coordinate
     * @param endX second point x coordinate
     * @param endY second point y coordinate
     * @return distance between the points
     */
    fun euclideanDistance(startX: Double, startY: Double, endX: Double, endY: Double): Double =
        sqrt(squaredEuclideanDistance(startX, startY, endX, endY))


    /**
     * Computes the squared euclidean distance between 2 points
     * @param startX first point x coordinate
     * @param startY first point y coordinate
     * @param endX second point x coordinate
     * @param endY second point y coordinate
     * @return squared euclidean distance between the points
     */
    fun squaredEuclideanDistance(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double
    ): Double {
        val dx = startX - endX
        val dy = startY - endY
        return squaredNorm(dx, dy)
    }

    /**
     * Computes the squared euclidean distance between 2 points
     * @param start first point
     * @param end second point
     * @return squared euclidean distance between the points
     */
    fun squaredEuclideanDistance(start: LatLng, end: LatLng): Double =
        squaredEuclideanDistance(start.longitude, start.latitude, end.longitude, end.latitude)

    /**
     * Computes the scalar product between 2 points
     * @param point1 first point
     * @param point2 second point
     * @return distance between the points
     */
    fun scalar(point1: LatLng, point2: LatLng) =
        point1.latitude * point2.latitude + point1.longitude * point2.latitude

    /**
     * Computes the projection product between 2 points
     * @param point1 first point
     * @param point2 second point
     * @return distance between the points
     */
    fun projectionDistance(point1: LatLng, point2: LatLng) =
        scalar(point1, point2) / squaredNorm(point1)

    /**
     * Computes the euclidean distance between 2 points
     * @param start first point
     * @param end second point
     * @return distance between the points
     */
    fun euclideanDistance(start: LatLng, end: LatLng): Double =
        euclideanDistance(start.longitude, start.latitude, end.longitude, end.latitude)

    /**
     * Computes the squared euclidean norm
     * @param point point
     */
    fun squaredNorm(point: LatLng) =
        squaredNorm(point.longitude, point.longitude)

    /**
     * Computes the euclidean norm
     * @param point point
     */
    fun norm(point: LatLng) =
        sqrt(squaredNorm(point))

    /**
     * Computes the squared euclidean norm
     * @param dx distance in x
     * @param dy distance in y
     */
    fun squaredNorm(dx: Double, dy: Double) =
        dx * dx + dy * dy
}