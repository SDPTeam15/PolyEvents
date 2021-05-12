package com.github.sdpteam15.polyevents.model.map

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.view.fragments.MapsFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline

object GoogleMapActionHandler {

    /**
     * Handles the click on a polygon
     * @param mode mode of the map
     * @param context context
     * @param polygon polygon clicked
     */
    fun onPolygonClickHandler(mode: MapsFragment.MapsFragmentMod, context: Context, polygon: Polygon){
        if (mode == MapsFragment.MapsFragmentMod.EditZone) {
            if (GoogleMapHelper.editMode && GoogleMapHelper.canEdit(polygon.tag.toString())) {
                GoogleMapHelper.editArea(context, polygon.tag.toString())
            } else if (GoogleMapHelper.deleteMode && GoogleMapHelper.canEdit(polygon.tag.toString())) {
                GoogleMapHelper.removeArea(polygon.tag.toString().toInt())
            }
        } else {
            GoogleMapHelper.setSelectedZoneFromArea(polygon.tag.toString())
            //Shows the info window of the marker assigned to the area
            GoogleMapHelper.areasPoints.get(polygon.tag)!!.second.showInfoWindow()
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
    fun onMarkerClickHandler(marker: Marker){
        if (!GoogleMapHelper.editMode) {
            marker.showInfoWindow()
        }
        val tag = marker.tag
        if (tag != null) {
            GoogleMapHelper.setSelectedZones(tag.toString())
        }
    }

    /**
     * Handles infoWindow click
     * @param activity activity
     * @param lifecycle lifecycleOwner
     * @param marker marker that ownes the info window
     * @param locationActivated is location of the visitor activated
     */
    fun onInfoWindowClickHandler(activity:Activity, lifecycle: LifecycleOwner, marker: Marker, locationActivated: Boolean){
        HelperFunctions.getLoc(activity).observeOnce(lifecycle) {
            RouteMapHelper.chemin =
                RouteMapHelper.getShortestPath(it.value!!, marker.tag.toString(), locationActivated)?.toMutableList()
                    ?: mutableListOf()
            RouteMapHelper.drawRoute()
        }
    }

    /**
     * Handles click on map
     * @param pos position of the click
     */
    fun onMapClickHandler(pos: LatLng?){
        GoogleMapHelper.clearSelectedZone()
    }

    /**
     * Handles all interaction of drag of the markers
     * @param marker dragged marker
     * @param dragMode which drag mode is used (DRAG_START, DRAG, DRAG_END)
     */
    fun interactionMarkerHandler(marker: Marker, dragMode: MarkerDragMode){
        when (marker.snippet) {
            PolygonAction.MOVE.toString() -> GoogleMapHelper.translatePolygon(marker)
            PolygonAction.RIGHT.toString() -> GoogleMapHelper.transformPolygon(marker)
            PolygonAction.DOWN.toString() -> GoogleMapHelper.transformPolygon(marker)
            PolygonAction.DIAG.toString() -> GoogleMapHelper.transformPolygon(marker)
            PolygonAction.ROTATE.toString() -> GoogleMapHelper.rotatePolygon(marker)
            PolygonAction.MARKER_START.toString() -> RouteMapHelper.moveMarker(marker, dragMode)
            PolygonAction.MARKER_END.toString() -> RouteMapHelper.moveMarker(marker, dragMode)
        }
        GoogleMapHelper.tempPoly?.points = GoogleMapHelper.tempLatLng
    }
}