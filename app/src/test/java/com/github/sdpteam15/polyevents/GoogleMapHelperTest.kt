package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.internal.maps.zzw
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import javax.annotation.meta.When

class GoogleMapHelperTest {
    lateinit var listLngLat: ArrayList<LatLng>
    lateinit var listLngLat2: ArrayList<LatLng>
    val arrayLngLat = arrayOf(4.10, 4.20, 4.30, 4.40, 4.50, 4.60, 4.70, 4.80)
    val arrayLngLat2 = arrayOf(5.10, 5.20, 5.30, 5.40, 5.50, 5.60, 5.70, 5.80)
    val map: MutableMap<Int, List<LatLng>> = mutableMapOf()
    val id1 = 0
    val id2 = 1
    val id3 = 2

    @Before
    fun setup() {
        listLngLat = ArrayList()
        listLngLat.add(LatLng(arrayLngLat[0], arrayLngLat[1]))
        listLngLat.add(LatLng(arrayLngLat[2], arrayLngLat[3]))
        listLngLat.add(LatLng(arrayLngLat[4], arrayLngLat[5]))
        listLngLat.add(LatLng(arrayLngLat[6], arrayLngLat[7]))
        listLngLat.add(LatLng(arrayLngLat[0], arrayLngLat[1]))

        listLngLat2 = ArrayList()

        listLngLat2.add(LatLng(arrayLngLat2[0], arrayLngLat2[1]))
        listLngLat2.add(LatLng(arrayLngLat2[2], arrayLngLat2[3]))
        listLngLat2.add(LatLng(arrayLngLat2[4], arrayLngLat2[5]))
        listLngLat2.add(LatLng(arrayLngLat2[6], arrayLngLat2[7]))
        listLngLat2.add(LatLng(arrayLngLat2[0], arrayLngLat2[1]))

        map[id1] = listLngLat
        map[id2] = listLngLat2
    }

    @Test
    fun areaToFormattedStringLocationReturnCorrectString() {
        var correctString = ""
        for (i in arrayLngLat.indices) {
            correctString += arrayLngLat[i].toString()
            if (i % 2 == 0)
                correctString += LAT_LONG_SEP.value
            else
                correctString += POINTS_SEP.value
        }
        correctString =
            correctString.substring(0, correctString.length - POINTS_SEP.value.length)
        assert(correctString == GoogleMapHelper.areaToFormattedStringLocation(listLngLat.dropLast(1)))
    }

    @Test
    fun areaToFormattedStringLocationReturnEmptyStringIfNullArgument() {
        assert(GoogleMapHelper.areaToFormattedStringLocation(null) == "")
    }

    @Test
    fun areasToFormattedStringLocationsReturnCorrectInformation() {
        var correctString = ""

        for (i in arrayLngLat.indices) {
            correctString += arrayLngLat[i].toString()
            if (i % 2 == 0)
                correctString += LAT_LONG_SEP.value
            else
                correctString += POINTS_SEP.value
        }
        correctString = correctString.substring(
            0,
            correctString.length - POINTS_SEP.value.length
        ) + AREAS_SEP.value
        for (i in arrayLngLat2.indices) {
            correctString += arrayLngLat2[i].toString()
            if (i % 2 == 0)
                correctString += LAT_LONG_SEP.value
            else
                correctString += POINTS_SEP.value
        }
        correctString = correctString.substring(0, correctString.length - POINTS_SEP.value.length)
        val zoneId = GoogleMapHelper.uidZone

        val mutableList = mutableListOf<zzw>()
        val areas = mutableListOf<Int>()
        val mockedzzt = Mockito.mock(zzt::class.java)
        var areaId = -1
        for(i in 0 until map.size){
            mutableList.add(i,Mockito.mock(zzw::class.java))
            Mockito.`when`((mutableList[i]).points).thenReturn(map[i])
            areaId = GoogleMapHelper.uidArea++
            GoogleMapHelper.areasPoints[areaId] = Triple(zoneId, Marker(mockedzzt), Polygon(mutableList[i]))
            areas.add(areaId)
        }
        GoogleMapHelper.zonesToArea[zoneId] = Pair(null, areas)
        assert(correctString == GoogleMapHelper.zoneAreasToFormattedStringLocation(zoneId))
    }
}