package com.github.sdpteam15.polyevents.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.map.*
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzw
import com.google.android.gms.internal.maps.zzz
import com.google.android.gms.maps.model.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RouteMapHelperTest {
    lateinit var mockedMap: MapsInterface
    val lat = 0.0
    val lng = 0.0
    val rn1 = RouteNode("ID0", lat, lng)
    val rn2 = RouteNode("ID 1", lat, lng)

    val lat2 = 42.52010210373032
    val lng2 = 8.566237434744834
    val zoom = 18f
    var position = LatLng(lat2, lng2)
    var camera = CameraPosition(position, zoom, 0f, 0f)

    @Before
    fun setup() {
        mockedMap = Mockito.mock(MapsInterface::class.java)
        RouteMapHelper.map = mockedMap
        Mockito.`when`(mockedMap.cameraPosition).thenReturn(camera)
        RouteMapHelper.nodes.add(rn1)
        RouteMapHelper.nodes.add(rn2)
    }

    @Test
    fun removeLineTest() {
        val tag = "Test Edge"

        val routeEdge = RouteEdge.fromRouteNode(rn1, rn2, tag)
        RouteMapHelper.edges.add(routeEdge)
        RouteMapHelper.removeLine(routeEdge)
    }

    /*@Test
    fun moveMarkerTest(){
        //Marker creation
        val start = LatLng(2.5, 2.2)
        val mockedzztStart = mock(zzt::class.java)
        val mStart = Marker(mockedzztStart)
        Mockito.`when`(mockedzztStart.position).thenReturn(start)

        val end = LatLng(2.5, 3.0)
        val mockedzztEnd = mock(zzt::class.java)
        val mEnd = Marker(mockedzztEnd)
        Mockito.`when`(mockedzztEnd.position).thenReturn(end)

        RouteMapHelper.startMarker = mStart
        RouteMapHelper.endMarker = mEnd


        //Fake markers moved creation
        val mockedzzt = mock(zzt::class.java)
        val m = Marker(mockedzzt)
        Mockito.`when`(mockedzzt.snippet).thenReturn(PolygonAction.MARKER_START.toString())

        val mockedzzt2 = mock(zzt::class.java)
        val m2 = Marker(mockedzzt2)
        Mockito.`when`(mockedzzt2.snippet).thenReturn(PolygonAction.MARKER_END.toString())

        //Fake polyline
        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        val listOfPts = listOf(LatLng(lat, lng), LatLng(lat, lng))
        RouteMapHelper.tempLatLng = mutableListOf(LatLng(lat, lng), LatLng(lat, lng))

        Mockito.`when`(mockedzzz.points).thenReturn(listOfPts)
        RouteMapHelper.tempPolyline = polyline

        RouteMapHelper.moveMarker(m, MarkerDragMode.DRAG)
        assertEquals(start, RouteMapHelper.tempLatLng[0])
        RouteMapHelper.moveMarker(m2, MarkerDragMode.DRAG_START)
        assertEquals(end, RouteMapHelper.tempLatLng[1])
        RouteMapHelper.moveMarker(m, MarkerDragMode.DRAG_END)
        RouteMapHelper.moveMarker(m,MarkerDragMode.DRAG_END)
        Mockito.`when`(mockedzzt.snippet).thenReturn("TCHO")
        RouteMapHelper.moveMarker(m, MarkerDragMode.DRAG_END)
    }*/

    @Test
    fun setUpEditLineTest(){
        val mockedzzt = mock(zzt::class.java)
        val m = Marker(mockedzzt)
        Mockito.`when`(mockedMap.addMarker(anyOrNull())).thenReturn(m)

        //Fake polyline
        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        val listOfPts = listOf(LatLng(lat, lng), LatLng(lat, lng))
        RouteMapHelper.tempLatLng.clear()

        Mockito.`when`(mockedzzz.points).thenReturn(listOfPts)
        Mockito.`when`(mockedMap.addPolyline(anyOrNull())).thenReturn(polyline)
        RouteMapHelper.endMarker = null
        RouteMapHelper.startMarker = null

        RouteMapHelper.setupEditLine(null, position)
        assertEquals(true, RouteMapHelper.tempLatLng.isNotEmpty())
    }

    @Test
    fun setUpModifyMarkersTest(){
        val mockedzzt = mock(zzt::class.java)
        val m = Marker(mockedzzt)
        Mockito.`when`(mockedMap.addMarker(anyOrNull())).thenReturn(m)

        //Fake polyline
        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        val listOfPts = listOf(LatLng(lat, lng), LatLng(lat, lng))
        RouteMapHelper.tempLatLng = mutableListOf(LatLng(lat, lng), LatLng(lat, lng))

        Mockito.`when`(mockedzzz.points).thenReturn(listOfPts)
        RouteMapHelper.tempPolyline = polyline

        RouteMapHelper.endMarker = null
        RouteMapHelper.startMarker = null

        RouteMapHelper.setupModifyMarkers(null)
        assertTrue(RouteMapHelper.endMarker != null)
        assertTrue(RouteMapHelper.startMarker != null)
    }
}