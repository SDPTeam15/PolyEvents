package com.github.sdpteam15.polyevents.helper

import android.graphics.Color
import com.github.sdpteam15.polyevents.R
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzw
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate
import com.google.android.gms.maps.model.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.mockito.Mockito.`when` as When
import org.mockito.kotlin.anyOrNull

private const val lat = 42.52010210373032
private const val lng = 8.566237434744834
private const val zoom = 18f
private const val minZoom = 17f
private const val maxZoom = 21f
private const val areaName = "Gunter"
private const val areaId = 1234

class GoogleMapHelperTest {
    lateinit var mockedMap: MapsInterface
    lateinit var mockedF: ICameraUpdateFactoryDelegate
    var position = LatLng(lat, lng)
    var camera = CameraPosition(position, zoom, 0f, 0f)
    lateinit var camUpdate: CameraUpdate

    @Before
    fun setup() {
        mockedMap = Mockito.mock(MapsInterface::class.java)
        GoogleMapHelper.map = mockedMap
        When(mockedMap.cameraPosition).thenReturn(camera)
        When(mockedMap.setMinZoomPreference(GoogleMapHelper.minZoom)).then {}
    }

    @Test
    fun saveCameraTest() {
        GoogleMapHelper.saveCamera()
        assertEquals(lat, GoogleMapHelper.map!!.cameraPosition!!.target.latitude)
        assertEquals(lng, GoogleMapHelper.map!!.cameraPosition!!.target.longitude)
    }

    @Test
    fun restoreCameraStateTest() {
        GoogleMapHelper.cameraPosition = LatLng(lat, lng)
        GoogleMapHelper.cameraZoom = zoom


        val mockedwesh = Mockito.mock(IObjectWrapper::class.java)

        mockedF = Mockito.mock(ICameraUpdateFactoryDelegate::class.java)

        CameraUpdateFactory.zza(mockedF)
        When(
            mockedF.newLatLngZoom(
                GoogleMapHelper.cameraPosition,
                GoogleMapHelper.cameraZoom
            )
        ).thenReturn(mockedwesh)

        println("${GoogleMapHelper.cameraPosition}")

        GoogleMapHelper.restoreCameraState()
        assertEquals(lat, GoogleMapHelper.map!!.cameraPosition!!.target.latitude)
        assertEquals(lng, GoogleMapHelper.map!!.cameraPosition!!.target.longitude)
    }

    @Test
    fun setBounds() {
        GoogleMapHelper.swBound = LatLng(lat, lng)
        GoogleMapHelper.neBound = LatLng(lat, lng)

        assertEquals(lat, GoogleMapHelper.swBound.latitude)
        assertEquals(lng, GoogleMapHelper.swBound.longitude)
        assertEquals(lat, GoogleMapHelper.neBound.latitude)
        assertEquals(lng, GoogleMapHelper.neBound.longitude)
    }

    @Test
    fun setMinAndMaxZoom() {
        var zoomMin = false
        var zoomMax = false
        When(mockedMap.setMaxZoomPreference(GoogleMapHelper.maxZoom)).then {
            zoomMax = true
            Unit
        }
        When(mockedMap.setMinZoomPreference(GoogleMapHelper.minZoom)).then {
            zoomMin = true
            Unit
        }
        GoogleMapHelper.setMinAndMaxZoom()

        assertTrue(zoomMax)
        assertTrue(zoomMin)
    }

    @Test
    fun setBoundariesTest() {
        var bound = false
        When(
            GoogleMapHelper.map!!.setLatLngBoundsForCameraTarget(
                LatLngBounds(
                    GoogleMapHelper.swBound,
                    GoogleMapHelper.neBound
                )
            )
        ).then {
            bound = true
            Unit
        }
        GoogleMapHelper.setBoundaries()

        assertTrue(bound)
    }

    @Test
    fun clearTempTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val mockedzzw = Mockito.mock(zzw::class.java)
        GoogleMapHelper.moveRightMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDownMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDiagMarker = Marker(mockedzzt)
        GoogleMapHelper.moveMarker = Marker(mockedzzt)
        GoogleMapHelper.rotationMarker = Marker(mockedzzt)
        GoogleMapHelper.tempPoly = Polygon(mockedzzw)
        GoogleMapHelper.tempLatLng.add(0, LatLng(lat, lng))

        GoogleMapHelper.clearTemp()

        assertEquals(null, GoogleMapHelper.tempPoly)
        assertEquals(null, GoogleMapHelper.moveRightPos)
        assertEquals(null, GoogleMapHelper.moveDownPos)
        assertEquals(null, GoogleMapHelper.moveDiagPos)
        assertEquals(null, GoogleMapHelper.movePos)
        assertEquals(null, GoogleMapHelper.rotationPos)
        assertEquals(null, GoogleMapHelper.tempPoly)
        assertEquals(null, GoogleMapHelper.moveRightMarker)
        assertEquals(null, GoogleMapHelper.moveDownMarker)
        assertEquals(null, GoogleMapHelper.moveDiagMarker)
        assertEquals(null, GoogleMapHelper.moveMarker)
        assertEquals(null, GoogleMapHelper.rotationMarker)
        assertTrue(GoogleMapHelper.tempLatLng.isEmpty())

        GoogleMapHelper.clearTemp()

        assertEquals(null, GoogleMapHelper.tempPoly)
        assertEquals(null, GoogleMapHelper.moveRightPos)
        assertEquals(null, GoogleMapHelper.moveDownPos)
        assertEquals(null, GoogleMapHelper.moveDiagPos)
        assertEquals(null, GoogleMapHelper.movePos)
        assertEquals(null, GoogleMapHelper.rotationPos)
        assertEquals(null, GoogleMapHelper.tempPoly)
        assertEquals(null, GoogleMapHelper.moveRightMarker)
        assertEquals(null, GoogleMapHelper.moveDownMarker)
        assertEquals(null, GoogleMapHelper.moveDiagMarker)
        assertEquals(null, GoogleMapHelper.moveMarker)
        assertEquals(null, GoogleMapHelper.rotationMarker)
        assertTrue(GoogleMapHelper.tempLatLng.isEmpty())
    }

    @Test
    fun newMarkerTest() {
        val anchor = IconAnchor(0f, 0f)
        val bound = IconBound(0, 0, 100, 100)
        val dimension = IconDimension(100, 100)
        GoogleMapHelper.newMarker(
            null,
            LatLng(lat, lng),
            anchor,
            null,
            null,
            true,
            R.id.ic_more,
            bound,
            dimension
        )
    }

    @Test
    fun translatePolygonTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val mockedzzt2 = Mockito.mock(zzt::class.java)

        val newlat = 40.52010210373032
        val newlng = 10.566237434744834

        val newPos = LatLng(newlat, newlng)

        When(mockedzzt.position).thenReturn(position)
        When(mockedzzt2.position).thenReturn(newPos)

        GoogleMapHelper.moveMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDiagMarker = Marker(mockedzzt)
        GoogleMapHelper.moveRightMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDownMarker = Marker(mockedzzt)
        GoogleMapHelper.rotationMarker = Marker(mockedzzt)
        GoogleMapHelper.movePos = position
        GoogleMapHelper.moveDiagPos = position
        GoogleMapHelper.moveRightPos = position
        GoogleMapHelper.moveDownPos = position
        GoogleMapHelper.rotationPos = position

        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)

        GoogleMapHelper.translatePolygon(Marker(mockedzzt2))
    }

    @Test
    fun transformPolygonTest() {
        val newlat = 40.52010210373032
        val newlng = 10.566237434744834

        val newPos = LatLng(newlat, newlng)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val mockedzzt1 = Mockito.mock(zzt::class.java)
        val mockedzzt2 = Mockito.mock(zzt::class.java)
        val mockedzzt3 = Mockito.mock(zzt::class.java)

        When(mockedzzt1.snippet).thenReturn(PolygonAction.RIGHT.toString())
        When(mockedzzt2.snippet).thenReturn(PolygonAction.DOWN.toString())
        When(mockedzzt3.snippet).thenReturn(PolygonAction.DIAG.toString())

        When(mockedzzt.position).thenReturn(position)
        When(mockedzzt1.position).thenReturn(newPos)
        When(mockedzzt2.position).thenReturn(newPos)
        When(mockedzzt3.position).thenReturn(newPos)

        GoogleMapHelper.moveMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDiagMarker = Marker(mockedzzt)
        GoogleMapHelper.moveRightMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDownMarker = Marker(mockedzzt)
        GoogleMapHelper.rotationMarker = Marker(mockedzzt)
        GoogleMapHelper.movePos = position
        GoogleMapHelper.moveDiagPos = position
        GoogleMapHelper.moveRightPos = position
        GoogleMapHelper.moveDownPos = position
        GoogleMapHelper.rotationPos = position
        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)

        GoogleMapHelper.transformPolygon(Marker(mockedzzt1))
        GoogleMapHelper.transformPolygon(Marker(mockedzzt2))
        GoogleMapHelper.transformPolygon(Marker(mockedzzt3))
    }

    //Dependent on addArea
    @Test
    fun saveNewAreaTest() {
        GoogleMapHelper.tempPoly = null
        GoogleMapHelper.saveNewArea(null)
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)

        val uidZone = 5
        GoogleMapHelper.editingZone = uidZone
        GoogleMapHelper.zonesToArea[uidZone] = Pair(null, mutableListOf())
        val list = mutableListOf<LatLng>()
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        val mockedzzw = Mockito.mock(zzw::class.java)
        When(mockedzzw.points).thenReturn(list)
        val p = Polygon(mockedzzw)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        GoogleMapHelper.tempPoly = p

        GoogleMapHelper.saveNewArea(null)

        assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()

        val title = "TITLE"
        GoogleMapHelper.tempTitle = title

        val mockedzzt2 = Mockito.mock(zzt::class.java)
        val m2 = Marker(mockedzzt)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m2)

        val uidZone2 = 6
        GoogleMapHelper.editingZone = uidZone2
        GoogleMapHelper.zonesToArea[uidZone2] = Pair(null, mutableListOf())
        val list2 = mutableListOf<LatLng>()
        list2.add(LatLng(lng, lat))
        list2.add(LatLng(lng, lat))
        list2.add(LatLng(lng, lat))
        list2.add(LatLng(lng, lat))
        val mockedzzw2 = Mockito.mock(zzw::class.java)
        val p2 = Polygon(mockedzzw2)
        When(mockedzzw2.points).thenReturn(list2)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p2)
        GoogleMapHelper.tempPoly = p2
        assertTrue(GoogleMapHelper.areasPoints.isEmpty())
        assertNotNull(GoogleMapHelper.tempPoly)
        GoogleMapHelper.saveNewArea(null)
        assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }

    @Test
    fun addAreaTest() {
        val mockedzzw = Mockito.mock(zzw::class.java)

        val list: MutableList<LatLng> = mutableListOf()
        GoogleMapHelper.addArea(null, areaId, list, areaName)
        assertTrue(GoogleMapHelper.areasPoints.isEmpty())

        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))

        val poly = PolygonOptions()
        poly.addAll(list).clickable(true)
        When(mockedMap.addPolygon(poly)).thenReturn(Polygon(mockedzzw))
        GoogleMapHelper.addArea(null, areaId, list, areaName)
        assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }

    @Test
    fun restoreMapState() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val title = "Event x"
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        When(mockedzzt.title).thenReturn(title)

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        val list: MutableList<LatLng> = mutableListOf()
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        When(mockedzzw.points).thenReturn(list)

        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)

        GoogleMapHelper.restoreMapState(null)
        assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())
        //To test the second part, we need to find how to mock map.addMarker(any) and map.addPolygon(any)
        GoogleMapHelper.restoreMapState(null)

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }

    //Depends on setupEditZone or create the edit zone markers by hand
    @Test
    fun interactionMarkerTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        assertEquals(m.hashCode(), mockedMap.addMarker(MarkerOptions()).hashCode())
        When(mockedzzt.snippet).thenReturn(PolygonAction.DIAG.toString())
        assertNotNull(position)
        When(mockedzzt.position).thenReturn(position)
        assertNotNull(mockedzzt.position)
        assertNotNull(mockedMap.addMarker(MarkerOptions()).position)
        GoogleMapHelper.setupEditZone(null, LatLng(lat, lng))

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.points).thenReturn(listOf())

        GoogleMapHelper.interactionMarker(m)

        When(mockedzzt.snippet).thenReturn(PolygonAction.DOWN.toString())
        GoogleMapHelper.interactionMarker(m)

        When(mockedzzt.snippet).thenReturn(PolygonAction.MOVE.toString())
        GoogleMapHelper.interactionMarker(m)

        When(mockedzzt.snippet).thenReturn(PolygonAction.RIGHT.toString())
        GoogleMapHelper.interactionMarker(m)

        When(mockedzzt.snippet).thenReturn(PolygonAction.ROTATE.toString())
        GoogleMapHelper.interactionMarker(m)

        GoogleMapHelper.tempPoly = p
        When(mockedzzt.snippet).thenReturn("TEST")
        GoogleMapHelper.interactionMarker(m)

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }

    @Test
    fun createNewAreaTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        When(mockedzzt.position).thenReturn(position)

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        GoogleMapHelper.createNewArea(null)
        assertNotNull(GoogleMapHelper.tempPoly)

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }


    @Test
    fun restoreMarkersTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val mockedzzt2 = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val m2 = Marker(mockedzzt2)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m2)
        When(mockedzzt.position).thenReturn(position)

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        val list: MutableList<LatLng> = mutableListOf()
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        When(mockedzzw.points).thenReturn(list)

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.tempValues.clear()
        GoogleMapHelper.restoreMarkers(null)

        val key1 = 1
        val title = "Title"
        val zoneId = 0
        GoogleMapHelper.areasPoints[key1] = Triple(zoneId,m, p)
        GoogleMapHelper.tempValues[key1] = Pair(title, position)

        GoogleMapHelper.restoreMarkers(null)
        assertEquals(GoogleMapHelper.areasPoints[key1]!!.first.hashCode(), m2.hashCode())


        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }


    @Test
    fun editModeTest() {
        val key1 = GoogleMapHelper.uidArea
        val title = "Title"
        val zoneId = GoogleMapHelper.editingZone
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        When(mockedzzt.title).thenReturn(title)
        When(mockedzzt.position).thenReturn(position)

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        val list: MutableList<LatLng> = mutableListOf()
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        When(mockedzzw.points).thenReturn(list)
        When(mockedzzw.strokeColor).thenReturn(Color.BLACK)
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.areasPoints[key1] = Triple(zoneId, m, p)
        GoogleMapHelper.zonesToArea.clear()
        GoogleMapHelper.zonesToArea[zoneId] = Pair(null, mutableListOf(key1))
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        GoogleMapHelper.tempValues.clear()

        GoogleMapHelper.editMode(null)
        assertEquals(true, GoogleMapHelper.editMode)
        assertTrue(GoogleMapHelper.tempValues.isNotEmpty())
        GoogleMapHelper.editMode(null)
        assertEquals(false, GoogleMapHelper.editMode)
    }

    @Test
    fun editAreaTest() {
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.tempTitle = null
        val key1 = 1
        val title = "Title"
        val zoneId = 0
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        When(mockedzzt.title).thenReturn(title)
        When(mockedzzt.position).thenReturn(position)

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        val list: MutableList<LatLng> = mutableListOf()
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        When(mockedzzw.points).thenReturn(list)
        GoogleMapHelper.areasPoints[key1] = Triple(zoneId, m, p)
        GoogleMapHelper.tempValues[key1] = Pair(title, position)

        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)

        val fakeKey = 5
        GoogleMapHelper.editMode = true
        GoogleMapHelper.editArea(null, fakeKey.toString())
        assertEquals(true, GoogleMapHelper.editMode)

        GoogleMapHelper.editArea(null, key1.toString())
        assertEquals(title, GoogleMapHelper.tempTitle)

    }

    @Test
    fun setUpMapTest() {
        val title = "Title"
        val mockedzzt = Mockito.mock(zzt::class.java)
        var m = Marker(mockedzzt)
        When(mockedzzt.title).thenReturn(title)
        When(mockedzzt.position).thenReturn(position)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        val list: MutableList<LatLng> = mutableListOf()
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        When(mockedzzw.points).thenReturn(list)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)


        GoogleMapHelper.uidZone = 0
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.zonesToArea.clear()
        GoogleMapHelper.editingZone = -1
        GoogleMapHelper.setUpMap(null)
    }

    @Test
    fun globalVariableTest() {
        GoogleMapHelper.tempLatLng = mutableListOf()
        assertTrue(GoogleMapHelper.tempLatLng.isEmpty())

        GoogleMapHelper.rotationPos = position
        assertEquals(position, GoogleMapHelper.rotationPos)

        GoogleMapHelper.minZoom = minZoom
        GoogleMapHelper.maxZoom = maxZoom
        assertEquals(minZoom, GoogleMapHelper.minZoom)
        assertEquals(maxZoom, GoogleMapHelper.maxZoom)

        val nextUidZone = 5
        GoogleMapHelper.uidZone = 5
        assertEquals(nextUidZone, GoogleMapHelper.uidZone)
        val nextUidArea = 5
        GoogleMapHelper.uidArea = 5
        assertEquals(nextUidArea, GoogleMapHelper.uidArea)

        val modifyingArea = 10
        GoogleMapHelper.modifyingArea = modifyingArea
        assertEquals(modifyingArea, GoogleMapHelper.modifyingArea)
    }

    @Test
    fun canEditTest(){
        val zone = GoogleMapHelper.uidZone++
        GoogleMapHelper.editingZone = zone
        val area1 = 1
        val area2 = 2
        val area3 = 3
        val areaNotModifiable = 50
        val list = mutableListOf(area1,area2,area3)
        GoogleMapHelper.zonesToArea[zone] = Pair(null, list)

        assertEquals(true, GoogleMapHelper.canEdit(area1.toString()))
        assertEquals(false, GoogleMapHelper.canEdit(areaNotModifiable.toString()))
    }

    @Test
    fun clearSelectedZoneTest(){
        GoogleMapHelper.selectedZone = -1
        GoogleMapHelper.clearSelectedZone()
        assertEquals(-1, GoogleMapHelper.selectedZone)
        val selected = GoogleMapHelper.uidZone++
        GoogleMapHelper.selectedZone = selected
        val elem1 = GoogleMapHelper.uidArea++
        val list = mutableListOf(elem1)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.strokeColor).thenReturn(Color.BLACK)

        GoogleMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        GoogleMapHelper.zonesToArea[selected] = Pair(null, list)
        GoogleMapHelper.clearSelectedZone()
        assertEquals(-1, GoogleMapHelper.selectedZone)
    }

    @Test
    fun setSelectedZoneTest(){
        val selected = GoogleMapHelper.uidZone++
        GoogleMapHelper.selectedZone = -1
        val elem1 = GoogleMapHelper.uidArea++
        val list = mutableListOf(elem1)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.strokeColor).thenReturn(Color.BLACK)

        GoogleMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        GoogleMapHelper.zonesToArea[selected] = Pair(null, list)
        GoogleMapHelper.setSelectedZones(selected)
        assertEquals(selected, GoogleMapHelper.selectedZone)
    }

    @Test
    fun setSelectedZoneFromAreaTest(){
        val selected = GoogleMapHelper.uidZone++
        GoogleMapHelper.selectedZone = -1
        val elem1 = GoogleMapHelper.uidArea++
        val list = mutableListOf(elem1)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.strokeColor).thenReturn(Color.BLACK)

        GoogleMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        GoogleMapHelper.zonesToArea[selected] = Pair(null, list)
        GoogleMapHelper.setSelectedZoneFromArea(elem1.toString())
        assertEquals(selected, GoogleMapHelper.selectedZone)
    }

    @Test
    fun removeAreaNotInListTest(){
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.removeArea(GoogleMapHelper.uidArea++)
    }

    @Test
    fun removeZoneAreaNotInListTest(){
        GoogleMapHelper.zonesToArea.clear()
        GoogleMapHelper.removeArea(GoogleMapHelper.uidZone++)
    }
}