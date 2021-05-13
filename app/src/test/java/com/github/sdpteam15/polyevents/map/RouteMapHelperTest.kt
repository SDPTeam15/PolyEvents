package com.github.sdpteam15.polyevents.map

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.RouteDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.*
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelperFunctions.areaToFormattedStringLocation
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.time
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.addLine
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzz
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RouteMapHelperTest {


    val epsilon = 1e-4
    fun assertListLatLngCloseEnough(expected: List<LatLng>, actual: List<LatLng>) {
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) {
            assertLatLngCloseEnough(expected[i], actual[i])
        }
    }

    fun assertLatLngCloseEnough(expected: LatLng, actual: LatLng) {
        assert(LatLngOperator.euclideanDistance(expected, actual) < epsilon)
    }

    @Test
    fun getShortestPathReturnsCorrectPath() {
        val node1 = RouteNode("1", 2.0, 5.0)
        val node2 = RouteNode("2", 2.0, 1.5, "1")
        val node3 = RouteNode("3", 1.5, 2.0, "1")
        val node4 = RouteNode("4", 6.0, 1.0)
        val node5 = RouteNode("5", 5.0, 5.5, "2")
        val node6 = RouteNode("6", 5.5, 5.0, "2")
        val edge1 = RouteEdge.fromRouteNode(node1, node3, "1")
        val edge2 = RouteEdge.fromRouteNode(node1, node5, "2")
        val edge3 = RouteEdge.fromRouteNode(node1, node4, "3")
        val edge4 = RouteEdge.fromRouteNode(node4, node6, "4")
        val edge5 = RouteEdge.fromRouteNode(node4, node2, "5")
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
                    "coolZone2"
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

        val result1 = RouteMapHelper.getShortestPath(LatLng(5.5, 7.0), "1", true)!!
        val expected1 = listOf(
            LatLng(5.5, 7.0),
            LatLng(5.5, 6.0),
            LatLng(5.0, 5.5),
            LatLng(2.0, 5.0),
            LatLng(1.5, 2.0)
        )

        assertListLatLngCloseEnough(expected1, result1)
        val result2 = RouteMapHelper.getShortestPath(LatLng(1.0, 6.0), "1", true)!!
        val expected2 = listOf(
            LatLng(1.0, 6.0),
            LatLng(2.0, 5.0),
            LatLng(1.5, 2.0)
        )
        assertListLatLngCloseEnough(expected2, result2)

        val result3 = RouteMapHelper.getShortestPath(LatLng(3.5, 2.5), "2", true)!!
        val expected3 = listOf(
            LatLng(3.5, 2.5),
            LatLng(4.0, 3.0),
            LatLng(2.0, 5.0),
            LatLng(5.0, 5.5)
        )
        assertListLatLngCloseEnough(expected3, result3)

        RouteMapHelper.edges.remove(edge1)
        val result4 = RouteMapHelper.getShortestPath(LatLng(1.0, 6.0), "1", true)!!
        val expected4 = listOf(
            LatLng(1.0, 6.0),
            LatLng(2.0, 5.0),
            LatLng(6.0, 1.0),
            LatLng(2.0, 1.5)
        )
        assertListLatLngCloseEnough(expected4, result4)

        RouteMapHelper.zones.clear(this)
        RouteMapHelper.nodes.clear(this)
        RouteMapHelper.edges.clear(this)
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
        GoogleMapHelper.map = mockedMap
        Mockito.`when`(mockedMap.cameraPosition).thenReturn(camera)
        RouteMapHelper.nodes.add(rn1)
        RouteMapHelper.nodes.add(rn2)
    }

    @Test
    fun removeLineTest() {
        val tag = "Test Edge"

        val routeEdge = RouteEdge.fromRouteNode(rn1, rn2, tag)
        RouteMapHelper.edges.add(routeEdge)
        Database.currentDatabase = mock(DatabaseInterface::class.java)
        Mockito.`when`(Database.currentDatabase.routeDatabase).thenAnswer {
            val mock = mock(RouteDatabaseInterface::class.java)
            Mockito.`when`(mock.removeEdge(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
                Observable(true)
            }
            mock
        }
        RouteMapHelper.removeLine(routeEdge)
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun moveMarkerTest() {
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
        RouteMapHelper.moveMarker(m, MarkerDragMode.DRAG_END)
        Mockito.`when`(mockedzzt.snippet).thenReturn("TCHO")
        RouteMapHelper.moveMarker(m, MarkerDragMode.DRAG_END)
    }

    @Test
    fun setUpEditLineTest() {
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
    fun setUpModifyMarkersTest() {
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

    /* cannot be tested here because we define a Color.rgb() method which can not be mocked
        @Test
        fun edgeAddedNotificationTest() {
            val tag = "Test osterone"

            val routeEdge = RouteEdge.fromRouteNode(rn1, rn2, tag)
            //Fake polyline
            val mockedzzz = mock(zzz::class.java)
            val polyline = Polyline(mockedzzz)
            RouteMapHelper.toDeleteLines.add(polyline)

            RouteMapHelper.edgeAddedNotification(null, routeEdge)
        }
    */

    @Test
    fun edgeRemovedNotificationTest() {
        val tag = "Test osterone"
        val routeEdge = RouteEdge.fromRouteNode(rn1, rn2, tag)
        val mockedzzz = mock(zzz::class.java)
        val polyline = Polyline(mockedzzz)
        RouteMapHelper.lineToEdge[routeEdge] = polyline
        RouteMapHelper.idToEdge[tag] = routeEdge

        RouteMapHelper.edgeRemovedNotification(routeEdge)
    }

    @Test
    fun tempVariableClearTest() {
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
    fun saveNewRouteTest() {
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
    }

    @Test
    fun removeRouteTest() {
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
        RouteMapHelper.removeRoute()

        RouteMapHelper.tempPolyline = null
        RouteMapHelper.deleteMode = false
        RouteMapHelper.removeRoute()
        assertEquals(true, RouteMapHelper.deleteMode)

    }

    @Test
    fun variablesSetterGetterTest() {

        val deleteMode = false
        RouteMapHelper.deleteMode = deleteMode
        assertEquals(deleteMode, RouteMapHelper.deleteMode)

        assertEquals(mockedMap, GoogleMapHelper.map)
        RouteMapHelper.lineToEdge
        RouteMapHelper.idToEdge
        RouteMapHelper.tempPolyline
    }

    @Test
    fun getPosOnNearestAttachableFrom() {
        RouteMapHelper.nodes.clear()
        RouteMapHelper.edges.clear()
        RouteMapHelper.zones.clear()

        RouteMapHelper.nodes.addAll(
            listOf(
                RouteNode.fromLatLong(time(LatLng(1.5, 0.0), MAGNET_DISTANCE_THRESHOLD)),
                RouteNode.fromLatLong(time(LatLng(1.0, 1.5), MAGNET_DISTANCE_THRESHOLD)),
                RouteNode.fromLatLong(time(LatLng(-1.0, 1.5), MAGNET_DISTANCE_THRESHOLD)),
            )
        )
        RouteMapHelper.edges.add(
            RouteEdge.fromRouteNode(RouteMapHelper.nodes[1], RouteMapHelper.nodes[2])
        )
        val res = RouteMapHelper.getPosOnNearestAttachableFrom(
            time(LatLng(0.0, 0.0), MAGNET_DISTANCE_THRESHOLD),
            time(LatLng(0.0, 1.0), MAGNET_DISTANCE_THRESHOLD),
            null
        )
        assertEquals(RouteMapHelper.edges[0], res.second)//TODO
    }

    @Test
    fun getNearestPoint() {
        var exepted = LatLng(0.0, 0.0)
        var result = LatLng(0.0, 0.0)
        assertEquals(exepted, result)

        exepted = LatLng(0.0, 0.0)
        result =
            RouteMapHelper.getNearestPoint(
                RouteNode(null, 0.0, 0.0),
                RouteNode(null, 1.0, 0.0),
                LatLng(-1.0, 1.0)
            ).toLatLng()
        assertEquals(exepted, result)

        exepted = LatLng(0.0, 0.0)
        result =
            RouteMapHelper.getNearestPoint(
                RouteNode(null, 0.0, 0.0),
                RouteNode(null, 1.0, 0.0),
                LatLng(0.0, 1.0)
            ).toLatLng()
        assertEquals(exepted, result)

        exepted = LatLng(0.5, 0.0)
        result =
            RouteMapHelper.getNearestPoint(
                RouteNode(null, 0.0, 0.0),
                RouteNode(null, 1.0, 0.0),
                LatLng(0.5, 1.0)
            ).toLatLng()
        assertEquals(exepted, result)

        exepted = LatLng(1.0, 0.0)
        result =
            RouteMapHelper.getNearestPoint(
                RouteNode(null, 0.0, 0.0),
                RouteNode(null, 1.0, 0.0),
                LatLng(1.0, 1.0)
            ).toLatLng()
        assertEquals(exepted, result)

        exepted = LatLng(1.0, 0.0)
        result =
            RouteMapHelper.getNearestPoint(
                RouteNode(null, 0.0, 0.0),
                RouteNode(null, 1.0, 0.0),
                LatLng(2.0, 1.0)
            ).toLatLng()
        assertEquals(exepted, result)
    }

    @Test
    fun edgePassesThroughAZone() {
        RouteMapHelper.edges.clear()
        RouteMapHelper.nodes.clear()
        RouteMapHelper.zones.clear()

        RouteMapHelper.zones.add(
            Zone(
                zoneId = "id",
                zoneName = "name",
                location = areaToFormattedStringLocation(
                    listOf(
                        LatLng(0.0, 0.0),
                        LatLng(2.0, 0.0),
                        LatLng(2.0, 2.0),
                        LatLng(0.0, 2.0),
                    )
                ),
                description = "description"
            )
        )

        Database.currentDatabase = mock(DatabaseInterface::class.java)
        Mockito.`when`(Database.currentDatabase.routeDatabase).thenAnswer {
            val mock = mock(RouteDatabaseInterface::class.java)
            Mockito.`when`(
                mock.updateEdges(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenAnswer {
                val iterator = it!!.arguments.iterator()
                val newEdges = iterator.next() as List<RouteEdge>
                val removeEdges = iterator.next() as List<RouteEdge>

                assertEquals(2, newEdges.size)

                assertEquals(2.0, newEdges[0].start!!.latitude)
                assertEquals(3.0, newEdges[0].end!!.latitude)
                assertEquals(-1.0, newEdges[1].start!!.latitude)
                assertEquals(0.0, newEdges[1].end!!.latitude)

                assertEquals(0, removeEdges.size)

                Observable(true)
            }
            mock
        }

        addLine(
            start = Pair(LatLng(-1.0, 1.0), null),
            end = Pair(LatLng(3.0, 1.0), null)
        ).observeOnce { assert(it.value) }.then.postValue(false)

        Mockito.`when`(Database.currentDatabase.routeDatabase).thenAnswer {
            val mock = mock(RouteDatabaseInterface::class.java)
            Mockito.`when`(
                mock.updateEdges(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenAnswer {
                val iterator = it!!.arguments.iterator()
                val newEdges = iterator.next() as List<RouteEdge>
                val removeEdges = iterator.next() as List<RouteEdge>

                assertEquals(2, newEdges.size)


                assertEquals(-1.0, newEdges[0].start!!.latitude)
                assertEquals(1.0, newEdges[0].start!!.longitude)
                assertEquals(0.0, newEdges[0].end!!.latitude)
                assertEquals(2.0, newEdges[0].end!!.longitude)
                assertEquals(0.0, newEdges[1].start!!.latitude)
                assertEquals(2.0, newEdges[1].start!!.longitude)
                assertEquals(1.0, newEdges[1].end!!.latitude)
                assertEquals(3.0, newEdges[1].end!!.longitude)


                println(newEdges[0].start!!)
                println(newEdges[0].end!!)
                println(newEdges[1].start!!)
                println(newEdges[1].end!!)

                assertEquals(0, removeEdges.size)

                Observable(true)
            }
            mock
        }

        addLine(
            start = Pair(LatLng(-1.0, 1.0), null),
            end = Pair(LatLng(1.0, 3.0), null)
        ).observeOnce { assert(it.value) }.then.postValue(false)

        Database.currentDatabase = FirestoreDatabaseProvider
    }


    @Test
    fun edgeIntersection() {
        RouteMapHelper.edges.clear()
        RouteMapHelper.nodes.clear()
        RouteMapHelper.zones.clear()

        RouteMapHelper.nodes.addAll(
            listOf(
                RouteNode(id = null, latitude = -2.0, longitude = 1.0),
                RouteNode(id = null, latitude = -2.0, longitude = -1.0),
                RouteNode(id = null, latitude = -1.0, longitude = 1.0),
                RouteNode(id = null, latitude = -1.0, longitude = 0.0),
                RouteNode(id = null, latitude = 0.0, longitude = 1.0),
                RouteNode(id = null, latitude = 0.0, longitude = -1.0),
                RouteNode(id = null, latitude = 1.0, longitude = 0.0),
                RouteNode(id = null, latitude = 1.0, longitude = -1.0),
                RouteNode(id = null, latitude = 2.0, longitude = 1.0),
                RouteNode(id = null, latitude = 2.0, longitude = -1.0),
            ),
            null
        )

        var i = 0
        RouteMapHelper.edges.addAll(
            listOf(
                RouteEdge.fromRouteNode(RouteMapHelper.nodes[i++], RouteMapHelper.nodes[i++]),
                RouteEdge.fromRouteNode(RouteMapHelper.nodes[i++], RouteMapHelper.nodes[i++]),
                RouteEdge.fromRouteNode(RouteMapHelper.nodes[i++], RouteMapHelper.nodes[i++]),
                RouteEdge.fromRouteNode(RouteMapHelper.nodes[i++], RouteMapHelper.nodes[i++]),
                RouteEdge.fromRouteNode(RouteMapHelper.nodes[i++], RouteMapHelper.nodes[i++]),
            )
        )

        // │ │ │   │
        // ├─┴─┼─┬─┤
        // │   │ │ │

        Database.currentDatabase = mock(DatabaseInterface::class.java)
        Mockito.`when`(Database.currentDatabase.routeDatabase).thenAnswer {
            val mock = mock(RouteDatabaseInterface::class.java)
            Mockito.`when`(
                mock.updateEdges(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenAnswer {
                val iterator = it!!.arguments.iterator()
                val newEdges = iterator.next() as List<RouteEdge>
                val removeEdges = iterator.next() as List<RouteEdge>

                assertEquals(10, newEdges.size)
                assertEquals(3, removeEdges.size)

                assertEquals(LatLng(-2.0, 1.0), newEdges[0].start!!.toLatLng())
                assertEquals(LatLng(-2.0, 0.0), newEdges[0].end!!.toLatLng())

                assertEquals(LatLng(-2.0, 0.0), newEdges[1].start!!.toLatLng())
                assertEquals(LatLng(-2.0, -1.0), newEdges[1].end!!.toLatLng())

                assertEquals(LatLng(-2.0, 0.0), newEdges[2].start!!.toLatLng())
                assertEquals(LatLng(-1.0, 0.0), newEdges[2].end!!.toLatLng())

                assertEquals(LatLng(0.0, 1.0), newEdges[3].start!!.toLatLng())
                assertEquals(LatLng(0.0, 0.0), newEdges[3].end!!.toLatLng())

                assertEquals(LatLng(0.0, 0.0), newEdges[4].start!!.toLatLng())
                assertEquals(LatLng(0.0, -1.0), newEdges[4].end!!.toLatLng())

                assertEquals(LatLng(-1.0, 0.0), newEdges[5].start!!.toLatLng())
                assertEquals(LatLng(0.0, 0.0), newEdges[5].end!!.toLatLng())

                assertEquals(LatLng(0.0, 0.0), newEdges[6].start!!.toLatLng())
                assertEquals(LatLng(1.0, 0.0), newEdges[6].end!!.toLatLng())

                assertEquals(LatLng(1.0, 0.0), newEdges[7].start!!.toLatLng())
                assertEquals(LatLng(2.0, 0.0), newEdges[7].end!!.toLatLng())

                assertEquals(LatLng(2.0, 1.0), newEdges[8].start!!.toLatLng())
                assertEquals(LatLng(2.0, 0.0), newEdges[8].end!!.toLatLng())

                assertEquals(LatLng(2.0, 0.0), newEdges[9].start!!.toLatLng())
                assertEquals(LatLng(2.0, -1.0), newEdges[9].end!!.toLatLng())


                assertEquals(newEdges[0].end, newEdges[1].start)
                assertEquals(newEdges[0].end, newEdges[2].start)

                assertEquals(newEdges[2].end, newEdges[5].start)

                assertEquals(newEdges[3].end, newEdges[4].start)
                assertEquals(newEdges[3].end, newEdges[5].end)
                assertEquals(newEdges[3].end, newEdges[6].start)

                assertEquals(newEdges[6].end, newEdges[7].start)

                assertEquals(newEdges[7].end, newEdges[8].end)
                assertEquals(newEdges[7].end, newEdges[9].start)


                assertEquals(RouteMapHelper.edges[0], removeEdges[0])
                assertEquals(RouteMapHelper.edges[2], removeEdges[1])
                assertEquals(RouteMapHelper.edges[4], removeEdges[2])

                Observable(true)
            }
            mock
        }

        addLine(
            start = Pair(LatLng(-2.0, 0.0), null),
            end = Pair(LatLng(2.0, 0.0), null)
        ).observeOnce { assert(it.value) }.then.postValue(false)

        Database.currentDatabase = FirestoreDatabaseProvider
    }
}