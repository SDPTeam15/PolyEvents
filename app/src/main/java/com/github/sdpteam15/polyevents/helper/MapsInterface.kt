package com.github.sdpteam15.polyevents.helper

import android.content.Context
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.*

interface MapsInterface {
    var isMyLocationEnabled: Boolean
    var cameraPosition: CameraPosition?

    /**
     * Moves the camera to the new camera location given the camera update
     */
    fun moveCamera(newLatLngZoom: CameraUpdate)

    /**
     * Set the map style to the map
     */
    fun setMapStyle(mapStyleOptions: MapStyleOptions)

    /**
     * Add a polygon to the map
     */
    fun addPolygon(poly: PolygonOptions): Polygon

    /**
     * Add a marker to the map
     */
    fun addMarker(icon: MarkerOptions): Marker

    /**
     * Set the min zoom of the map
     */
    fun setMinZoomPreference(minZoom: Float)

    /**
     * Set the max zoom of the map
     */
    fun setMaxZoomPreference(maxZoom: Float) {

    }

    /**
     * Set the bounds of the map
     */
    fun setLatLngBoundsForCameraTarget(bounds: LatLngBounds)

    /**
     * Activate the location on the map
     */
    fun setMyLocationEnabled(b: Boolean, requireContext: Context)
}