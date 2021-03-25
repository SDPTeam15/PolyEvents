package com.github.sdpteam15.polyevents.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline


class MapsFragment : Fragment(), OnMapReadyCallback, OnPolylineClickListener,
    OnPolygonClickListener, OnMarkerClickListener, OnInfoWindowClickListener,
    OnMapLongClickListener, OnMarkerDragListener {

    var locationPermissionGranted = false
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onPause() {
        super.onPause()
        GoogleMapHelper.saveCamera()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.id_fragment_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        if (!locationPermissionGranted) {
            getLocationPermission()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        GoogleMapHelper.context = context
        GoogleMapHelper.map = googleMap

        GoogleMapHelper.map!!.setOnPolylineClickListener(this)
        GoogleMapHelper.map!!.setOnPolygonClickListener(this)
        GoogleMapHelper.map!!.setOnMarkerClickListener(this)
        GoogleMapHelper.map!!.setOnMapLongClickListener(this)
        GoogleMapHelper.map!!.setOnMarkerDragListener(this)
        GoogleMapHelper.map!!.setOnInfoWindowClickListener(this)
        GoogleMapHelper.setUpMap()
    }

    override fun onPolylineClick(polyline: Polyline) {

    }

    override fun onPolygonClick(polygon: Polygon) {
        //Shows the info window of the marker assigned to the area
        GoogleMapHelper.areasPoints.get(polygon.tag)!!.first.showInfoWindow()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()

        //Return true to say that we don't want the event to go further (to the usual event when a marker is clicked)
        return true
    }

    override fun onInfoWindowClick(p0: Marker) {
        HelperFunctions.showToast(
            "Info Window clicked for marker" + p0.title + ", can lanch activity here",
            requireContext()
        )
    }


    override fun onMapLongClick(pos: LatLng) {
        GoogleMapHelper.clearTemp()
        GoogleMapHelper.setupEditZone(pos)
    }

    override fun onMarkerDragEnd(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0)
    }

    override fun onMarkerDragStart(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0)
    }


    override fun onMarkerDrag(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0)
    }
    //-----------END LISTENER---------------------------------------


    /**
     * Asks for permission to use location
     */
    fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
}