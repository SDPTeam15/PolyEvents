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
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.GoogleMapAdapter
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelper
import com.github.sdpteam15.polyevents.model.map.MarkerDragMode
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementActivity
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementListActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

const val HEATMAP_PERIOD = 15L

class MapsFragment : Fragment(), OnMapReadyCallback, OnPolylineClickListener,
        OnPolygonClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener,
        OnMyLocationButtonClickListener, OnMapClickListener {

    companion object {
        var instance: MapsFragment? = null
    }

    private lateinit var locationButton: FloatingActionButton
    var locationPermissionGranted = false
    private var useUserLocation = false
    var zone: Zone? = null
    var onEdit: Boolean = false
    var startId = -1
    var drawHeatmap = false
    var timerHeatmap: Timer? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //if(!ZoneManagementActivity.inTest) {
        ZoneManagementListActivity.zones.observeAdd(this) {
            GoogleMapHelper.importNewZone(requireContext(), it.value)
        }

        Database.currentDatabase.zoneDatabase!!.getAllZones(
                null, 50,
                ZoneManagementListActivity.zones
        ).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to get the list of zones", requireContext())
            }
        }
        //}
        instance = this
        onEdit = zone != null

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        val addNewAreaButton: View = view.findViewById(R.id.addNewArea)
        val saveNewAreaButton: View = view.findViewById(R.id.acceptNewArea)
        val editAreaButton: View = view.findViewById(R.id.id_edit_area)
        //val deleteAreaButton: View = view.findViewById(R.id.id_delete_areas)

        addNewAreaButton.setOnClickListener { GoogleMapHelper.createNewArea(requireContext()) }
        saveNewAreaButton.setOnClickListener { GoogleMapHelper.saveNewArea(requireContext()) }
        editAreaButton.setOnClickListener { GoogleMapHelper.editMode(requireContext()) }
        //deleteAreaButton.setOnClickListener{GoogleMapHelper.deleteMode(requireContext())}

        val addNewRouteButton = view.findViewById<FloatingActionButton>(R.id.addNewRoute)
        val removeRouteButton = view.findViewById<FloatingActionButton>(R.id.removeRoute)
        val saveNewRouteButton = view.findViewById<FloatingActionButton>(R.id.saveNewRoute)
        addNewRouteButton.setOnClickListener { RouteMapHelper.createNewRoute(requireContext()) }
        removeRouteButton.setOnClickListener { RouteMapHelper.removeRoute(requireContext()) }
        saveNewRouteButton.setOnClickListener { RouteMapHelper.saveNewRoute() }

        locationButton = view.findViewById(R.id.id_location_button)
        val locateMeButton = view.findViewById<FloatingActionButton>(R.id.id_locate_me_button)
        val saveButton = view.findViewById<FloatingActionButton>(R.id.saveAreas)
        val heatmapButton = view.findViewById<FloatingActionButton>(R.id.id_heatmap)

        if (onEdit) {
            addNewAreaButton.visibility = View.VISIBLE
            saveNewAreaButton.visibility = View.VISIBLE
            editAreaButton.visibility = View.VISIBLE
            //deleteAreaButton.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE
            locationButton.visibility = View.INVISIBLE
            locateMeButton.visibility = View.INVISIBLE
            heatmapButton.visibility = View.INVISIBLE
            addNewRouteButton.visibility = View.INVISIBLE
            removeRouteButton.visibility = View.INVISIBLE
            saveNewRouteButton.visibility = View.INVISIBLE

            saveButton.setOnClickListener {
                GoogleMapHelper.editMode = false
                GoogleMapHelper.clearTemp()
                //TODO : Save the areas in the map
                //val location = GoogleMapHelper.areasToFormattedStringLocations(from = startId)
                val location =
                        GoogleMapHelper.zoneAreasToFormattedStringLocation(GoogleMapHelper.editingZone!!)
                zone!!.location = location
                ZoneManagementActivity.zoneObservable.postValue(
                        Zone(
                                zoneName = zone!!.zoneName,
                                zoneId = zone!!.zoneId,
                                location = location,
                                description = zone!!.description
                        )
                )
            }
        } else {
            addNewAreaButton.visibility = View.INVISIBLE
            saveNewAreaButton.visibility = View.INVISIBLE
            editAreaButton.visibility = View.INVISIBLE
            //deleteAreaButton.visibility = View.INVISIBLE
            saveButton.visibility = View.INVISIBLE
            locationButton.visibility = View.VISIBLE
            locateMeButton.visibility = View.VISIBLE
            heatmapButton.visibility = View.VISIBLE
            addNewRouteButton.visibility = View.VISIBLE
            removeRouteButton.visibility = View.VISIBLE
            saveNewRouteButton.visibility = View.INVISIBLE
        }

        heatmapButton.setOnClickListener {
            drawHeatmap = !drawHeatmap
            if (drawHeatmap) {
                timerHeatmap = Timer("SettingUp", false)
                val task = object : TimerTask() {
                    override fun run() {
                        val locations = ObservableList<LatLng>()
                        Database.currentDatabase.heatmapDatabase!!.getLocations(locations)
                        locations.observeOnce {
                            GoogleMapHelper.addHeatMap(it.value)
                        }
                    }
                }
                timerHeatmap?.schedule(task, 0, HEATMAP_PERIOD * 1000)
            } else {
                GoogleMapHelper.lastOverlay?.remove()
                timerHeatmap?.cancel()
                timerHeatmap = null
            }
        }

        locationButton.setOnClickListener {
            switchLocationOnOff()
        }

        locationButton.tag = R.drawable.ic_location_on


        locateMeButton.setOnClickListener {
            moveToMyLocation()
        }

        locateMeButton.tag = R.drawable.ic_locate_me

        return view
    }

    override fun onPause() {
        super.onPause()
        GoogleMapHelper.saveCamera()
        timerHeatmap?.cancel()
        drawHeatmap = false
        GoogleMapHelper.lastOverlay?.remove()
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
        //GoogleMapHelper.context = context
        GoogleMapHelper.map = GoogleMapAdapter(googleMap)
        RouteMapHelper.map = GoogleMapAdapter(googleMap)
        RouteMapHelper.getNodesAndEdgesFromDB(context, this)

        googleMap!!.setOnPolylineClickListener(this)
        googleMap.setOnPolygonClickListener(this)
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMarkerDragListener(this)
        googleMap.setOnInfoWindowClickListener(this)
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMapClickListener(this)
        GoogleMapHelper.setUpMap(requireContext())



        if (useUserLocation) {
            activateMyLocation()
        }
        startId = GoogleMapHelper.uidArea
    }

    override fun onPolylineClick(polyline: Polyline) {
        RouteMapHelper.polylineClick(polyline)
    }

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

    fun showSaveButton() {
        val removeRouteButton = requireView().findViewById<FloatingActionButton>(R.id.saveNewRoute)
        if (RouteMapHelper.tempPolyline != null) {
            removeRouteButton.visibility = View.VISIBLE
        } else {
            removeRouteButton.visibility = View.INVISIBLE
        }
    }

    override fun onPolygonClick(polygon: Polygon) {
        if (onEdit) {
            if (GoogleMapHelper.editMode && GoogleMapHelper.canEdit(polygon.tag.toString())) {
                GoogleMapHelper.editArea(requireContext(), polygon.tag.toString())
            } else if (GoogleMapHelper.deleteMode && GoogleMapHelper.canEdit(polygon.tag.toString())) {
                GoogleMapHelper.removeArea(polygon.tag.toString().toInt())
            }
        } else {
            GoogleMapHelper.setSelectedZoneFromArea(polygon.tag.toString())
            //Shows the info window of the marker assigned to the area
            GoogleMapHelper.areasPoints.get(polygon.tag)!!.second.showInfoWindow()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (!GoogleMapHelper.editMode) {
            marker.showInfoWindow()
        }
        val tag = marker.tag
        if (tag != null) {
            GoogleMapHelper.setSelectedZones(tag.toString())
        }
        marker.showInfoWindow()

        //Return true to say that we don't want the event to go further (to the usual event when a marker is clicked)
        return true
    }

    override fun onInfoWindowClick(p0: Marker) {

        HelperFunctions.getLoc(requireActivity()).observeOnce(this) {
            RouteMapHelper.chemin =
                    RouteMapHelper.getShortestPath(it.value!!, p0.tag.toString())?.toMutableList()
                            ?: mutableListOf()
            RouteMapHelper.drawRoute()
        }

    }

    override fun onMarkerDragEnd(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0, MarkerDragMode.DRAG_END)
    }

    override fun onMarkerDragStart(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0, MarkerDragMode.DRAG_START)
    }


    override fun onMarkerDrag(p0: Marker) {
        GoogleMapHelper.interactionMarker(p0, MarkerDragMode.DRAG)
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

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) = HelperFunctions.onRequestPermissionsResult(requestCode, permissions, grantResults)

    override fun onMapClick(p0: LatLng?) {
        GoogleMapHelper.clearSelectedZone()
    }
}