package com.github.sdpteam15.polyevents.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.map.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsFragment(private val mod: MapsFragmentMod) : Fragment(),
    OnMapReadyCallback,
    OnPolylineClickListener,
    OnPolygonClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener,
    OnMyLocationButtonClickListener, OnMapClickListener {


    companion object {
        var instance: MapsFragment? = null
    }

    private lateinit var locationButton: FloatingActionButton

    private lateinit var addNewAreaButton: FloatingActionButton
    private lateinit var deleteAreaButton: FloatingActionButton
    private lateinit var saveNewAreaButton: FloatingActionButton
    private lateinit var editAreaButton: FloatingActionButton

    private lateinit var locateMeButton: FloatingActionButton
    private lateinit var saveButton: FloatingActionButton
    private lateinit var heatmapButton: FloatingActionButton

    private lateinit var addNewRouteButton: FloatingActionButton
    private lateinit var removeRouteButton: FloatingActionButton
    private lateinit var saveNewRouteButton: FloatingActionButton


    var locationPermissionGranted = false
    private var useUserLocation = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        instance = this

        setUpButtons(view)

        setUpButtonsVisibility()

        GoogleMapHelperFunctions.getAllZonesFromDB(requireContext(), this, mod)

        setUpButtonsListeners()

        locationButton.tag = R.drawable.ic_location_on

        locateMeButton.tag = R.drawable.ic_locate_me

        return view
    }

    override fun onPause() {
        super.onPause()
        GoogleMapOptions.saveCamera()
        GoogleMapHeatmap.resetHeatmap()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.id_fragment_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        if (!locationPermissionGranted) {
            HelperFunctions.getLocationPermission(requireActivity()).observeOnce {
                locationPermissionGranted = it.value
                activateMyLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        GoogleMapHelper.map = GoogleMapAdapter(googleMap)
        RouteMapHelper.getNodesAndEdgesFromDB(context, this)

        setMapListeners(googleMap!!)
        GoogleMapOptions.setUpMap(requireContext(), mod != MapsFragmentMod.EditZone)

        if (useUserLocation) {
            activateMyLocation()
        }
    }


    //----- HELPER FUNCTIONS -------

    /**
     * Gets the references of the floating buttons to set listeners and visibility
     * @param view view on which the buttons are
     */
    private fun setUpButtons(view: View) {
        locationButton = view.findViewById(R.id.id_location_button)

        addNewAreaButton = view.findViewById(R.id.addNewArea)
        //deleteAreaButton= view.findViewById(R.id.id_delete_areas)
        saveNewAreaButton = view.findViewById(R.id.acceptNewArea)
        editAreaButton = view.findViewById(R.id.id_edit_area)

        locateMeButton = view.findViewById(R.id.id_locate_me_button)
        saveButton = view.findViewById(R.id.saveAreas)
        heatmapButton = view.findViewById(R.id.id_heatmap)

        addNewRouteButton = view.findViewById(R.id.addNewRoute)
        removeRouteButton = view.findViewById(R.id.removeRoute)
        saveNewRouteButton = view.findViewById(R.id.saveNewRoute)
    }

    /**
     * Sets the visibility of the buttons acording to the mode
     */
    private fun setUpButtonsVisibility() {
        when (mod) {
            MapsFragmentMod.Visitor -> {
                locateMeButton.visibility = View.VISIBLE
                heatmapButton.visibility = View.VISIBLE
                locationButton.visibility = View.VISIBLE

                addNewAreaButton.visibility = View.INVISIBLE
                //deleteAreaButton.visibility = View.INVISIBLE
                saveNewAreaButton.visibility = View.INVISIBLE
                editAreaButton.visibility = View.INVISIBLE
                saveButton.visibility = View.INVISIBLE

                addNewRouteButton.visibility = View.INVISIBLE
                removeRouteButton.visibility = View.INVISIBLE
                saveNewRouteButton.visibility = View.INVISIBLE
            }
            MapsFragmentMod.EditZone -> {
                locateMeButton.visibility = View.INVISIBLE
                heatmapButton.visibility = View.INVISIBLE
                locationButton.visibility = View.INVISIBLE

                addNewAreaButton.visibility = View.VISIBLE
                //deleteAreaButton.visibility = View.VISIBLE
                saveNewAreaButton.visibility = View.VISIBLE
                editAreaButton.visibility = View.VISIBLE
                saveButton.visibility = View.VISIBLE

                addNewRouteButton.visibility = View.INVISIBLE
                removeRouteButton.visibility = View.INVISIBLE
                saveNewRouteButton.visibility = View.INVISIBLE
            }
            MapsFragmentMod.EditRoute -> {
                locateMeButton.visibility = View.INVISIBLE
                heatmapButton.visibility = View.INVISIBLE
                locationButton.visibility = View.INVISIBLE

                addNewAreaButton.visibility = View.INVISIBLE
                //deleteAreaButton.visibility = View.INVISIBLE
                saveNewAreaButton.visibility = View.INVISIBLE
                editAreaButton.visibility = View.INVISIBLE
                saveButton.visibility = View.INVISIBLE

                addNewRouteButton.visibility = View.VISIBLE
                removeRouteButton.visibility = View.VISIBLE
                saveNewRouteButton.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Sets the onClickListeners to the buttons
     */
    private fun setUpButtonsListeners() {
        addNewAreaButton.setOnClickListener { ZoneAreaMapHelper.createNewArea(requireContext()) }
        saveNewAreaButton.setOnClickListener { ZoneAreaMapHelper.saveNewArea(requireContext()) }
        editAreaButton.setOnClickListener { ZoneAreaMapHelper.editMode(requireContext()) }
        //deleteAreaButton.setOnClickListener{GoogleMapHelper.deleteMode(requireContext())}

        addNewRouteButton.setOnClickListener { RouteMapHelper.createNewRoute(requireContext()) }
        removeRouteButton.setOnClickListener { RouteMapHelper.removeRoute() }
        saveNewRouteButton.setOnClickListener { RouteMapHelper.saveNewRoute() }

        heatmapButton.setOnClickListener { GoogleMapHeatmap.heatmap() }

        saveButton.setOnClickListener { ZoneAreaMapHelper.saveArea() }

        locationButton.setOnClickListener { switchLocationOnOff() }
        locateMeButton.setOnClickListener { moveToMyLocation() }
    }

    /**
     * Sets the listeners of the map
     */
    fun setMapListeners(googleMap: GoogleMap) {
        googleMap.setOnPolylineClickListener(this)
        googleMap.setOnPolygonClickListener(this)
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMarkerDragListener(this)
        googleMap.setOnInfoWindowClickListener(this)
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMapClickListener(this)
    }

    /**
     * Switches the style of the delete button for routes
     */
    fun switchIconDelete() {
        val removeRouteButton = requireView().findViewById<FloatingActionButton>(R.id.removeRoute)
        if (RouteMapHelper.deleteMode) {
            removeRouteButton.supportBackgroundTintList =
                resources.getColorStateList(R.color.red, null)
        } else {
            removeRouteButton.supportBackgroundTintList =
                resources.getColorStateList(R.color.teal_200, null)
        }
    }

    /**
     * Set the visibility of the save button for routes
     */
    fun showSaveButton() {
        val removeRouteButton = requireView().findViewById<FloatingActionButton>(R.id.saveNewRoute)
        if (RouteMapHelper.tempPolyline != null) {
            removeRouteButton.visibility = View.VISIBLE
        } else {
            removeRouteButton.visibility = View.INVISIBLE
        }
    }

    //-----------START LISTENER---------------------------------------

    override fun onPolygonClick(polygon: Polygon) =
        GoogleMapActionHandler.onPolygonClickHandler(mod, requireContext(), polygon)

    override fun onMarkerClick(marker: Marker): Boolean {
        GoogleMapActionHandler.onMarkerClickHandler(marker)

        //Return true to say that we don't want the event to go further (to the usual event when a marker is clicked)
        return true
    }

    override fun onPolylineClick(polyline: Polyline) =
        GoogleMapActionHandler.polylineClick(polyline)

    override fun onInfoWindowClick(marker: Marker) =
        GoogleMapActionHandler.onInfoWindowClickHandler(
            requireActivity(),
            this,
            marker,
            useUserLocation
        )

    override fun onMarkerDragEnd(marker: Marker) =
        GoogleMapActionHandler.interactionMarkerHandler(marker, MarkerDragMode.DRAG_END)

    override fun onMarkerDragStart(marker: Marker) =
        GoogleMapActionHandler.interactionMarkerHandler(marker, MarkerDragMode.DRAG_START)

    override fun onMarkerDrag(marker: Marker) =
        GoogleMapActionHandler.interactionMarkerHandler(marker, MarkerDragMode.DRAG)

    override fun onMyLocationButtonClick(): Boolean {
        // Return false to indicate that we did not consume the event. So the default behavior
        // still occurs (which is to move the camera to the current location.
        return false
    }

    override fun onMapClick(pos: LatLng?) =
        GoogleMapActionHandler.onMapClickHandler(pos)

    //-----------END LISTENER---------------------------------------

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
    private fun moveToMyLocation() =
        getBuiltInLocationButton().performClick()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) = HelperFunctions.onRequestPermissionsResult(requestCode, permissions, grantResults)
}