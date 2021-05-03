package com.github.sdpteam15.polyevents.model.map

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sqrt

/**
 * Object containing methods to compute various metrics with LatLng
 * TODO take into account the curvature of the earth to compute distances instead of considering latitude and longitude as cartesian coordinates
 */
object LatLngOperator {
    /**
     * Used to check if floating point values are closed enough to be considered as equal
     */
    private const val epsilon = 1e-10

    /**
     * Returns the coordinate-wise subtraction of the first point by the second one
     * @param point1 the first point
     * @param point2 the second point
     * @return the subtraction of point 1 by point 2
     */
    fun minus(point1: LatLng, point2: LatLng) =
        LatLng(point1.latitude - point2.latitude, point1.longitude - point2.longitude)

    /**
     * Returns the coordinate-wise addition of the first point by the second one
     * @param point1 the first point
     * @param point2 the second point
     * @return the addition of point 1 by point 2
     */
    fun plus(point1: LatLng, point2: LatLng) =
        LatLng(point1.latitude + point2.latitude, point1.longitude + point2.longitude)

    /**
     * Returns the coordinate-wise multiplication of the point by a scalar
     * @param point the point to multiply
     * @param nbr the scalar to multiply
     * @return the multiplication of the point by the given scalar
     */
    fun time(point: LatLng, nbr: Double) =
        LatLng(point.latitude * nbr, point.longitude * nbr)

    /**
     * Returns the coordinate-wise division of the point by a scalar
     * @param point the point to be divided
     * @param nbr the scalar to divide by
     * @return the division of the point by the given scalar
     */
    fun divide(point: LatLng, nbr: Double) =
        LatLng(point.latitude / nbr, point.longitude / nbr)

    /**
     * Returns the angle in degrees between the horizontal x axis and the line passing through the two given points
     * @param start the first point
     * @param end the second point
     * @return the angle between the x axis and the line passing though start and end
     */
    fun angle(start: LatLng, end: LatLng) =
        (atan((start.latitude - end.latitude) / (start.longitude - end.longitude)) / Math.PI) * 180

    /**
     * Checks whether two angles are close enough to each other, i.e. if the difference between the lines described by the given angles is less than 20°.
     * @param angle1 the first angle
     * @param angle2 the second angle
     * @return true if the angles are less than 20° apart, else return false
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

        val denom =
            ((start1.latitude - end1.latitude) * (start2.longitude - end2.longitude) - (start1.longitude - end1.longitude) * (start2.latitude - end2.latitude))
        if (denom < epsilon) {
            //the lines are parallel
            return null
        }
        val t =
            ((start1.latitude - start2.latitude) * (start2.longitude - end2.longitude) - (start1.longitude - start2.longitude) * (start2.latitude - end2.latitude)) / denom
        val s =
            ((end1.latitude - start1.latitude) * (start1.longitude - start2.longitude) - (end1.longitude - start1.longitude) * (start1.latitude - start2.latitude)) / denom
        if (!(t in 0.0..1.0 && s in 0.0..1.0)) {
            //the intersection point is outside the boundaries formed by the extremities of the segments
            return null
        }
        return plus(start1, time(minus(end1, start1), t))
    }

    /**
     * Checks is a point is on a given segment
     * @param start the segment start
     * @param end the segment end
     * @param point the point to be checked if on the segment or not
     * @return true if the point is on the segment, false otherwise
     */
    fun isOnSegment(start: LatLng, end: LatLng, point: LatLng): Boolean {
        val a = minus(point, start)
        val b = minus(end, start)
        //if projection on the segment is (almost) the same, return checks if the point lies inside the boundaries formed by the two points
        return if (euclideanDistance(point, project(a, b)) > epsilon) false
        else ((point.latitude in start.latitude..end.latitude && point.longitude in start.longitude..end.longitude) ||
                (point.latitude in start.latitude..end.latitude && point.longitude in end.longitude..start.longitude) ||
                (point.latitude in end.latitude..start.latitude && point.longitude in end.longitude..start.longitude) ||
                (point.latitude in end.latitude..start.latitude && point.longitude in start.longitude..end.longitude))
    }

    /**
     * Computes the projection of the vector a on the vector b
     * @param a the vector to project
     * @param b the vector on which we want to project
     * @return the projection of a on b
     */
    fun project(a: LatLng, b: LatLng): LatLng {
        return time(b, scalar(a, divide(b, squaredNorm(b))))
    }
}