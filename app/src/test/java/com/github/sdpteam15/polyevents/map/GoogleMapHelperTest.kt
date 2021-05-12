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
        GoogleMapHelperFunctions.newMarker(
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

        val uidZone = "Zone ${GoogleMapHelper.uidZone++}"
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
        val m2 = Marker(mockedzzt2)
        When(mockedMap.addMarker(anyOrNull())).thenReturn(m2)

        val uidZone2 = "Zone ${GoogleMapHelper.uidZone++}"
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
        GoogleMapHelper.addArea(null, areaId, Pair(list, null), areaName)
        assertTrue(GoogleMapHelper.areasPoints.isEmpty())

        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))
        list.add(LatLng(lat, lng))

        val poly = PolygonOptions()
        poly.addAll(list).clickable(true)
        When(mockedMap.addPolygon(poly)).thenReturn(Polygon(mockedzzw))
        GoogleMapHelper.addArea(null, areaId, Pair(list, null), areaName)
        assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }


    @Test
    fun restoreMapState() {
        GoogleMapHelper.zonesToArea.clear()
        GoogleMapHelper.areasPoints.clear()
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
        GoogleMapHelper.waitingZones.add(newZone)
        GoogleMapHelper.restoreMapState(null, false)
        assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())
        assertEquals(newZone.hashCode(), GoogleMapHelper.zonesToArea[id]!!.first.hashCode())
        //To test the second part, we need to find how to mock map.addMarker(any) and map.addPolygon(any)
        GoogleMapHelper.restoreMapState(null, false)

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
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
        GoogleMapHelper.createNewArea(null)
        assertNotNull(GoogleMapHelper.tempPoly)

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }

    @Test
    fun editModeTest() {
        val key1 = GoogleMapHelper.uidArea
        val title = "Title"
        GoogleMapHelper.editingZone = "Zone ${GoogleMapHelper.uidZone++}"
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
        GoogleMapHelper.areasPoints[key1] = Triple(zoneId!!, m, p)
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
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.zonesToArea.clear()
        GoogleMapHelper.editingZone = null
        GoogleMapOptions.setUpMap(null, false)
    }

    @Test
    fun globalVariableTest() {
        GoogleMapHelper.tempLatLng = mutableListOf()
        assertTrue(GoogleMapHelper.tempLatLng.isEmpty())

        GoogleMapHelper.rotationPos = position
        assertEquals(position, GoogleMapHelper.rotationPos)

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
        GoogleMapHelper.modifyingArea = modifyingArea
        assertEquals(modifyingArea, GoogleMapHelper.modifyingArea)
    }

    @Test
    fun canEditTest(){
        val zone = "Zone ${GoogleMapHelper.uidZone++}"
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

        GoogleMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        GoogleMapHelper.zonesToArea[selected] = Pair(null, list)
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

        GoogleMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        GoogleMapHelper.zonesToArea[selected] = Pair(null, list)
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

        GoogleMapHelper.areasPoints[elem1] = Triple(selected, m, p)
        GoogleMapHelper.zonesToArea[selected] = Pair(null, list)
        setSelectedZoneFromArea(elem1.toString())
        assertEquals(selected, GoogleMapHelper.selectedZone)
    }

    @Test
    fun removeAreaNotInListTest(){
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.removeArea(GoogleMapHelper.uidArea++)
    }

    @Test
    fun importNewZoneTest(){
        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.zonesToArea.clear()
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
        GoogleMapHelper.importNewZone(null, newZone, false)

        assertEquals(newZone.hashCode(), GoogleMapHelper.zonesToArea[id]!!.first.hashCode())
        GoogleMapHelper.importNewZone(null, newZone, false)
        assertEquals(newZone.hashCode(), GoogleMapHelper.zonesToArea[id]!!.first.hashCode())
    }
}