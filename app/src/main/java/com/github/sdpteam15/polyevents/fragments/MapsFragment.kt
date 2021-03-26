package com.github.sdpteam15.polyevents.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.isPermissionGranted
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MapsFragment : Fragment(), OnMapReadyCallback, OnPolylineClickListener,
    OnPolygonClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener,
    OnMyLocationButtonClickListener {

    private lateinit var locationButton: FloatingActionButton
    var locationPermissionGranted = false
    private var useUserLocation = false
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        val addNewAreaButton:View = view.findViewById(R.id.addNewArea)
        val saveNewAreaButton:View = view.findViewById(R.id.acceptNewArea)
        addNewAreaButton.setOnClickListener(View.OnClickListener { view -> GoogleMapHelper.createNewArea() })
        saveNewAreaButton.setOnClickListener(View.OnClickListener { view -> GoogleMapHelper.saveNewArea() })

        locationButton = view.findViewById(R.id.id_location_button)
        locationButton.setOnClickListener {
            switchLocationOnOff()
        }
        locationButton.tag = R.drawable.ic_location_on

        val locateMeButton = view.findViewById<FloatingActionButton>(R.id.id_locate_me_button)
        locateMeButton.setOnClickListener {
            moveToMyLocation()
        }

        locateMeButton.tag = R.drawable.ic_locate_me

        return view
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
        GoogleMapHelper.map!!.setOnMarkerDragListener(this)
        GoogleMapHelper.map!!.setOnInfoWindowClickListener(this)
        GoogleMapHelper.map!!.setOnMyLocationButtonClickListener(this)
        GoogleMapHelper.setUpMap()

        if (useUserLocation) {
            activateMyLocation()
        }
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

    override fun onMarkerDragEnd(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0)
    }

    override fun onMarkerDragStart(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0)
    }


    override fun onMarkerDrag(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0)
    }

    override fun onMyLocationButtonClick(): Boolean {
        // Return false to indicate that we did not consume the event. So the default behavior
        // still occurs (which is to move the camera to the current location.
        return false
    }

    /**
     * Activate "my location" tracking.
     * (source : https://developers.google.com/maps/documentation/android-sdk/location#kotlin)
     */
    private fun activateMyLocation() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            == PackageManager.PERMISSION_GRANTED) {
            GoogleMapHelper.map!!.isMyLocationEnabled = true

            // Hide the built-in location button (but DO NOT disable it !)
            getBuiltInLocationButton().isVisible = false

            // Change the appearance of the location button.
            setLocationIcon(true)
            useUserLocation = true
        }
    }

    /**
     * Update the location button icon according to whether the location is on or not
     * @param locationIsOn : boolean, true if the location is currently used
     */
    private fun setLocationIcon(locationIsOn: Boolean) {
        val idOfResource: Int = if (locationIsOn) {
            R.drawable.ic_location_on
        } else {
            R.drawable.ic_location_off
        }
        requireView().findViewById<FloatingActionButton>(R.id.id_location_button)
            .setImageResource(idOfResource)
        locationButton.tag = idOfResource
    }

    /**
     * Switch the location to on or off according to the current state.
     * Accordingly update the icon.
     */
    @SuppressLint("MissingPermission")
    private fun switchLocationOnOff() {
        val locationIsOn = GoogleMapHelper.map!!.isMyLocationEnabled

        if (locationIsOn) {
            // Disable the location
            GoogleMapHelper.map!!.isMyLocationEnabled = false
            setLocationIcon(false)
            useUserLocation = false
        } else {
            activateMyLocation()
        }
    }

    /**
     * Get and return the built-in "my location" button from map fragment.
     * @return the view of the built-in location button
     */
    private fun getBuiltInLocationButton(): View {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.id_fragment_map) as SupportMapFragment?

        // Magic : https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
        return (mapFragment!!.requireView()
            .findViewById<View>(Integer.parseInt("1")).parent as View)
            .findViewById(Integer.parseInt("2"))
    }

    /**
     * Click on the built-in my location button so that
     * the default effect occurs (move camera to current location).
     */
    private fun moveToMyLocation() {
        getBuiltInLocationButton().performClick()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is denied, the result arrays are empty.
                if (isPermissionGranted(
                        permissions,
                        grantResults,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    locationPermissionGranted = true
                    activateMyLocation()
                } else {
                    locationPermissionGranted = false
                }
            }
        }
    }
}