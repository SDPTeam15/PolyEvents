package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.clearSelectedZone
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.setSelectedZoneFromArea
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.setSelectedZones
import com.github.sdpteam15.polyevents.view.fragments.ZoneEventsFragment
import com.github.sdpteam15.polyevents.view.fragments.ZonePreviewBottomSheetDialogFragment
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
        activity: FragmentActivity,
        lifecycle: LifecycleOwner,
        marker: Marker,
        locationActivated: Boolean
    ) {
        /**
         * Display a new ZonePreviewDialog bottom sheet, displaying the zone and the current events
         * going on. Has 2 buttons, one for showing the itinerary on the map, the other to see all
         * the events going on in that zone
         */
        ZonePreviewBottomSheetDialogFragment.newInstance(
            zoneId = marker.tag as String,
            onShowEventsClickListener = {
                HelperFunctions.changeFragmentWithBundle(
                    activity,
                    ZoneEventsFragment::class.java,
                    bundle = bundleOf(
                        // marker tag should hold the zone id
                        ZonePreviewBottomSheetDialogFragment.EXTRA_ZONE_ID to marker.tag,
                        // marker title should hold the zone name
                        ZonePreviewBottomSheetDialogFragment.EXTRA_ZONE_NAME to marker.title
                    ),
                    addToBackStack = true
                )
            },
            onItineraryClickListener = {
                // The listener invoked when clicked on the show itinerary button
                HelperFunctions.getLoc(activity).observeOnce(lifecycle) {
                    RouteMapHelper.chemin =
                        RouteMapHelper.getShortestPath(
                            it.value!!,
                            marker.tag.toString(),
                            locationActivated
                        )
                            ?.toMutableList()
                            ?: mutableListOf()
                    RouteMapHelper.drawRoute()
                }
            }
        ).show(activity.supportFragmentManager, ZonePreviewBottomSheetDialogFragment.TAG)
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