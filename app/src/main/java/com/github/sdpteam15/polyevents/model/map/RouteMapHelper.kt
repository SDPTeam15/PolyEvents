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
        nodesForShortestPath.addAll(nodes)
        val edgesForShortestPath = mutableSetOf<RouteEdge>()
        edgesForShortestPath.addAll(edges)


        // Add current position as starting node

        val startingNode = RouteNode.fromLatLong(startPosition)
        nodesForShortestPath.add(startingNode)
        when (nearestPos.second) {
            is RouteNode -> {
                edgesForShortestPath.add(
                    RouteEdge(
                        null,
                        startingNode,
                        RouteNode.fromLatLong(nearestPos.first)
                    )
                )
            }
            is RouteEdge -> {
                edgesForShortestPath.remove(nearestPos.second)
                val splitNode = RouteNode.fromLatLong(nearestPos.first)
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
                    RouteNode.fromLatLong(nearestPos.first, (nearestPos.second as Zone).zoneId)
                nodesForShortestPath.add(areaNode)
                edgesForShortestPath.add(RouteEdge(null, startingNode, areaNode))
            }
        }

        //consider zones as fully connected graphs
        val areasnodes = nodesForShortestPath.groupBy { it.areaId }
        for (nodesByArea in areasnodes.values) {
            for (node in nodesByArea) {
                for (node2 in nodesByArea) {
                    val edge = RouteEdge(null, node, node2)
                    if (!edgesForShortestPath.contains(edge)) {
                        edgesForShortestPath.add(edge)
                    }
                }
            }
        }
        RouteNode.fromLatLong()
        val startAttachable = RouteNode.fromLatLong()
        nodesForShortestPath.add(startAttachable)
        edgesForShortestPath

        return listOf()
    }

    data class Graph<T>(
        val vertices: Set<T>,
        val edges: Map<T, Set<T>>,
        val weights: Map<Pair<T, T>, Int>
    )

    fun <T> dijkstra(graph: Graph<T>, start: T): Map<T, T?> {
        val S: MutableSet<T> =
            mutableSetOf() // a subset of vertices, for which we know the true distance

        val delta = graph.vertices.map { it to Int.MAX_VALUE }.toMap().toMutableMap()
        delta[start] = 0

        val previous: MutableMap<T, T?> = graph.vertices.map { it to null }.toMap().toMutableMap()

        while (S != graph.vertices) {
            val v: T = delta
                .filter { !S.contains(it.key) }
                .minBy { it.value }!!
                .key

            graph.edges.getValue(v).minus(S).forEach { neighbor ->
                val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

                if (newPath < delta.getValue(neighbor)) {
                    delta[neighbor] = newPath
                    previous[neighbor] = v
                }
            }

            S.add(v)
        }

        return previous.toMap()
    }

    fun <T> shortestPath(shortestPathTree: Map<T, T?>, start: T, end: T): List<T> {
        fun pathTo(start: T, end: T): List<T> {
            if (shortestPathTree[end] == null) return listOf(end)
            return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
        }

        return pathTo(start, end)
    }

    fun dijkstra(nodes: Set<RouteNode>, edges: Set<RouteEdge>) {

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
        exclude: Attachable? = null
    ): Pair<LatLng, Attachable?> {
        // TODO
        return Pair(point, RouteNode(null, 0.0, 0.0, null))
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