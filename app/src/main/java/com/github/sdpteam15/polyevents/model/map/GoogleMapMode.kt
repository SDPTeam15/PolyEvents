package com.github.sdpteam15.polyevents.model.map

import android.graphics.Color

object GoogleMapMode {

    const val DEFAULT_ZONE_STROKE_COLOR = Color.BLACK
    const val SELECTED_ZONE_STROKE_COLOR = Color.BLUE
    const val EDITED_ZONE_STROKE_COLOR = Color.GREEN
    /**
     * Color all the areas of a zone to a certain color
     * @param idZone id of the zone to color
     * @param color color target for the zone
     */
    fun colorAreas(idZone: String, color: Int) {
        for (key in ZoneAreaMapHelper.zonesToArea[idZone]!!.second) {
            ZoneAreaMapHelper.areasPoints[key]!!.third.strokeColor = color
        }
    }

    /**
     * clears the color of the current selected zone
     */
    fun clearSelectedZone() {
        if (GoogleMapHelper.selectedZone != null) {
            colorAreas(GoogleMapHelper.selectedZone!!, DEFAULT_ZONE_STROKE_COLOR)
            GoogleMapHelper.selectedZone = null
        }
    }

    /**
     * Set the selected zone to color it on the map
     * @param tag id of the selected zone
     */
    fun setSelectedZones(tag: String) {
        clearSelectedZone()
        GoogleMapHelper.selectedZone = tag
        colorAreas(tag, SELECTED_ZONE_STROKE_COLOR)
    }

    /**
     * Set the selected zone from to color it on the map from the id of the area
     * @param tag id of the area to find the zone it belongs
     */
    fun setSelectedZoneFromArea(tag: String) {
        setSelectedZones(ZoneAreaMapHelper.areasPoints[tag.toInt()]!!.first)
    }
}