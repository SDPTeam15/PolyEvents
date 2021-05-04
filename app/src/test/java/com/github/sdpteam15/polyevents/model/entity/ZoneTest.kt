package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ZoneTest {
    lateinit var zone: Zone
    val zoneId = "zoneId1"
    val zoneName = "zone A"
    val location = "Esplanade"
    val description = "A simple cool zone"
    val arrayLngLat = arrayOf(4.10, 4.20, 4.30, 4.40, 4.50, 4.60, 4.70, 4.80)
    val arrayLngLat2 = arrayOf(5.10, 5.20, 5.30, 5.40, 5.50, 5.60, 5.70, 5.80)
    val allCoords: MutableList<MutableList<LatLng>> = ArrayList()
    lateinit var list1: MutableList<LatLng>
    lateinit var list2: MutableList<LatLng>

    @Before
    fun setupZone() {
        list1 = mutableListOf(
            LatLng(arrayLngLat[0], arrayLngLat[1]),
            LatLng(arrayLngLat[2], arrayLngLat[3]),
            LatLng(arrayLngLat[4], arrayLngLat[5]),
            LatLng(arrayLngLat[6], arrayLngLat[7])
        )
        list2 = mutableListOf(
            LatLng(arrayLngLat2[0], arrayLngLat2[1]),
            LatLng(arrayLngLat2[2], arrayLngLat2[3]),
            LatLng(arrayLngLat2[4], arrayLngLat2[5]),
            LatLng(arrayLngLat2[6], arrayLngLat2[7])
        )
        allCoords.add(list1)
        allCoords.add(list2)
        zone = Zone(zoneId, zoneName, location, description)
    }

    @Test
    fun latitudeLongitudeConversionDoneProperly() {
        var s = ""

        for (c in list1) {
            s += c.latitude.toString() + LAT_LONG_SEP.value + c.longitude + POINTS_SEP.value
        }
        s = s.substring(0, s.length - POINTS_SEP.value.length) + AREAS_SEP.value
        for (c in list2) {
            s += c.latitude.toString() + LAT_LONG_SEP.value + c.longitude + POINTS_SEP.value
        }
        s = s.substring(0, s.length - POINTS_SEP.value.length)
        zone.location = s

        val fromZone = zone.getZoneCoordinates()
        assert(allCoords.size == fromZone.size)
        for (i in 0 until allCoords.size) {
            val valid = allCoords[i]
            val test = fromZone[i]
            assert(valid.size == test.size)
            for (j in 0 until valid.size) {
                assert(valid[j] == test[j])
            }
        }
    }

    @Test
    fun getZoneCoordinatesReturnEmptyListOnNull() {
        zone.location = null
        assert(zone.getZoneCoordinates().size == 0)
        assert(zone.getZoneCoordinates().isEmpty())
    }

    @Test
    fun testZoneProperties() {
        assertEquals(zone.zoneName, zoneName)
        assertEquals(zone.location, location)
        assertEquals(zone.zoneId, zoneId)
        assertEquals(zone.description, description)
    }
}