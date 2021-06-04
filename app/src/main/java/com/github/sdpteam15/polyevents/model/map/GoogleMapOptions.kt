package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import android.util.Log
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

object GoogleMapOptions {
    var swBound = LatLng(46.519941764550545, 6.564997248351575)  // SW bounds
    var neBound = LatLng(46.5213428130699, 6.566603220999241)    // NE bounds

    var cameraPosition = LatLng(46.52010210373031, 6.566237434744834)
    var cameraZoom = 18f

    var minZoom = 17f
    var maxZoom = 21f

    /**
     * Saves the current camera position and zoom when changing fragment
     */
    fun saveCamera() {
        //Saves the last position of the camera
        cameraPosition = GoogleMapHelper.map!!.cameraPosition!!.target
        cameraZoom = GoogleMapHelper.map!!.cameraPosition!!.zoom
    }

    /**
     * Setup the map to the desired look
     * @param context Context of the fragment
     * @param drawingMod if we draw the zone in rectangles or in polygons
     */
    fun setUpMap(context: Context?, drawingMod: Boolean, mode:MapsFragmentMod) {


        GoogleMapHelper.restoreMapState(context, drawingMod)
        setMapStyle(context)
        GoogleMapHelper.selectedZone = null
        ZoneAreaMapHelper.deleteMode = false
        ZoneAreaMapHelper.editMode = false
        //setBoundaries()
        //setMinAndMaxZoom()

        //To deactivate the 3d buildings
        //map!!.isBuildingsEnabled = false


        //Restoring the map state
        if(mode != MapsFragmentMod.EditZone){
            //Standard mode -> goes back to the previous camera state
            restoreCameraState()
        }else{
            //Edit zone mode -> Tries to center the camera on the zone
            if(ZoneAreaMapHelper.zonesToArea.containsKey(ZoneManagementActivity.zoneId)){
                val pair = ZoneAreaMapHelper.zonesToArea[ZoneManagementActivity.zoneId]
                val zone = pair!!.first
                if(zone == null){
                    restoreCameraState()
                    return
                }
                val coords = zone.getZoneCoordinates()
                val center = LatLngOperator.mean(coords.flatten())
                GoogleMapHelper.map!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        center,
                        cameraZoom
                    ))
            }else{
                //If it is a new zone, goes back to the previous camera state
                restoreCameraState()
            }
        }
    }

    /**
     * Restores the camera to the location it was before changing fragment or activity, goes to a initial position if it is the first time the map is opened
     */
    fun restoreCameraState() {
        GoogleMapHelper.map!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                cameraPosition,
                cameraZoom
            )
        )
    }

    /**
     * Changes the style of the map
     */
    fun setMapStyle(context: Context?) {
        if (context != null) {
            GoogleMapHelper.map!!.setMapStyle(MapStyleOptions(context.resources.getString(R.string.style_test3)))
        }
    }

    /**
     * Sets the minimal and the maximal zoom
     */
    fun setMinAndMaxZoom() {
        GoogleMapHelper.map!!.setMinZoomPreference(minZoom)
        GoogleMapHelper.map!!.setMaxZoomPreference(maxZoom)
    }

    /**
     * Set the boundaries of the event
     */
    fun setBoundaries() {
        val bounds = LatLngBounds(swBound, neBound)

        // Constrain the camera target to the bounds.
        GoogleMapHelper.map!!.setLatLngBoundsForCameraTarget(bounds)
    }

}