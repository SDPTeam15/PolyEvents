package com.github.sdpteam15.polyevents.helper

import android.content.Context
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.*

interface MapsInterface {
    var isMyLocationEnabled: Boolean
    var cameraPosition: CameraPosition?

    fun moveCamera(newLatLngZoom: CameraUpdate)

    fun setMapStyle(mapStyleOptions: MapStyleOptions)
    fun addPolygon(poly: PolygonOptions): Polygon
    fun addMarker(icon: MarkerOptions): Marker
    fun setMinZoomPreference(minZoom: Float) {

    }

    fun setMaxZoomPreference(maxZoom: Float) {

    }

    fun setLatLngBoundsForCameraTarget(bounds: LatLngBounds) {

    }

    fun setMyLocationEnabled(b: Boolean, requireContext: Context) {

    }
}