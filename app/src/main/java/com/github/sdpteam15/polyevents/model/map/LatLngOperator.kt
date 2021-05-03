package com.github.sdpteam15.polyevents.model.map

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sqrt

object LatLngOperator {

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
        LatLng(point.latitude / nbr, point.longitude / nbr)

    /**
     * TODO
     */
    fun angle(start: LatLng, end: LatLng) =
        (atan((start.latitude - end.latitude) / (start.longitude - end.longitude)) / Math.PI) * 180

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
        point1.latitude * point2.latitude + point1.longitude * point2.longitude

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
        squaredNorm(point.longitude, point.latitude)

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

    /**
     * Returns the intersection point between 2 segments
     * @param start1 start of the first segment
     * @param end1 end of the first segment
     * @param start2 start of the second segment
     * @param end2 end of the second segment
     * @return The intersection point of the two segments, null if the two segments do not intersect
     */
    fun getIntersection(start1: LatLng, end1: LatLng, start2: LatLng, end2: LatLng): LatLng? {
        return null
    }

    /**
     * Checks is a point is on a given segment
     * @param start the segment start
     * @param end the segment end
     * @param point the point to be checked if on the segment or not
     * @return true if the point is on the segment, false otherwise
     */
    fun isOnSegment(start: LatLng, end: LatLng, point: LatLng): Boolean {
        return false
    }
}