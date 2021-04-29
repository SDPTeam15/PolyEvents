package com.github.sdpteam15.polyevents.helper

import android.content.Context
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.*

interface MapsInterface {
    var isMyLocationEnabled: Boolean
    var cameraPosition: CameraPosition?

    /**
     * Moves the camera to the new camera location given the camera update
     * @param newLatLngZoom camera update that will change the location, zoom level of the camera
     */
    fun moveCamera(newLatLngZoom: CameraUpdate)

    /**
     * Set the map style to the map
     * @param mapStyleOptions map style option to change the style of the map
     */
    fun setMapStyle(mapStyleOptions: MapStyleOptions)

    /**
     * Add a polygon to the map
     * @param poly polygon option to create the polygon
     */
    fun addPolygon(poly: PolygonOptions): Polygon

    /**
     * Add a marker to the map
     * @param icon marker option to create the marker
     */
    fun addMarker(icon: MarkerOptions): Marker

    /**
     * Set the min zoom of the map
     * @param minZoom minimum zoom of the map
     */
    fun setMinZoomPreference(minZoom: Float)

    /**
     * Set the max zoom of the map
     * @param maxZoom maximum zoom of the map
     */
    fun setMaxZoomPreference(maxZoom: Float) {

    }

    /**
     * Set the bounds of the map
     * @param bounds bounds of the camera
     */
    fun setLatLngBoundsForCameraTarget(bounds: LatLngBounds)

    /**
     * Activate the location on the map
     * @param b value to set the location of the map
     * @param requireContext context
     */
    fun setMyLocationEnabled(b: Boolean, requireContext: Context)

    /**
     * Add a Overlay on the map
     * @param tileProvider tileProvider
     * @return TileOverlay
     */
    fun addTileOverlay(tileProvider: TileOverlayOptions?): TileOverlay
}