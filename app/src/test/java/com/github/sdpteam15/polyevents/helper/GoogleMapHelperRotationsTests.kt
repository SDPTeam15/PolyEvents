package com.github.sdpteam15.polyevents.helper

import com.google.android.gms.maps.model.LatLng
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.hamcrest.Matchers.closeTo
import kotlin.math.PI
import kotlin.math.sqrt
import org.hamcrest.core.Is.`is` as Is

/**
 * Unit tests for the helper functions used to rotate the
 * areas in Google Map.
 */
class GoogleMapHelperRotationsTests {

    @Test
    fun applyTransformationAndInverseGiveSamePoint() {
        val center = LatLng(46.52100506978624, 6.565499156713487)
        val point = LatLng(46.52111073013754, 6.565624214708805)

        val projectedPoint = GoogleMapHelper.equirectangularProjection(point, center)
        val transformedPoint = GoogleMapHelper.inverseEquirectangularProjection(
            projectedPoint,
            center)

        assertLatLng(transformedPoint, point)
    }

    @Test
    fun getCenterIsCorrect() {
        val points = mutableListOf(
            LatLng(46.52100506978624, 6.565499156713487),
            LatLng(46.2432100506624, 4.43459156713487)
        )

        var middle = LatLng((points[0].latitude + points[1].latitude) / 2,
            (points[0].longitude + points[1].longitude) / 2)

        var center = GoogleMapHelper.getCenter(points)

        assertLatLng(center, middle)

        points.add(LatLng(30.02938498, 45.20934809))

        middle = LatLng(40.93120003348288, 18.736479604616118)
        center = GoogleMapHelper.getCenter(points)

        assertLatLng(center, middle)
    }

    @Test
    fun distanceWithEquirectangularProjectionIsCorrect() {
        val p0 = LatLng(46.52100506978624, 6.565499156713487)
        val p1 = LatLng(46.2432100506624, 4.43459156713487)

        val center = GoogleMapHelper.getCenter(listOf(p0, p1))

        val p0c = GoogleMapHelper.equirectangularProjection(p0, center)
        val p1c = GoogleMapHelper.equirectangularProjection(p1, center)

        val distance = sqrt((p0c.first - p1c.first) * (p0c.first - p1c.first) + (p0c.second- p1c.second) * (p0c.second - p1c.second))
        assertThat(distance, Is(166349.3295962308))
    }

    @Test
    fun degreeToRadianIsCorrect() {
        assertThat(GoogleMapHelper.degreeToRadian(90.0), Is(PI / 2))
        assertThat(GoogleMapHelper.degreeToRadian(45.0), Is(PI / 4))
    }

    @Test
    fun radianToDegreeIsCorrect() {
        assertThat(GoogleMapHelper.radianToDegree(PI / 2), Is(90.0))
        assertThat(GoogleMapHelper.radianToDegree(PI / 4), Is(45.0))
    }

    @Test
    fun getDirectionIsCorrect() {
        var p = Pair(1.0, 1.0)
        assertThat(GoogleMapHelper.getDirection(p), Is(PI / 4))

        p = Pair(-1.0, 2.0)
        assertThat(GoogleMapHelper.getDirection(p), Is(PI - 1.1071487177940904))
    }

    @Test
    fun computeRotationIsCorrect() {
        var p = Pair(3.14, 1.0)
        var angle = PI / 2

        var rotated = GoogleMapHelper.computeRotation(p, angle)
        var expected = Pair(-1.0, 3.14)
        assertPoints(rotated, expected)

        angle = PI / 8
        rotated = GoogleMapHelper.computeRotation(p, angle)
        expected = Pair(2.5182983, 2.12550551)
        assertPoints(rotated, expected)
    }

    @Test
    fun computeMeanRadiusIsCorrect() {
        val points = listOf(
            Pair(-1.0, 0.5),
            Pair(1.0, -0.5),
            Pair(1.0, 0.5),
            Pair(-1.0, -0.5)
        )

        assertThat(GoogleMapHelper.computeMeanRadius(points),
            Is(sqrt(1.25)))
    }

    @Test
    fun applyRotationIsCorrect() {
        val point = LatLng(46.52100506978624, 6.565499156713487)
        val center = LatLng(46.52111073013754, 6.565624214708805)
        val angle = PI / 3

        val result = GoogleMapHelper.applyRotation(point, angle, center)
        println(result)
        val expected = LatLng(46.520983377771145, 6.565694669641021)
        assertLatLng(result, expected)
    }

    private fun assertPoints(actual: Pair<Double, Double>, expected: Pair<Double, Double>) {
        val error = 0.0001
        assertThat(actual.first, closeTo(expected.first, error))
        assertThat(actual.second, closeTo(expected.second, error))
    }

    private fun assertLatLng(actual: LatLng, expected: LatLng) {
        val error = 1e-10
        assertThat(actual.latitude, closeTo(expected.latitude, error))
        assertThat(actual.longitude, closeTo(expected.longitude, error))
    }
}