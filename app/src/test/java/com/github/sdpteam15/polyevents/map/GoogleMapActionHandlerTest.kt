package com.github.sdpteam15.polyevents.map

import com.github.sdpteam15.polyevents.model.map.*
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzw
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate
import com.google.android.gms.maps.model.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private const val lat = 42.52010210373032
private const val lng = 8.566237434744834
private const val zoom = 18f
private const val minZoom = 17f
private const val maxZoom = 21f
private const val areaName = "Gunter"
private const val areaId = 1234

class GoogleMapActionHandlerTest {
    lateinit var mockedMap: MapsInterface
    lateinit var mockedF: ICameraUpdateFactoryDelegate
    var position = LatLng(lat, lng)
    var camera = CameraPosition(position, zoom, 0f, 0f)
    lateinit var camUpdate: CameraUpdate

    @Before
    fun setup() {
        mockedMap = Mockito.mock(MapsInterface::class.java)
        GoogleMapHelper.map = mockedMap
        Mockito.`when`(mockedMap.cameraPosition).thenReturn(camera)
        Mockito.`when`(mockedMap.setMinZoomPreference(GoogleMapOptions.minZoom)).then {}
    }
    //Depends on setupEditZone or create the edit zone markers by hand
    @Test
    fun interactionMarkerTest() {
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        Mockito.`when`(mockedMap.addMarker(anyOrNull())).thenReturn(m)
        assertEquals(m.hashCode(), mockedMap.addMarker(MarkerOptions()).hashCode())
        Mockito.`when`(mockedzzt.snippet).thenReturn(PolygonAction.DIAG.toString())
        assertNotNull(position)
        Mockito.`when`(mockedzzt.position).thenReturn(position)
        assertNotNull(mockedzzt.position)
        assertNotNull(mockedMap.addMarker(MarkerOptions()).position)
        GoogleMapHelper.setupEditZone(null, LatLng(lat, lng))

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        Mockito.`when`(mockedzzw.points).thenReturn(listOf())

        GoogleMapActionHandler.interactionMarkerHandler(m, MarkerDragMode.DRAG)

        Mockito.`when`(mockedzzt.snippet).thenReturn(PolygonAction.DOWN.toString())
        GoogleMapActionHandler.interactionMarkerHandler(m, MarkerDragMode.DRAG)

        Mockito.`when`(mockedzzt.snippet).thenReturn(PolygonAction.MOVE.toString())
        GoogleMapActionHandler.interactionMarkerHandler(m, MarkerDragMode.DRAG)

        Mockito.`when`(mockedzzt.snippet).thenReturn(PolygonAction.RIGHT.toString())
        GoogleMapActionHandler.interactionMarkerHandler(m, MarkerDragMode.DRAG)

        Mockito.`when`(mockedzzt.snippet).thenReturn(PolygonAction.ROTATE.toString())
        GoogleMapActionHandler.interactionMarkerHandler(m, MarkerDragMode.DRAG)

        GoogleMapHelper.tempPoly = p
        Mockito.`when`(mockedzzt.snippet).thenReturn("TEST")
        GoogleMapActionHandler.interactionMarkerHandler(m, MarkerDragMode.DRAG)

        GoogleMapHelper.areasPoints.clear()
        GoogleMapHelper.clearTemp()
    }
}