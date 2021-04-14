package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.github.sdpteam15.polyevents.helper.MapsInterface
import com.github.sdpteam15.polyevents.helper.PolygonAction
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzw
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate
import com.google.android.gms.maps.model.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.any
import java.io.File
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.mockito.Mockito.`when` as When

private const val lat = 42.52010210373032
private const val lng = 8.566237434744834
private const val zoom = 18f
private const val areaName = "Gunter"
private const val areaId = "1234"

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
        val dir = File("./src/main/res/drawable")
        val file: Array<File> = dir.listFiles()
        //Drawable.createFromPath(dir.path + "/" + file[0])

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
        When(mockedF.newLatLngZoom(GoogleMapHelper.cameraPosition, GoogleMapHelper.cameraZoom)).thenReturn(mockedwesh)

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
        var zoomMin: Boolean = false
        var zoomMax: Boolean = false
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
        var bound: Boolean = false
        When(GoogleMapHelper.map!!.setLatLngBoundsForCameraTarget(LatLngBounds(GoogleMapHelper.swBound, GoogleMapHelper.neBound))).then {
            bound = true
            Unit
        }
        GoogleMapHelper.setBoundaries()

        assertTrue(bound)
    }

    //Cannot be tested?
    @Test
    fun setMapStyleTest() {

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
    fun newMarkerTest(){
        GoogleMapHelper.newMarker(LatLng(lat, lng), 0f,0f,null, null, true, R.id.ic_more, 0,0,100,100,100,100)
    }

    @Test
    fun translatePolygonTest(){
        val mockedzzt = Mockito.mock(zzt::class.java)
        val mockedzzt2 = Mockito.mock(zzt::class.java)

        val newlat = 40.52010210373032
        val newlng = 10.566237434744834

        val newPos = LatLng(newlat,newlng)

        When(mockedzzt.position).thenReturn(position)
        When(mockedzzt2.position).thenReturn(newPos)

        GoogleMapHelper.moveMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDiagMarker = Marker(mockedzzt)
        GoogleMapHelper.moveRightMarker = Marker(mockedzzt)
        GoogleMapHelper.moveDownMarker = Marker(mockedzzt)
        GoogleMapHelper.movePos = position
        GoogleMapHelper.moveDiagPos = position
        GoogleMapHelper.moveRightPos = position
        GoogleMapHelper.moveDownPos = position

        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)

        GoogleMapHelper.translatePolygon(Marker(mockedzzt2))

        //Cannot test the values
        //assertEquals(newlat, GoogleMapHelper.movePos!!.latitude)
        //assertEquals(newlng, GoogleMapHelper.movePos!!.longitude)
    }

    @Test
    fun transformPolygonTest(){
        val newlat = 40.52010210373032
        val newlng = 10.566237434744834

        val newPos = LatLng(newlat,newlng)

        val mockedzzt = Mockito.mock(zzt::class.java)
        val mockedzzt1 = Mockito.mock(zzt::class.java)
        val mockedzzt2 = Mockito.mock(zzt::class.java)
        val mockedzzt3= Mockito.mock(zzt::class.java)

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
        GoogleMapHelper.movePos = position
        GoogleMapHelper.moveDiagPos = position
        GoogleMapHelper.moveRightPos = position
        GoogleMapHelper.moveDownPos = position
        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)
        GoogleMapHelper.tempLatLng.add(position)

        GoogleMapHelper.transformPolygon(Marker(mockedzzt1))
        GoogleMapHelper.transformPolygon(Marker(mockedzzt2))
        GoogleMapHelper.transformPolygon(Marker(mockedzzt3))

        //Cannot test the values
        //assertEquals(newlat, GoogleMapHelper.movePos!!.latitude)
        //assertEquals(newlng, GoogleMapHelper.movePos!!.longitude)
    }
    //Dependent on addArea
    @Test
    fun saveNewAreaTest(){
        GoogleMapHelper.tempPoly = null
        GoogleMapHelper.saveNewArea()

        val list = mutableListOf<LatLng>()
        list.add(LatLng(lat,lng))
        list.add(LatLng(lat,lng))
        list.add(LatLng(lat,lng))
        list.add(LatLng(lat,lng))
        val mockedzzw = Mockito.mock(zzw::class.java)
        When(mockedzzw.points).thenReturn(list)
        GoogleMapHelper.tempPoly = Polygon(mockedzzw)

        GoogleMapHelper.saveNewArea()
        GoogleMapHelper.areasPoints.clear()

    }

    @Test
    fun addAreaTest(){
        val mockedzzw = Mockito.mock(zzw::class.java)

        val list:MutableList<LatLng> = mutableListOf()
        GoogleMapHelper.addArea(areaName,list,areaId)
        assertTrue(GoogleMapHelper.areasPoints.isEmpty())

        list.add(LatLng(lat,lng))
        list.add(LatLng(lat,lng))
        list.add(LatLng(lat,lng))
        list.add(LatLng(lat,lng))

        val poly = PolygonOptions()
        poly.addAll(list).clickable(true)
        When(mockedMap.addPolygon(poly)).thenReturn(Polygon(mockedzzw))
        GoogleMapHelper.addArea(areaName,list,areaId)
        assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())
        GoogleMapHelper.areasPoints.clear()



    }

     @Test
     fun restoreMapState(){

         GoogleMapHelper.restoreMapState()
         assertTrue(GoogleMapHelper.areasPoints.isNotEmpty())
         //To test the second part, we need to find how to mock map.addMarker(any) and map.addPolygon(any)
         //GoogleMapHelper.restoreMapState()
     }

    //Depends on setupEditZone or create the edit zone markers by hand
    @Test
    fun interactionMarkerTest() {
        GoogleMapHelper.setupEditZone(LatLng(lat, lng))
        val mockedzzt = Mockito.mock(zzt::class.java)
        When(mockedMap.addMarker(any())).thenReturn(Marker(mockedzzt))

        When(mockedzzt.snippet).thenReturn(PolygonAction.DIAG.toString())
        /*
        val mockedzzt2 = Mockito.mock(zzt::class.java)
        When(mockedzzt2.snippet).thenReturn(PolygonAction.DOWN.toString())
        val mockedzzt3 = Mockito.mock(zzt::class.java)
        When(mockedzzt3.snippet).thenReturn(PolygonAction.MOVE.toString())
        val mockedzzt4 = Mockito.mock(zzt::class.java)
        When(mockedzzt4.snippet).thenReturn(PolygonAction.RIGHT.toString())
        val mockedzzt5 = Mockito.mock(zzt::class.java)
        When(mockedzzt5.snippet).thenReturn(PolygonAction.ROTATE.toString())
        */
        var m = Marker(mockedzzt)
        /*
        var m2 = Marker(mockedzzt2)
        var m3 = Marker(mockedzzt3)
        var m4 = Marker(mockedzzt4)
        var m5 = Marker(mockedzzt5)
        */
        GoogleMapHelper.interactionMarker(m)
        /*
        GoogleMapHelper.interactionMarker(m2)
        GoogleMapHelper.interactionMarker(m3)
        GoogleMapHelper.interactionMarker(m4)
        GoogleMapHelper.interactionMarker(m5)
        */

    }






/*
    @Test
    fun setUpEditZoneTest(){
        val zoom = GoogleMapHelper.map!!.cameraPosition!!.zoom
        val divisor = 2.0.pow(zoom.toDouble())
        val longDiff = 188.0 / divisor / 2
        val latDiff = longDiff / 2
        var pos1 = LatLng(lat + latDiff, lng - longDiff)
        var pos2 = LatLng(lat - latDiff, lng - longDiff)
        var pos3 = LatLng(lat - latDiff, lng + longDiff)
        var pos4 = LatLng(lat + latDiff, lng + longDiff)

        val temp1 = (pos4.latitude + pos3.latitude) / 2
        val temp2 = (pos2.longitude + pos3.longitude) / 2
        val posMidRight = LatLng(temp1, pos4.longitude)
        val posMidDown = LatLng(pos2.latitude, temp2)
        val posCenter = LatLng(temp1, temp2)


        val mockedzzt = Mockito.mock(zzt::class.java)
        When(mockedMap.addMarker(ArgumentMatchers.any(MarkerOptions::class.java))).thenReturn(Marker(mockedzzt))
        GoogleMapHelper.setupEditZone(LatLng(lat, lng))
        assertNotNull(GoogleMapHelper.moveDiagMarker)
    }



        //Dependent on setUpEditZone
    @Test
    fun createNewAreaTest(){

    }

    */



}