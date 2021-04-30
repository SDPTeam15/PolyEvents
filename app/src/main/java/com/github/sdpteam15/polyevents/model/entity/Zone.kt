package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.map.Attachable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Entity model for a zone. Events occur inside a zone.
 *
 * @property zoneName the name of the zone
 * @property location the location of the zone
 *
 */
@IgnoreExtraProperties
data class Zone(
    var zoneId: String? = null,
    var zoneName: String? = null,
    var location: String? = null,
    var description: String? = null
) : Attachable {
    /**
     * Get the coordinates of all the areas on the current Zone
     * @return A list of list of LatLng points composing an area
     */
    fun getZoneCoordinates(): MutableList<MutableList<LatLng>> {
        val listZoneCoordinates: MutableList<MutableList<LatLng>> = ArrayList()
        if (location != null) {
            val arr = location!!.split(AREAS_SEP.value)
            for (s in arr) {
                val curList = ArrayList<LatLng>()
                val points = s.split(POINTS_SEP.value)
                for (p in points) {
                    val coor = p.split(LAT_LONG_SEP.value)
                    try {
                        curList.add(LatLng(coor[0].toDouble(), coor[1].toDouble()))
                    } catch (e: NumberFormatException) {
                        println(coor)
                    }
                }
                listZoneCoordinates.add(curList)
            }
        }
        return listZoneCoordinates
    }

    /**
     * Get the coordinates of all the grouped areas on the current Zone
     * @return A list of list of LatLng points composing an area
     */
    fun getDrawingPoints(): List<List<LatLng>> {
        // TODO reduce the number of element
        return getZoneCoordinates()
    }

    override fun getAttachedNewPoint(
        position: LatLng,
        angle: Double?
    ): Pair<RouteNode, Double> {
        val list = getDrawingPoints()
        var res: Pair<RouteNode, Double>? = null
        for (e in list)
            for (i in e.indices) {
                val from = e[i]
                val to = e[(i + 1) % e.size]
                val lineAngle = angle(from, to)
                if (angle == null || !isTooParallel(angle, lineAngle)) {
                    val newPoint = getNearestPoint(RouteNode.fromLatLong(from), RouteNode.fromLatLong(to), position)
                    newPoint.areaId = zoneId
                    val distance = norm(minus(position, newPoint.toLatLng()))
                    if (res == null || distance < res.second)
                        res = Pair(newPoint, distance)
                }
            }
        return res!!
    }

    fun getZoneCenter(): LatLng {
        var pointCount = 0
        var lat = 0.0
        var lng = 0.0
        for (latLngList in getZoneCoordinates()){
            for (latLng in latLngList){
                pointCount++
                lat += latLng.latitude
                lng += latLng.longitude
            }
        }
        return LatLng(lat/pointCount,lng/pointCount)
    }
}