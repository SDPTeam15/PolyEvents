package com.github.sdpteam15.polyevents.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import com.github.sdpteam15.polyevents.model.map.*
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzz
import com.google.android.gms.maps.model.*
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertTrue

class RouteMapHelperTest {


    @Test
    fun getShortestPathReturnsCorrectPath() {
        val node1 = RouteNode("1", 2.0, 5.0)
        val node2 = RouteNode("2", 2.0, 1.5, "1")
        val node3 = RouteNode("3", 1.5, 2.0, "1")
        val node4 = RouteNode("4", 6.0, 1.0)
        val node5 = RouteNode("5", 5.0, 5.5, "2")
        val node6 = RouteNode("6", 5.5, 5.0, "2")
        val edge1 = RouteEdge.fromRouteNode(node1, node3,"1")
        val edge2 = RouteEdge.fromRouteNode(node1, node5,"2")
        val edge3 = RouteEdge.fromRouteNode(node1, node4,"3")
        val edge4 = RouteEdge.fromRouteNode(node4, node6,"4")
        val edge5 = RouteEdge.fromRouteNode(node4, node2,"5")
        RouteMapHelper.zones.clear(this)
        RouteMapHelper.nodes.clear(this)
        RouteMapHelper.edges.clear(this)
        RouteMapHelper.zones.addAll(
            mutableListOf(
                Zone(
                    "1",
                    "zone1",
                    "1.0|1.0!2.0|1.0!2.0|2.0!1.0|2.0",
                    "coolZone"
                ), Zone(
                    "2",
                    "zone2",
                    "5.0|5.0!5.0|6.0!6.0|6.0!6.0|5.0",
                    "coolZone"
                )
            )
        )

        RouteMapHelper.nodes.addAll(
            mutableListOf(
                node1, node2, node3, node4, node5, node6
            )
        )
        RouteMapHelper.edges.addAll(
            mutableListOf(
                edge1, edge2, edge3, edge4, edge5
            )
        )
    }


        val result = RouteMapHelper.getShortestPath(LatLng(5.5, 7.0), "1")!!
        val expected = listOf(
            LatLng(5.5, 7.0),
            LatLng(5.5, 6.0),
            LatLng(5.0, 5.5),
            LatLng(2.0, 5.0),
            LatLng(1.5, 2.0)
        )
        assertEquals(expected,result)
    }

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

    @Test
    fun edgeAddedNotificationTest(){
        val tag = "Test osterone"

        val routeEdge = RouteEdge.fromRouteNode(rn1, rn2, tag)

        //Fake polyline
        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        RouteMapHelper.toDeleteLines.add(polyline)

        RouteMapHelper.edgeAddedNotification(null, routeEdge)
    }

    @Test
    fun edgeRemovedNotificationTest(){
        val tag = "Test osterone"
        val routeEdge = RouteEdge.fromRouteNode(rn1, rn2, tag)
        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        RouteMapHelper.lineToEdge[routeEdge] = polyline
        RouteMapHelper.idToEdge[tag] = routeEdge

        RouteMapHelper.edgeRemovedNotification(routeEdge)
    }

    @Test
    fun tempVariableClearTest(){
        val mockedzzt = mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzt2 = mock(zzt::class.java)
        val m2 = Marker(mockedzzt2)
        RouteMapHelper.startMarker = m
        RouteMapHelper.endMarker = m2

        RouteMapHelper.tempVariableClear()
    }

    @Test
    fun createNewRouteTest() {
        val mockedzzt = mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzt2 = mock(zzt::class.java)
        val m2 = Marker(mockedzzt2)
        RouteMapHelper.startMarker = m
        RouteMapHelper.endMarker = m2

        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        val listOfPts = listOf(LatLng(lat, lng), LatLng(lat, lng))
        RouteMapHelper.tempLatLng = mutableListOf(LatLng(lat, lng), LatLng(lat, lng))

        Mockito.`when`(mockedzzz.points).thenReturn(listOfPts)
        RouteMapHelper.tempPolyline = polyline
        Mockito.`when`(mockedMap.addPolyline(anyOrNull())).thenReturn(polyline)
        RouteMapHelper.createNewRoute(null)
    }

    @Test
    fun saveNewRouteTest(){
        val mockedzzt = mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzt2 = mock(zzt::class.java)
        val m2 = Marker(mockedzzt2)
        RouteMapHelper.startMarker = m
        RouteMapHelper.endMarker = m2

        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        val listOfPts = listOf(LatLng(lat, lng), LatLng(lat, lng))
        RouteMapHelper.tempLatLng = mutableListOf(LatLng(lat, lng), LatLng(lat, lng))

        Mockito.`when`(mockedzzz.points).thenReturn(listOfPts)
        RouteMapHelper.tempPolyline = polyline
        Mockito.`when`(mockedMap.addPolyline(anyOrNull())).thenReturn(polyline)
        RouteMapHelper.saveNewRoute()
    }

    @Test
    fun removeRouteTest(){
        val mockedzzt = mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzt2 = mock(zzt::class.java)
        val m2 = Marker(mockedzzt2)
        RouteMapHelper.startMarker = m
        RouteMapHelper.endMarker = m2

        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        val listOfPts = listOf(LatLng(lat, lng), LatLng(lat, lng))
        RouteMapHelper.tempLatLng = mutableListOf(LatLng(lat, lng), LatLng(lat, lng))

        Mockito.`when`(mockedzzz.points).thenReturn(listOfPts)
        RouteMapHelper.tempPolyline = polyline
        Mockito.`when`(mockedMap.addPolyline(anyOrNull())).thenReturn(polyline)
        RouteMapHelper.removeRoute(null)

        RouteMapHelper.tempPolyline = null
        RouteMapHelper.deleteMode = false
        RouteMapHelper.removeRoute(null)
        assertEquals(true, RouteMapHelper.deleteMode)

    }

    @Test
    fun variablesSetterGetterTest(){
        val id = 60
        RouteMapHelper.tempUid = id
        assertEquals(id, RouteMapHelper.tempUid)

        val deleteMode = false
        RouteMapHelper.deleteMode = deleteMode
        assertEquals(deleteMode, RouteMapHelper.deleteMode)

        assertEquals(mockedMap,RouteMapHelper.map)
        RouteMapHelper.lineToEdge
        RouteMapHelper.idToEdge
        RouteMapHelper.tempPolyline
    }


}