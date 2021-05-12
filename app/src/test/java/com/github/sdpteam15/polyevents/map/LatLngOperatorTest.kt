package com.github.sdpteam15.polyevents.map

import com.github.sdpteam15.polyevents.model.map.LatLngOperator
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.angle
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.divide
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.epsilon
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.getIntersection
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.isOnSegment
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.isTooParallel
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.minus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.norm
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.plus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.polygonUnion
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.project
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.scalar
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.squaredEuclideanDistance
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.squaredNorm
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.time
import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LatLngOperatorTest {
    private val a = LatLng(1.0, 1.0)
    private val b = LatLng(1.0, 2.0)
    private val zero = LatLng(0.0, 0.0)

    @Test
    fun minusTest() {
        assertEquals(LatLng(0.0, -1.0), minus(a, b))
    }

    @Test
    fun plusTest() {
        assertEquals(LatLng(2.0, 3.0), plus(a, b))
    }

    @Test
    fun timeTest() {
        assertEquals(LatLng(3.0, 6.0), time(b, 3.0))
    }

    @Test
    fun divideTest() {
        assertEquals(LatLng(0.25, 0.5), divide(b, 4.0))
    }

    @Test
    fun angleTest() {
        assertEquals(0.0, angle(b, a))
        assertEquals(45.0, angle(zero, a))
    }

    @Test
    fun isTooParallelTest() {
        assert(isTooParallel(-90.0, 90.0))
        assert(isTooParallel(-90.0, 80.0))
        assert(isTooParallel(-90.0, -80.0))
        assert(isTooParallel(10.0, 0.0))
        assert(!isTooParallel(0.0, 90.0))
        assert(!isTooParallel(20.0, -70.0))
        assert(!isTooParallel(20.0, -10.0))
    }

    @Test
    fun scalarTest() {
        assertEquals(3.0, scalar(a, b))
        assertEquals(0.0, scalar(a, zero))
    }

    @Test
    fun euclideanDistanceTest() {
        assertEquals(1.0, euclideanDistance(a, b))
        assertEquals(sqrt(2.0), euclideanDistance(a, zero))
        assertEquals(sqrt(5.0), euclideanDistance(b, zero))
        assertEquals(1.0, euclideanDistance(a.longitude, a.latitude, b.longitude, b.latitude))
        assertEquals(
            sqrt(2.0),
            euclideanDistance(a.longitude, a.latitude, zero.longitude, zero.latitude)
        )
        assertEquals(
            sqrt(5.0),
            euclideanDistance(b.longitude, b.latitude, zero.longitude, zero.latitude)
        )
    }

    @Test
    fun squaredNormTest() {
        assertEquals(2.0, squaredNorm(a))
        assertEquals(0.0, squaredNorm(zero))
        assertEquals(5.0, squaredNorm(b))
        assertEquals(2.0, squaredNorm(a.longitude, a.latitude))
        assertEquals(0.0, squaredNorm(zero.longitude, zero.latitude))
        assertEquals(5.0, squaredNorm(b.longitude, b.latitude))
    }

    @Test
    fun normTest() {
        assertEquals(sqrt(2.0), norm(a))
        assertEquals(0.0, norm(zero))
        assertEquals(sqrt(5.0), norm(b))
    }

    @Test
    fun squaredEuclideanDistanceTest() {
        assertEquals(1.0, squaredEuclideanDistance(a, b))
        assertEquals(2.0, squaredEuclideanDistance(a, zero))
        assertEquals(5.0, squaredEuclideanDistance(b, zero))
        assertEquals(
            1.0,
            squaredEuclideanDistance(a.longitude, a.latitude, b.longitude, b.latitude)
        )
        assertEquals(
            2.0,
            squaredEuclideanDistance(a.longitude, a.latitude, zero.longitude, zero.latitude)
        )
        assertEquals(
            5.0,
            squaredEuclideanDistance(b.longitude, b.latitude, zero.longitude, zero.latitude)
        )
    }

    @Test
    fun getIntersectionTest() {
        val a = LatLng(1.0, 0.0)
        val b = LatLng(3.0, 4.0)
        val c = LatLng(3.0, 0.0)
        val d = LatLng(1.0, 4.0)
        val mid = LatLng(2.0, 2.0)
        assertEquals(mid, getIntersection(a, b, c, d))
        assertEquals(null, getIntersection(a, c, b, d))
        assertEquals(a, getIntersection(a, c, a, d))
        assertEquals(c, getIntersection(a, c, d, c))
        assertEquals(mid, getIntersection(mid, d, a, b))
        assertEquals(mid, getIntersection(mid, d, a, b))
        assertEquals(null, getIntersection(c, b, mid, d))
    }

    @Test
    fun isOnSegmentTest() {
        val a = LatLng(1.0, 0.0)
        val b = LatLng(3.0, 4.0)
        val c = LatLng(3.0, 0.0)
        val d = LatLng(1.0, 4.0)
        val mid = LatLng(2.0, 2.0)
        assert(isOnSegment(a, b, mid))
        assert(isOnSegment(b, a, mid))
        assert(!isOnSegment(a, b, c))
        assert(isOnSegment(c, d, mid))
        assert(!isOnSegment(d, mid, c))
        assert(!isOnSegment(b, mid, a))
    }

    @Test
    fun projectTest() {
        val a = LatLng(1.0, 0.0)
        val b = LatLng(3.0, 4.0)
        val c = LatLng(3.0, 0.0)
        val mid = LatLng(2.0, 2.0)
        assertEquals(minus(mid, a), project(minus(mid, a), minus(b, a)))
        assertEquals(c, project(b, a))
    }

    @Test
    fun polygonUnionTestWith() {
        var subject = LatLngOperator.Polygon(
            listOf(
                LatLng(0.0, 1.0), LatLng(0.0, 2.0), LatLng(3.0, 2.0), LatLng(3.0, 1.0)
            )
        )
        var clip = LatLngOperator.Polygon(
            listOf(
                LatLng(1.0, 0.0), LatLng(2.0, 0.0), LatLng(2.0, 3.0), LatLng(1.0, 3.0)
            )
        )
        var expected = listOf(
            LatLng(1.0, 0.0),
            LatLng(1.0, 1.0),
            LatLng(0.0, 1.0),
            LatLng(0.0, 2.0),
            LatLng(1.0, 2.0),
            LatLng(1.0, 3.0),
            LatLng(2.0, 3.0),
            LatLng(2.0, 2.0),
            LatLng(3.0, 2.0),
            LatLng(3.0, 1.0),
            LatLng(2.0, 1.0),
            LatLng(2.0, 0.0)
        )
        var polygon = polygonUnion(subject, clip)
        assertPolygonsEquivalent(polygon.first, expected)
        assertNull(polygon.second)

        /////////////////////////////////////////////////////////////////////////////////////
        subject = LatLngOperator.Polygon(
            listOf(
                LatLng(0.0, 1.0), LatLng(0.0, 3.0), LatLng(5.0, 3.0), LatLng(5.0, 1.0)
            )
        )
        clip = LatLngOperator.Polygon(
            listOf(
                LatLng(1.0, 0.0),
                LatLng(4.0, 0.0),
                LatLng(4.0, 4.0),
                LatLng(3.0, 4.0),
                LatLng(3.0, 2.0),
                LatLng(2.0, 2.0),
                LatLng(2.0, 4.0),
                LatLng(1.0, 4.0)
            )
        )
        expected = listOf(
            LatLng(1.0, 0.0),
            LatLng(1.0, 1.0),
            LatLng(0.0, 1.0),
            LatLng(0.0, 3.0),
            LatLng(1.0, 3.0),
            LatLng(1.0, 4.0),
            LatLng(2.0, 4.0),
            LatLng(2.0, 3.0),
            LatLng(3.0, 3.0),
            LatLng(3.0, 4.0),
            LatLng(4.0, 4.0),
            LatLng(4.0, 3.0),
            LatLng(5.0, 3.0),
            LatLng(5.0, 1.0),
            LatLng(4.0, 1.0),
            LatLng(4.0, 0.0)
        )
        polygon = polygonUnion(subject, clip)
        assertPolygonsEquivalent(polygon.first, expected)
        assertNull(polygon.second)

        /////////////////////////////////////////////////////////////////////////////////////
        subject = LatLngOperator.Polygon(
            listOf(
                LatLng(0.0, 1.0),
                LatLng(5.0, 1.0),
                LatLng(1.0, 3.0),
                LatLng(5.0, 5.0),
                LatLng(1.0, 7.0),
                LatLng(5.0, 9.0),
                LatLng(0.0, 9.0)
            )
        )
        clip = LatLngOperator.Polygon(
            listOf(
                LatLng(2.0, 0.0),
                LatLng(3.0, 0.0),
                LatLng(3.0, 10.0),
                LatLng(2.0, 10.0)
            )
        )
        expected = listOf(
            LatLng(0.0, 1.0),
            LatLng(2.0, 1.0),
            LatLng(2.0, 0.0),
            LatLng(3.0, 0.0),
            LatLng(3.0, 1.0),
            LatLng(5.0, 1.0),
            LatLng(3.0, 2.0),
            LatLng(3.0, 4.0),
            LatLng(5.0, 5.0),
            LatLng(3.0, 6.0),
            LatLng(3.0, 8.0),
            LatLng(5.0, 9.0),
            LatLng(3.0, 9.0),
            LatLng(3.0, 10.0),
            LatLng(2.0, 10.0),
            LatLng(2.0, 9.0),
            LatLng(0.0,9.0)
        )
        polygon = polygonUnion(subject, clip)
        assertPolygonsEquivalent(polygon.first, expected)
        println(polygon.second)
    }


    fun assertPolygonsEquivalent(list1: List<LatLng>, list2: List<LatLng>) {
        println(list1)
        println(list2)
        if (list1.isEmpty() && list2.isEmpty()) {
            return
        } else {
            assert(!(list1.isEmpty() || list2.isEmpty()))
        }

        val list1ToCompare = if (list1.last() == list1.first()) {
            list1.dropLast(1)
        } else {
            list1
        }
        val list2ToCompare = if (list2.last() == list2.first()) {
            list2.dropLast(1)
        } else {
            list2
        }
        val size = list1ToCompare.size
        assert(size == list2ToCompare.size)
        var firstElementList1: Int? = null
        for (i in list1.indices) {
            if (list1[i] == list2.first()) {
                firstElementList1 = i
                break
            }
        }
        assert(firstElementList1 != null)

        if (euclideanDistance(
                list1ToCompare[(firstElementList1!! + 1) % size],
                list2ToCompare[1]
            ) < epsilon
        ) {
            for (i in 1 until size) {
                assertLatLngCloseEnough(
                    list1ToCompare[(firstElementList1 + i) % size],
                    list2ToCompare[i]
                )
            }
        } else {
            for (i in 1 until size) {
                assertLatLngCloseEnough(
                    list1ToCompare[(firstElementList1 - i + size) % size],
                    list2ToCompare[i]
                )
            }
        }

    }

    private fun assertLatLngCloseEnough(expected: LatLng, actual: LatLng) {
        assert(euclideanDistance(expected, actual) < epsilon)
    }
}

