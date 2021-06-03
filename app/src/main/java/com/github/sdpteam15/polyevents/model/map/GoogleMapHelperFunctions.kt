package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementListActivity
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Helper functions for google map
 */
object GoogleMapHelperFunctions {

    /**
     * Sets the values for the markers
     * @param pos position of the marker
     * @param anchor position of the icon with respect to the marker
     * @param snippet subtitle of the info window
     * @param title title of the info window
     * @param draggable is the marker draggable
     * @param idDrawable id of the icon
     * @param bound bounds of the icon
     * @param dim dimensions of the icon
     */
    fun newMarker(
        context: Context?,
        pos: LatLng,
        anchor: IconAnchor,
        snippet: String?,
        title: String?,
        draggable: Boolean,
        idDrawable: Int,
        bound: IconBound,
        dim: IconDimension
    ): MarkerOptions {
        var mo = MarkerOptions().position(pos).anchor(anchor.anchorWidth, anchor.anchorHeight)
            .draggable(draggable).snippet(snippet).title(title)
        if (context != null) {
            mo = mo.icon(getMarkerRessource(context, idDrawable, bound, dim))
        }

        return mo
    }

    /**
     * Generates the icon for the invisible icons
     * @param id id of the icon
     * @param bound bounds of the icon
     * @param dim dimensions of the icon
     * ref : https://stackoverflow.com/questions/35718103/how-to-specify-the-size-of-the-icon-on-the-marker-in-google-maps-v2-android
     * TODO : Check if we should make it a singleton to save memory/performance
     */
    private fun getMarkerRessource(
        context: Context?,
        id: Int,
        bound: IconBound,
        dim: IconDimension
    ): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context!!, id)
        vectorDrawable?.setBounds(
            bound.leftBound,
            bound.topBound,
            bound.rightBound,
            bound.bottomBound
        )
        val bitmap = Bitmap.createBitmap(dim.width, dim.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    //TODO : Maybe move into GoogleMapHelper along with the observableList
    /**
     * Get all zones from the database
     * @param context context
     * @param lifecycleOwner lifecycleOwner
     * @param mode map display mode
     */
    fun getAllZonesFromDB(context: Context, lifecycleOwner: LifecycleOwner, mode: MapsFragmentMod) {
        ZoneManagementListActivity.zones.observeAdd(lifecycleOwner) {
            ZoneAreaMapHelper.importNewZone(context, it.value, mode != MapsFragmentMod.EditZone)
        }

        Database.currentDatabase.zoneDatabase.getActiveZones(
            ZoneManagementListActivity.zones, 50
        ).observe(lifecycleOwner) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to get the list of zones", context)
            }
        }
    }

    /**
     * Generate the string format for Firebase of the area points for a given zone
     * @param idZone id of the zone
     */
    fun zoneAreasToFormattedStringLocation(idZone: String): String {
        var temp = ZoneAreaMapHelper.zonesToArea[idZone]!!.second.toMutableList()
        var s = ""
        for (uid in temp) {
            s += areaToFormattedStringLocation(
                ZoneAreaMapHelper.areasPoints[uid]!!.third.points.dropLast(
                    1
                )
            )
            s += DatabaseConstant.ZoneConstant.AREAS_SEP
        }
        if (s != "") {
            s = s.substring(0, s.length - DatabaseConstant.ZoneConstant.AREAS_SEP.value.length)
        }
        return s
    }

    /**
     * Generate the string format of a list of points
     * @param loc list of the points to save into a string
     * @return formatted string of the points
     */
    fun areaToFormattedStringLocation(loc: List<LatLng>?): String {
        if (loc == null) {
            return ""
        }
        var s = ""

        for (c in loc) {
            s += c.latitude.toString() + DatabaseConstant.ZoneConstant.LAT_LONG_SEP.value + c.longitude.toString() + DatabaseConstant.ZoneConstant.POINTS_SEP.value
        }
        if (s != "") {
            s = s.substring(0, s.length - DatabaseConstant.ZoneConstant.AREAS_SEP.value.length)
        }
        return s
    }
}