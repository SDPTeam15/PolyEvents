package com.github.sdpteam15.polyevents.map

import android.graphics.Color
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.*
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.clearSelectedZone
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.setSelectedZoneFromArea
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.setSelectedZones
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
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.mockito.Mockito.`when` as When

private const val lat = 42.52010210373032
private const val lng = 8.566237434744834
private const val zoom = 18f
private const val minZoom = 17f
private const val maxZoom = 21f
private const val areaName = "Gunter"
private const val areaId = 1234

//TODO : Refactor file to have less lines
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
        When(mockedMap.setMinZoomPreference(GoogleMapOptions.minZoom)).then {}
    }

    @Test
    fun saveCameraTest() {
        GoogleMapOptions.saveCamera()
        assertEquals(lat, GoogleMapHelper.map!!.cameraPosition!!.target.latitude)
        assertEquals(lng, GoogleMapHelper.map!!.cameraPosition!!.target.longitude)
    }

    @Test
    fun restoreCameraStateTest() {
        GoogleMapOptions.cameraPosition = LatLng(lat, lng)
        GoogleMapOptions.cameraZoom = zoom


        val mockedwesh = Mockito.mock(IObjectWrapper::class.java)

        mockedF = Mockito.mock(ICameraUpdateFactoryDelegate::class.java)

        CameraUpdateFactory.zza(mockedF)
        When(
            mockedF.newLatLngZoom(
                GoogleMapOptions.cameraPosition,
                GoogleMapOptions.cameraZoom
            )
        ).thenReturn(mockedwesh)

        println("${GoogleMapOptions.cameraPosition}")

        GoogleMapOptions.restoreCameraState()
        assertEquals(lat, GoogleMapHelper.map!!.cameraPosition!!.target.latitude)
        assertEquals(lng, GoogleMapHelper.map!!.cameraPosition!!.target.longitude)
    }

    @Test
    fun setBounds() {
        GoogleMapOptions.swBound = LatLng(lat, lng)
        GoogleMapOptions.neBound = LatLng(lat, lng)

        assertEquals(lat, GoogleMapOptions.swBound.latitude)
        assertEquals(lng, GoogleMapOptions.swBound.longitude)
        assertEquals(lat, GoogleMapOptions.neBound.latitude)
        assertEquals(lng, GoogleMapOptions.neBound.longitude)
    }

    @Test
    fun setMinAndMaxZoom() {
        var zoomMin = false
        var zoomMax = false
        When(mockedMap.setMaxZoomPreference(GoogleMapOptions.maxZoom)).then {
            zoomMax = true
            Unit
        }
        When(mockedMap.setMinZoomPreference(GoogleMapOptions.minZoom)).then {
            zoomMin = true
            Unit
        }
        GoogleMapOptions.setMinAndMaxZoom()

        assertTrue(zoomMax)
        assertTrue(zoomMin)
    }

    @Test
    fun setBoundariesTest() {
        var bound = false
        When(
            GoogleMapHelper.map!!.setLatLngBoundsForCameraTarget(
                LatLngBounds(
                    GoogleMapOptions.swBound,
                    GoogleMapOptions.neBound
                )
            )
        ).then {
            bound = true
            Unit
        }
        GoogleMapOptions.setBoundaries()

        assertTrue(bound)
    }

    @Test
    fun clearTempTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val mockedzzw = Mockito.mock(zzw::class.java)
        ZoneAreaMapHelper.moveRightMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveDownMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveDiagMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.rotationMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.tempPoly = Polygon(mockedzzw)
        ZoneAreaMapHelper.tempLatLng.add(0, LatLng(lat, lng))

        ZoneAreaMapHelper.clearTemp()

        assertEquals(null, ZoneAreaMapHelper.tempPoly)
        assertEquals(null, ZoneAreaMapHelper.moveRightPos)
        assertEquals(null, ZoneAreaMapHelper.moveDownPos)
        assertEquals(null, ZoneAreaMapHelper.moveDiagPos)
        assertEquals(null, ZoneAreaMapHelper.movePos)
        assertEquals(null, ZoneAreaMapHelper.rotationPos)
        assertEquals(null, ZoneAreaMapHelper.tempPoly)
        assertEquals(null, ZoneAreaMapHelper.moveRightMarker)
        assertEquals(null, ZoneAreaMapHelper.moveDownMarker)
        assertEquals(null, ZoneAreaMapHelper.moveDiagMarker)
        assertEquals(null, ZoneAreaMapHelper.moveMarker)
        assertEquals(null, ZoneAreaMapHelper.rotationMarker)
        assertTrue(ZoneAreaMapHelper.tempLatLng.isEmpty())

        ZoneAreaMapHelper.clearTemp()

        assertEquals(null, ZoneAreaMapHelper.tempPoly)
        assertEquals(null, ZoneAreaMapHelper.moveRightPos)
        assertEquals(null, ZoneAreaMapHelper.moveDownPos)
        assertEquals(null, ZoneAreaMapHelper.moveDiagPos)
        assertEquals(null, ZoneAreaMapHelper.movePos)
        assertEquals(null, ZoneAreaMapHelper.rotationPos)
        assertEquals(null, ZoneAreaMapHelper.tempPoly)
        assertEquals(null, ZoneAreaMapHelper.moveRightMarker)
        assertEquals(null, ZoneAreaMapHelper.moveDownMarker)
        assertEquals(null, ZoneAreaMapHelper.moveDiagMarker)
        assertEquals(null, ZoneAreaMapHelper.moveMarker)
        assertEquals(null, ZoneAreaMapHelper.rotationMarker)
        assertTrue(ZoneAreaMapHelper.tempLatLng.isEmpty())
    }

    @Test
    fun newMarkerTest() {
        val anchor = IconAnchor(0f, 0f)
        val bound = IconBound(0, 0, 100, 100)
        val dimension = IconDimension(100, 100)
        GoogleMapHelperFunctions.newMarker(
            null,
            LatLng(lat, lng),
            anchor,
            null,
            null,
            true,
            R.id.ic_settings,
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

        ZoneAreaMapHelper.moveMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveDiagMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveRightMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveDownMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.rotationMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.movePos = position
        ZoneAreaMapHelper.moveDiagPos = position
        ZoneAreaMapHelper.moveRightPos = position
        ZoneAreaMapHelper.moveDownPos = position
        ZoneAreaMapHelper.rotationPos = position

        ZoneAreaMapHelper.tempLatLng.add(position)
        ZoneAreaMapHelper.tempLatLng.add(position)

        ZoneAreaMapHelper.translatePolygon(Marker(mockedzzt2))
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

        ZoneAreaMapHelper.moveMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveDiagMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveRightMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.moveDownMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.rotationMarker = Marker(mockedzzt)
        ZoneAreaMapHelper.movePos = position
        ZoneAreaMapHelper.moveDiagPos = position
        ZoneAreaMapHelper.moveRightPos = position
        ZoneAreaMapHelper.moveDownPos = position
        ZoneAreaMapHelper.rotationPos = position
        ZoneAreaMapHelper.tempLatLng.add(position)
        ZoneAreaMapHelper.tempLatLng.add(position)
        ZoneAreaMapHelper.tempLatLng.add(position)
        ZoneAreaMapHelper.tempLatLng.add(position)

        ZoneAreaMapHelper.transformPolygon(Marker(mockedzzt1))
        ZoneAreaMapHelper.transformPolygon(Marker(mockedzzt2))
        ZoneAreaMapHelper.transformPolygon(Marker(mockedzzt3))
    }

    //Dependent on addArea
    @Test
    fun saveNewAreaTest() {
        ZoneAreaMapHelper.tempPoly = null
        ZoneAreaMapHelper.saveNewArea(null)
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)

        val uidZone = "Zone ${GoogleMapHelper.uidZone++}"
        ZoneAreaMapHelper.editingZone = uidZone
        ZoneAreaMapHelper.zonesToArea[uidZone] = Pair(null, mutableListOf())
        val list = mutableListOf<LatLng>()
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        val mockedzzw = Mockito.mock(zzw::class.java)
        When(mockedzzw.points).thenReturn(list)
        val p = Polygon(mockedzzw)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        ZoneAreaMapHelper.tempPoly = p

        ZoneAreaMapHelper.saveNewArea(null)

        assertTrue(ZoneAreaMapHelper.areasPoints.isNotEmpty())
        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.clearTemp()

        val title = "TITLE"
        ZoneAreaMapHelper.tempTitle = title

        val mockedzzt2 = Mockito.mock(zzt::class.java)
        val m2 = Marker(mockedzzt2)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m2)

        val uidZone2 = "Zone ${GoogleMapHelper.uidZone++}"
        ZoneAreaMapHelper.editingZone = uidZone2
        ZoneAreaMapHelper.zonesToArea[uidZone2] = Pair(null, mutableListOf())
        val list2 = mutableListOf<LatLng>()
        list2.add(LatLng(lng, lat))
        list2.add(LatLng(lng, lat))
        list2.add(LatLng(lng, lat))
        list2.add(LatLng(lng, lat))
        val mockedzzw2 = Mockito.mock(zzw::class.java)
        val p2 = Polygon(mockedzzw2)
        When(mockedzzw2.points).thenReturn(list2)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p2)
        ZoneAreaMapHelper.tempPoly = p2
        assertTrue(ZoneAreaMapHelper.areasPoints.isEmpty())
        assertNotNull(ZoneAreaMapHelper.tempPoly)
        ZoneAreaMapHelper.saveNewArea(null)
        assertTrue(ZoneAreaMapHelper.areasPoints.isNotEmpty())

        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.clearTemp()
    }

    @Test
    fun addAreaTest() {
        val mockedzzw = Mockito.mock(zzw::class.java)

        val list: MutableList<LatLng> = mutableListOf()
        ZoneAreaMapHelper.addArea(null, areaId, Pair(list, null), areaName)
        assertTrue(ZoneAreaMapHelper.areasPoints.isEmpty())

        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))

        val poly = PolygonOptions()
        poly.addAll(list).clickable(true)
        When(mockedMap.addPolygon(poly)).thenReturn(Polygon(mockedzzw))
        ZoneAreaMapHelper.addArea(null, areaId, Pair(list, null), areaName)
        assertTrue(ZoneAreaMapHelper.areasPoints.isNotEmpty())

        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.clearTemp()
    }


    @Test
    fun restoreMapState() {
        ZoneAreaMapHelper.zonesToArea.clear()
        ZoneAreaMapHelper.areasPoints.clear()
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
        val newZone = Zone()
        newZone.zoneName = "ZONE NAME"
        newZone.description = "desc"
        newZone.zoneId = "Zone ${GoogleMapHelper.uidZone++}"
        val id = newZone.zoneId
        newZone.location = "46.52028185590883|6.565878853201865!46.51992327436586|6.565878853201865!46.51992327436586|6.566596016287803!46.52028185590883|6.566596016287803"
        ZoneAreaMapHelper.waitingZones.add(newZone)
        GoogleMapHelper.restoreMapState(null, false)
        assertTrue(ZoneAreaMapHelper.areasPoints.isNotEmpty())
        assertEquals(newZone.hashCode(), ZoneAreaMapHelper.zonesToArea[id]!!.first.hashCode())
        //To test the second part, we need to find how to mock map.addMarker(any) and map.addPolygon(any)
        GoogleMapHelper.restoreMapState(null, false)

        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.clearTemp()
    }
    /**/



    @Test
    fun createNewAreaTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        When(mockedzzt.position).thenReturn(position)

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        ZoneAreaMapHelper.createNewArea(null)
        assertNotNull(ZoneAreaMapHelper.tempPoly)

        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.clearTemp()
    }

    @Test
    fun editModeTest() {
        val key1 = GoogleMapHelper.uidArea
        val title = "Title"
        ZoneAreaMapHelper.editingZone = "Zone ${GoogleMapHelper.uidZone++}"
        val zoneId = ZoneAreaMapHelper.editingZone
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
        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.areasPoints[key1] = Triple(zoneId!!, m, p)
        ZoneAreaMapHelper.zonesToArea.clear()
        ZoneAreaMapHelper.zonesToArea[zoneId] = Pair(null, mutableListOf(key1))
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        ZoneAreaMapHelper.tempValues.clear()

        ZoneAreaMapHelper.editMode(null)
        assertEquals(true, ZoneAreaMapHelper.editMode)
        assertTrue(ZoneAreaMapHelper.tempValues.isNotEmpty())
        ZoneAreaMapHelper.editMode(null)
        assertEquals(false, ZoneAreaMapHelper.editMode)
    }

    @Test
    fun editAreaTest() {
        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.tempTitle = null
        val key1 = 1
        val title = "Title"
        val zoneId = "Zone ${GoogleMapHelper.uidZone++}"
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
        ZoneAreaMapHelper.areasPoints[key1] = Triple(zoneId, m, p)
        ZoneAreaMapHelper.tempValues[key1] = Pair(title, position)

        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)

        val fakeKey = 5
        ZoneAreaMapHelper.editMode = true
        ZoneAreaMapHelper.editArea(null, fakeKey.toString())
        assertEquals(true, ZoneAreaMapHelper.editMode)

        ZoneAreaMapHelper.editArea(null, key1.toString())
        assertEquals(title, ZoneAreaMapHelper.tempTitle)

    }

    @Test
    fun setUpMapTest() {
        val title = "Title"
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
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)


        GoogleMapHelper.uidZone = 0
        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.zonesToArea.clear()
        ZoneAreaMapHelper.editingZone = null
        GoogleMapOptions.setUpMap(null, false)
    }

    @Test
    fun globalVariableTest() {
        ZoneAreaMapHelper.tempLatLng = mutableListOf()
        assertTrue(ZoneAreaMapHelper.tempLatLng.isEmpty())

        ZoneAreaMapHelper.rotationPos = position
        assertEquals(position, ZoneAreaMapHelper.rotationPos)

        GoogleMapOptions.minZoom = minZoom
        GoogleMapOptions.maxZoom = maxZoom
        assertEquals(minZoom, GoogleMapOptions.minZoom)
        assertEquals(maxZoom, GoogleMapOptions.maxZoom)

        val nextUidZone = 5
        GoogleMapHelper.uidZone = 5
        assertEquals(nextUidZone, GoogleMapHelper.uidZone)
        val nextUidArea = 5
        GoogleMapHelper.uidArea = 5
        assertEquals(nextUidArea, GoogleMapHelper.uidArea)

        val modifyingArea = 10
        ZoneAreaMapHelper.modifyingArea = modifyingArea
        assertEquals(modifyingArea, ZoneAreaMapHelper.modifyingArea)
    }

    @Test
    fun canEditTest(){
        val zone = "Zone ${GoogleMapHelper.uidZone++}"
        ZoneAreaMapHelper.editingZone = zone
        val area1 = 1
        val area2 = 2
        val area3 = 3
        val areaNotModifiable = 50
        val list = mutableListOf(area1,area2,area3)
        ZoneAreaMapHelper.zonesToArea[zone] = Pair(null, list)

        assertEquals(true, ZoneAreaMapHelper.canEdit(area1.toString()))
        assertEquals(false, ZoneAreaMapHelper.canEdit(areaNotModifiable.toString()))
    }

    @Test
    fun clearSelectedZoneTest(){
        GoogleMapHelper.selectedZone = null
        clearSelectedZone()
        assertEquals(null, GoogleMapHelper.selectedZone)
        val selected = "Zone ${GoogleMapHelper.uidZone++}"
        GoogleMapHelper.selectedZone = selected
        val elem1 = GoogleMapHelper.uidArea++
        val list = mutableListOf(elem1)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.strokeColor).thenReturn(Color.BLACK)

        ZoneAreaMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        ZoneAreaMapHelper.zonesToArea[selected] = Pair(null, list)
        clearSelectedZone()
        assertEquals(null, GoogleMapHelper.selectedZone)
    }

    @Test
    fun setSelectedZoneTest(){
        val selected = "Zone ${GoogleMapHelper.uidZone++}"
        GoogleMapHelper.selectedZone = null
        val elem1 = GoogleMapHelper.uidArea++
        val list = mutableListOf(elem1)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.strokeColor).thenReturn(Color.BLACK)

        ZoneAreaMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        ZoneAreaMapHelper.zonesToArea[selected] = Pair(null, list)
        setSelectedZones(selected)
        assertEquals(selected, GoogleMapHelper.selectedZone)
    }

    @Test
    fun setSelectedZoneFromAreaTest(){
        val selected = "Zone ${GoogleMapHelper.uidZone++}"
        GoogleMapHelper.selectedZone = null
        val elem1 = GoogleMapHelper.uidArea++
        val list = mutableListOf(elem1)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.strokeColor).thenReturn(Color.BLACK)

        ZoneAreaMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        ZoneAreaMapHelper.zonesToArea[selected] = Pair(null, list)
        setSelectedZoneFromArea(elem1.toString())
        assertEquals(selected, GoogleMapHelper.selectedZone)
    }

    @Test
    fun removeAreaNotInListTest(){
        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.removeArea(GoogleMapHelper.uidArea++)
    }

    @Test
    fun importNewZoneTest(){
        ZoneAreaMapHelper.areasPoints.clear()
        ZoneAreaMapHelper.zonesToArea.clear()
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)

        When(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        When(mockedMap.addPolygon(anyOrNull())).thenReturn(p)

        val newZone = Zone()
        newZone.zoneName = "ZONE NAME"
        newZone.description = "desc"
        newZone.zoneId = "Zone ${GoogleMapHelper.uidZone++}"
        val id = newZone.zoneId
        newZone.location = "46.52028185590883|6.565878853201865!46.51992327436586|6.565878853201865!46.51992327436586|6.566596016287803!46.52028185590883|6.566596016287803"
        ZoneAreaMapHelper.importNewZone(null, newZone, false)

        assertEquals(newZone.hashCode(), ZoneAreaMapHelper.zonesToArea[id]!!.first.hashCode())
        ZoneAreaMapHelper.importNewZone(null, newZone, false)
        assertEquals(newZone.hashCode(), ZoneAreaMapHelper.zonesToArea[id]!!.first.hashCode())
    }
}