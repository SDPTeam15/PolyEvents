package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng
import io.grpc.KnownLength
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sqrt

const val THRESHOLD = 0.001
object RouteMapHelper {
    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()

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
     * TODO
     */
    fun getShortestPath(startPosition: LatLng, targetZoneId: String): List<LatLng> {
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
    fun getPosOnNearestAttachable(point: LatLng, exclude: Attachable?): LatLng {
        // TODO
        return point
    }

    /**
     * TODO
     */
    fun getNodesAndEdgesFromDB(): Observable<Boolean> {
        TODO()
    }

    fun edgeAddedNotification(edge: RouteEdge){TODO()}
    fun edgeRemovedNotification(edge: RouteEdge){TODO()}

    /**
     * TODO
     */
    fun getNearestPoint(start: LatLng, end: LatLng, point: LatLng): LatLng {
        var line = minus(end, start)
        val lineNorm = norm(line)
        line = divide(line, lineNorm)
        val p = minus(point, start)
        val dif = scalar(line, p)
        if(dif <= THRESHOLD)
            return start
        if(THRESHOLD <= lineNorm - dif)
            return end
        return plus(start, time(line, dif))
    }

    /**
     * TODO
     */
    fun minus(point1: LatLng, point2: LatLng) =
        LatLng(point1.latitude - point2.latitude, point1.longitude - point2.longitude)

    /**
     * TODO
     */
    fun plus(point1: LatLng, point2: LatLng) =
        LatLng(point1.latitude + point2.latitude, point1.longitude + point2.longitude)

    /**
     * TODO
     */
    fun time(point: LatLng, nbr: Double) =
        LatLng(point.latitude * nbr, point.longitude * nbr)

    /**
     * TODO
     */
    fun divide(point: LatLng, nbr: Double) =
        LatLng(point.latitude / nbr, point.longitude /nbr)

    /**
     * TODO
     */
    fun angle(start: LatLng, end: LatLng) =
        atan(start.latitude - end.latitude / start.longitude - end.longitude) / Math.PI * 180

    /**
     * TODO
     */
    fun isTooParallel(angle1: Double, angle2: Double): Boolean {
        var dif = abs(angle1 - angle2)
        dif = if (dif > 90) dif - 90 else dif
        return dif < 20
    }

    /**
     * Computes the scalar product between 2 points
     * @param point1 first point
     * @param point2 second point
     * @return distance between the points
     */
    fun scalar(point1: LatLng, point2: LatLng) =
        point1.latitude * point2.latitude + point1.longitude * point2.latitude

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
}