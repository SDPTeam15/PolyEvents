package com.github.sdpteam15.polyevents.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

/**
 * Adapter for the map
 * @param map map
 */
open class GoogleMapAdapter(var map: GoogleMap?) : MapsInterface {

    override var cameraPosition: CameraPosition? = null
        get() = field ?: map!!.cameraPosition

    override var isMyLocationEnabled
        get() = map!!.isMyLocationEnabled
        @SuppressLint("MissingPermission")
        set(value) {
            map!!.isMyLocationEnabled = value
        }

    //Could be done as set to the isMyLocationEnabled but it would neet the context stored in this class
    override fun setMyLocationEnabled(b: Boolean, requireContext: Context) {

        if (ActivityCompat.checkSelfPermission(
                requireContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        map!!.isMyLocationEnabled = b
    }

    override fun addTileOverlay(tileProvider: TileOverlayOptions?): TileOverlay =
        map!!.addTileOverlay(tileProvider)

    override fun moveCamera(newLatLngZoom: CameraUpdate) {
        map!!.moveCamera(newLatLngZoom)
    }

    override fun setMapStyle(mapStyleOptions: MapStyleOptions) {
        map!!.setMapStyle(mapStyleOptions)
    }

    override fun addPolygon(option: PolygonOptions): Polygon {
        return map!!.addPolygon(option)
    }

    override fun addMarker(option: MarkerOptions): Marker {
        return map!!.addMarker(option)
    }

    override fun addPolyline(option: PolylineOptions): Polyline {
        return map!!.addPolyline(option)
    }

    override fun setMinZoomPreference(minZoom: Float) {
        map!!.setMinZoomPreference(minZoom)
    }

    override fun setMaxZoomPreference(maxZoom: Float) {
        map!!.setMaxZoomPreference(maxZoom)
    }

    override fun setLatLngBoundsForCameraTarget(bounds: LatLngBounds) {
        map!!.setLatLngBoundsForCameraTarget(bounds)
    }
}

