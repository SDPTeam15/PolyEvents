package com.github.sdpteam15.polyevents.helper

import com.google.android.gms.maps.model.LatLng
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
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

        assertThat(transformedPoint.latitude, Is(point.latitude))
        assertThat(transformedPoint.longitude, Is(point.longitude))
    }
}