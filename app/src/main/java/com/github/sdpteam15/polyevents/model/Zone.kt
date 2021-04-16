package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.database.DatabaseConstant
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
data class Zone (
    var zoneId: String? = null,
    var zoneName: String? = null,
    var location: String? = null,
    var description: String? = null
){
    /**
     * Get the coordinates of all the areas on the current Zone
     * @return A list of list of LatLng points composing an area
     */
    fun getZoneCoordinates():MutableList<MutableList<LatLng>>{
        val listZoneCoordinates: MutableList<MutableList<LatLng>> = ArrayList()
        if(location!=null) {
            val arr = location!!.split(DatabaseConstant.AREAS_SEP)
            for(s in arr){
                val curList = ArrayList<LatLng>()
                val points = s.split(DatabaseConstant.POINTS_SEP)
                for(p in points){
                    val coor = p.split(DatabaseConstant.LAT_LONG_SEP)
                    curList.add(LatLng(coor[0].toDouble(), coor[1].toDouble()))
                }
                listZoneCoordinates.add(curList)
            }
        }
        return listZoneCoordinates
    }
}