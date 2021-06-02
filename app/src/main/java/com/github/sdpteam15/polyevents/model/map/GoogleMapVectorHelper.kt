package com.github.sdpteam15.polyevents.model.map

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

object GoogleMapVectorHelper {

    const val EARTH_RADIUS = 6371000
    const val TWOPIE = 2 * PI

    /**
     * Projection of vector on the perpendicular of the two positions in a cartesian space
     * @param vector vector to project
     * @param pos1 position of the first point to compute the perpendicular
     * @param pos2 position of the second point to compute the perpendicular
     */
    fun projectionVectorThroughCartesian(vector: LatLng, pos1: LatLng, pos2: LatLng): LatLng {
        val vec = equirectangularProjection(vector, LatLng(0.0,0.0))
        val v = equirectangularProjection(LatLngOperator.minus(pos1, pos2), LatLng(0.0,0.0))

        val norm = v.first * v.first + v.second * v.second
        val scalar = (vec.first * v.first + vec.second * v.second) / norm
        return inverseEquirectangularProjection(Pair(scalar * v.first, scalar * v.second), LatLng(0.0,0.0))
    }

    /**
     * Compute the center (mean) of the given points.
     * @param list: the list of the latitude/longitude pairs of the points
     * to compute the center of.
     * @return the center of these points
     */
    fun getCenter(list: List<LatLng?>): LatLng {
        var lat = 0.0
        var lng = 0.0
        for (coord in list) {
            lat += coord!!.latitude
            lng += coord.longitude
        }

        return LatLng(lat / list.size, lng / list.size)
    }

    /**
     * Projects a point in lat/lng format onto a cartesian coordinates using the
     * equirectangular projection (approximation).
     * @param point: the point to project
     * @param center: the center of the projection
     * @return the cartesian coordinates of the points in meter wrt to the given center.
     */
    fun equirectangularProjection(point: LatLng?, center: LatLng): Pair<Double, Double> {
        val x = EARTH_RADIUS * (degreeToRadian(point!!.longitude - center.longitude)) * cos(
            degreeToRadian(center.latitude)
        )
        val y = EARTH_RADIUS * (degreeToRadian(point.latitude - center.latitude))
        return Pair(x, y)
    }

    /**
     * Convert cartesian coordinates back to lat/lng coordinates using the
     * equirectangular transformation.
     * @param point: the point to convert in cartesian coordinates
     * @param center: the center of the projection used in lat/lng coordinates
     * @return point in lat/lng coordinates
     */
    fun inverseEquirectangularProjection(point: Pair<Double, Double>, center: LatLng): LatLng {
        val lng =
            radianToDegree(
                point.first / (EARTH_RADIUS * cos(
                    degreeToRadian(center.latitude)
                ))
            ) + center.longitude
        val lat = radianToDegree(point.second / EARTH_RADIUS) + center.latitude

        return LatLng(lat, lng)
    }

    /**
     * Convert an angle in degrees to radians
     * @param angle: angle in degrees to convert
     * @return angle in radians
     */
    fun degreeToRadian(angle: Double): Double {
        return angle * TWOPIE / 360.0
    }

    /**
     * Convert an angle in radians to degrees
     * @param angle: angle in radians to convert
     * @return angle in degrees
     */
    fun radianToDegree(angle: Double): Double {
        return angle * 360.0 / TWOPIE
    }

    /**
     * Compute the direction (angle wrt x-axis) of the given point
     * @param point: the point in cartesian coordinates to compute the direction of
     * @return direction in radians
     */
    fun getDirection(point: Pair<Double, Double>): Double {
        val direction = atan(point.second / point.first)
        return if (point.first < 0) PI + direction else direction
    }

    /**
     * Compute the rotation of the given point in cartesian coordinates by
     * the given angle
     * @param point: point in cartesian coordinates to rotate
     * @param angle: rotation angle in radians
     * @return rotated point in cartesian coordinates
     */
    fun computeRotation(point: Pair<Double, Double>, angle: Double): Pair<Double, Double> {
        val cosA = cos(angle)
        val sinA = sin(angle)

        return Pair(
            point.first * cosA - point.second * sinA,
            point.first * sinA + point.second * cosA
        )
    }

    /**
     * Compute the mean radius radius of the given points
     * forming a polygon wrt its center (implicitely).
     * @param points: the points forming the polygon
     * @return the mean radius from the center of the polygon
     */
    fun computeMeanRadius(points: List<Pair<Double, Double>>): Double {
        var runningRadius = 0.0
        points.forEach {
            runningRadius += sqrt(it.first * it.first + it.second * it.second)
        }
        return runningRadius / points.size
    }

    /**
     * Apply the whole transformation needed to rotate a point in lat/lng coordinates.
     * @param point: the point in lat/lng coordinates to rotate
     * @param angle: the angle of the rotation
     * @param center: the center of the rotation in lat/lng coordinates
     * @return the rotated point in lat/lng coordinates
     */
    fun applyRotation(point: LatLng?, angle: Double, center: LatLng): LatLng {
        val pointCartesian = equirectangularProjection(point, center)
        val rotatedCartesian = computeRotation(pointCartesian, angle)
        return inverseEquirectangularProjection(rotatedCartesian, center)
    }
}