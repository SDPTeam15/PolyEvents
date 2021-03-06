package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
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
     * @param activity activity
     * @param lifecycle LifecycleOwner
     * @param locationActivated True if location is activated
     */
    fun onPolygonClickHandler(
        mode: MapsFragmentMod,
        context: Context,
        polygon: Polygon,
        activity: FragmentActivity,
        lifecycle: LifecycleOwner,
        locationActivated: Boolean
    ) {
        setSelectedZoneFromArea(polygon.tag.toString())
        if (mode == MapsFragmentMod.EditZone) {
            if (ZoneAreaMapHelper.editMode && ZoneAreaMapHelper.canEdit(polygon.tag.toString())) {
                ZoneAreaMapHelper.editArea(context, polygon.tag.toString())
            } else if (ZoneAreaMapHelper.deleteMode && ZoneAreaMapHelper.canEdit(polygon.tag.toString())) {
                ZoneAreaMapHelper.removeArea(polygon.tag.toString().toInt())
            }
        } else if(mode == MapsFragmentMod.Visitor){
            // Get the marker associated to the selected zone
            val zoneMarker = ZoneAreaMapHelper.areasPoints.get(polygon.tag)!!.second
            // Display the bottom dialog previewing the selected zone
            displayZoneDetailsBottomDialog(
                zoneId = zoneMarker.tag.toString(),
                zoneName = zoneMarker.title,
                activity = activity,
                lifecycle = lifecycle,
                locationActivated = locationActivated
            )
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
     * @param activity activity
     * @param lifecycle LifecycleOwner
     * @param locationActivated True if location is activated
     */
    fun onMarkerClickHandler(
        marker: Marker,
        mode: MapsFragmentMod,
        activity: FragmentActivity,
        lifecycle: LifecycleOwner,
        locationActivated: Boolean
    ) {
        if (canClickMarker(marker)) {
            if(mode == MapsFragmentMod.Visitor) {
                if (!ZoneAreaMapHelper.editMode) {
                    displayZoneDetailsBottomDialog(
                        zoneId = marker.tag.toString(),
                        zoneName = marker.title,
                        activity = activity,
                        lifecycle = lifecycle,
                        locationActivated = locationActivated
                    )
                }
            }
            val tag = marker.tag
            if (tag != null) {
                setSelectedZones(tag.toString())
            }
        }
    }

    /**
     * Returns true if the marker is clickable
     * @param marker marker that has been clicked
     */
    fun canClickMarker(marker: Marker): Boolean {
        if (marker == RouteMapHelper.startMarker
            || marker == RouteMapHelper.endMarker
            || marker == ZoneAreaMapHelper.moveDiagMarker
            || marker == ZoneAreaMapHelper.moveDownMarker
            || marker == ZoneAreaMapHelper.moveMarker
            || marker == ZoneAreaMapHelper.moveRightMarker
            || marker == ZoneAreaMapHelper.rotationMarker
        )
            return false
        return true
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
        displayZoneDetailsBottomDialog(
            zoneId = marker.tag.toString(),
            zoneName = marker.title,
            activity = activity,
            lifecycle = lifecycle,
            locationActivated = locationActivated
        )
    }

    /**
     * Display Zone Details Bottom Dialog Fragment
     * @param zoneId zone id
     * @param zoneName zone name
     * @param lifecycle lifecycleOwner
     * @param marker marker that ownes the info window
     * @param locationActivated is location of the visitor activated
     */
    private fun displayZoneDetailsBottomDialog(
        zoneId: String,
        zoneName: String?,
        activity: FragmentActivity,
        lifecycle: LifecycleOwner,
        locationActivated: Boolean
    ) {
        /**
         * Display a new ZonePreviewDialog bottom sheet, displaying the zone and the current events
         * going on. Has 2 buttons, one for showing the itinerary on the map, the other to see all
         * the events going on in that zone
         */
        ZonePreviewBottomSheetDialogFragment.newInstance(
                zoneId = zoneId,
                onShowEventsClickListener = {
                    HelperFunctions.changeFragmentWithBundle(
                            activity,
                            ZoneEventsFragment::class.java,
                            bundle = bundleOf(
                                    // marker tag should hold the zone id
                                    ZonePreviewBottomSheetDialogFragment.EXTRA_ZONE_ID to zoneId,
                                    // marker title should hold the zone name
                                    ZonePreviewBottomSheetDialogFragment.EXTRA_ZONE_NAME to zoneName
                            ),
                            //addToBackStack = true
                    )
                },
                onItineraryClickListener = {
                    // The listener invoked when clicked on the show itinerary button
                    HelperFunctions.getLoc(activity).observeOnce(lifecycle) {
                        RouteMapHelper.chemin =
                                RouteMapHelper.getShortestPath(
                                        it.value!!,
                                        zoneId,
                                        locationActivated
                                )
                                        ?.toMutableList()
                                        ?: mutableListOf()
                        if(RouteMapHelper.chemin.isEmpty()){
                            showToast(activity.applicationContext.getString(R.string.can_not_find_the_path), activity)
                        }
                        RouteMapHelper.drawRoute(activity)
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