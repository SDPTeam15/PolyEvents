package com.github.sdpteam15.polyevents.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

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
    override fun setMyLocationEnabled(value: Boolean, context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
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
        map!!.isMyLocationEnabled = value
    }

    override fun addTileOverlay(tileProvider: TileOverlayOptions?): TileOverlay  =
        map!!.addTileOverlay(tileProvider)

    override fun moveCamera(cameraUpdate: CameraUpdate) {
        map!!.moveCamera(cameraUpdate)
    }

    override fun setMapStyle(style: MapStyleOptions) {
        map!!.setMapStyle(style)
    }

    override fun addPolygon(options: PolygonOptions): Polygon {
        return map!!.addPolygon(options)
    }

    override fun addMarker(options: MarkerOptions): Marker {
        return map!!.addMarker(options)
    }

    override fun setMinZoomPreference(minZoomPreference: Float) {
        map!!.setMinZoomPreference(minZoomPreference)
    }

    override fun setMaxZoomPreference(maxZoomPreference: Float) {
        map!!.setMaxZoomPreference(maxZoomPreference)
    }

    override fun setLatLngBoundsForCameraTarget(bounds: LatLngBounds) {
        map!!.setLatLngBoundsForCameraTarget(bounds)
    }
}

