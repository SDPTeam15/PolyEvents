package com.github.sdpteam15.polyevents.model.map

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.clearSelectedZone
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.setSelectedZoneFromArea
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.setSelectedZones
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline

/**
 * Object that has all functions that handle interactions with the google map
 */
object GoogleMapActionHandler {

    /**
     * Handles the click on a polygon
     * @param mode mode of the map
     * @param context context
     * @param polygon polygon clicked
     */
    fun onPolygonClickHandler(
        mode: MapsFragmentMod,
        context: Context,
        polygon: Polygon
    ) {
        if (mode == MapsFragmentMod.EditZone) {
            if (ZoneAreaMapHelper.editMode && ZoneAreaMapHelper.canEdit(polygon.tag.toString())) {
                ZoneAreaMapHelper.editArea(context, polygon.tag.toString())
            } else if (ZoneAreaMapHelper.deleteMode && ZoneAreaMapHelper.canEdit(polygon.tag.toString())) {
                ZoneAreaMapHelper.removeArea(polygon.tag.toString().toInt())
            }
        } else {
            setSelectedZoneFromArea(polygon.tag.toString())
            //Shows the info window of the marker assigned to the area
            ZoneAreaMapHelper.areasPoints.get(polygon.tag)!!.second.showInfoWindow()
        }
    }

    /**
     * Handles the click on a polyline : if on delete mode, deletes the polyline
     * @param polyline polyline clicked
     */
    fun polylineClick(polyline: Polyline) {
        if (RouteMapHelper.deleteMode) {
            RouteMapHelper.removeLine(RouteMapHelper.idToEdge[polyline.tag]!!)
        }
    }

    /**
     * Handles the marker click
     * @param marker marker clicked
     */
    fun onMarkerClickHandler(marker: Marker) {
        if (!ZoneAreaMapHelper.editMode) {
            marker.showInfoWindow()
        }
        val tag = marker.tag
        if (tag != null) {
            setSelectedZones(tag.toString())
        }
    }

    /**
     * Handles infoWindow click
     * @param activity activity
     * @param lifecycle lifecycleOwner
     * @param marker marker that ownes the info window
     * @param locationActivated is location of the visitor activated
     */
    fun onInfoWindowClickHandler(
        activity: Activity,
        lifecycle: LifecycleOwner,
        marker: Marker,
        locationActivated: Boolean
    ) {
        HelperFunctions.getLoc(activity).observeOnce(lifecycle) {
            RouteMapHelper.chemin =
                RouteMapHelper.getShortestPath(it.value!!, marker.tag.toString(), locationActivated)
                    ?.toMutableList()
                    ?: mutableListOf()
            RouteMapHelper.drawRoute()
        }
    }

    /**
     * Handles click on map
     * @param pos position of the click
     */
    fun onMapClickHandler(pos: LatLng?) {
        clearSelectedZone()
    }

    /**
     * Handles all interaction of drag of the markers
     * @param marker dragged marker
     * @param dragMode which drag mode is used (DRAG_START, DRAG, DRAG_END)
     */
    fun interactionMarkerHandler(marker: Marker, dragMode: MarkerDragMode) {
        when (marker.snippet) {
            PolygonAction.MOVE.toString() -> ZoneAreaMapHelper.translatePolygon(marker)
            PolygonAction.RIGHT.toString() -> ZoneAreaMapHelper.transformPolygon(marker)
            PolygonAction.DOWN.toString() -> ZoneAreaMapHelper.transformPolygon(marker)
            PolygonAction.DIAG.toString() -> ZoneAreaMapHelper.transformPolygon(marker)
            PolygonAction.ROTATE.toString() -> ZoneAreaMapHelper.rotatePolygon(marker)
            PolygonAction.MARKER_START.toString() -> RouteMapHelper.moveMarker(marker, dragMode)
            PolygonAction.MARKER_END.toString() -> RouteMapHelper.moveMarker(marker, dragMode)
        }
        ZoneAreaMapHelper.tempPoly?.points = ZoneAreaMapHelper.tempLatLng
    }
}