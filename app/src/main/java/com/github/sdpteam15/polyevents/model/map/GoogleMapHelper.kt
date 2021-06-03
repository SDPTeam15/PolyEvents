package com.github.sdpteam15.polyevents.model.map

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper.addArea
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper.areasPoints
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper.editingZone
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper.importNewZone
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper.waitingZones
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper.zonesToArea

//TODO : Refactor file, it is too long

@SuppressLint("StaticFieldLeak")
object GoogleMapHelper {
    var map: MapsInterface? = null
    var uidArea = 0
    var uidZone = 0

    var selectedZone: String? = null


    /**
     * Redraws all areas that were previously drawn before changing fragment or activity, draws some example
     */
    fun restoreMapState(context: Context?, drawingMod: Boolean) {
        val currentEditZone = editingZone
        val areaTemp = areasPoints.toMap()
        val zoneTemp = zonesToArea.toMap()
        zonesToArea.clear()
        areasPoints.clear()
        for ((k, v) in zoneTemp) {
            editingZone = k
            zonesToArea[editingZone!!] = Pair(null, mutableListOf())
            for (id in v.second) {
                addArea(
                    context,
                    id,
                    Pair(areaTemp[id]!!.third.points, areaTemp[id]!!.third.holes),
                    areaTemp[id]!!.second.title
                )
            }
        }
        val copyWaitingZones = waitingZones.toList()
        for (zone in copyWaitingZones) {
            importNewZone(context, zone, drawingMod)
            waitingZones.remove(zone)
        }

        editingZone = currentEditZone
    }

    /**
     * Helper function to have size in pixels from dp
     * https://android--code.blogspot.com/2020/08/android-kotlin-convert-dp-to-pixels.html
     */
    fun Int.dpToPixelsFloat(context: Context): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    )
}