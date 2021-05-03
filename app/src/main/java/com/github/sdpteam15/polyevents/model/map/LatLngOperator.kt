package com.github.sdpteam15.polyevents.model.map

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sqrt

object LatLngOperator {

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
}