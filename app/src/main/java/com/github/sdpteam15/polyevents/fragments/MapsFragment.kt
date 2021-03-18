package com.github.sdpteam15.polyevents.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsFragment : Fragment(), OnMapReadyCallback, OnPolylineClickListener,
    OnPolygonClickListener, OnMarkerClickListener, OnInfoWindowClickListener {

    private var map: GoogleMap? = null

    private var cameraPosition: CameraPosition? = null

    private val areasPoints: MutableMap<String, Pair<Marker, Polygon>> = mutableMapOf()

    private var locationPermissionGranted = false
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

        //Saves the last position of the camera
        cameraPosition = map!!.cameraPosition
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
        map = googleMap!!

        //Restoring the map state
        restoreCameraState()
        restoreMapState()
        setMapStyle()

        //Sample example from
        drawPolygon()
        drawPolyline()
        createMarker()
        createMarker2()
        setBoundaries()
        setMinAndMaxZoom()


        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this)
        googleMap.setOnPolygonClickListener(this)
        googleMap.setOnMarkerClickListener(this)

        //This is to get easily the coordinates from a position
        map!!.setOnMapClickListener { latLng ->
            Log.d(
                "POSITION",
                "Position : " + latLng.latitude + "," + latLng.longitude
            )
        }

        //To deactivate the 3d buildings
        //map!!.isBuildingsEnabled = false
    }

    /**
     * Restores the camera to the location it was before changing fragment or activity, goes to a initial position if it is the first time the map is opened
     */
    private fun restoreCameraState() {
        if (cameraPosition != null) {
            map!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    cameraPosition!!.target,
                    cameraPosition!!.zoom
                )
            )
        } else {
            map!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        46.52010210373031,
                        6.566237434744834
                    ), 18f
                )
            )
            changeCameraLocation()
        }
    }

    /**
     * Redraws all areas that were previously drawn before changing fragment or activity, draws some example
     */
    private fun restoreMapState() {
        if (!areasPoints.isEmpty()) {
            val temp = areasPoints.toMap()
            areasPoints.clear()
            //Draw all areas and points
            for ((k, v) in temp) {
                addArea(k, v.second.points, v.first.title)
            }

        } else {
            //-----------Create example areas---------------
            val listEvent1 = arrayListOf<LatLng>()
            listEvent1.add(LatLng(46.52100506978624, 6.565499156713487))
            listEvent1.add(LatLng(46.52073238207864, 6.565499156713487))
            listEvent1.add(LatLng(46.52073238207864, 6.565711721777915))
            listEvent1.add(LatLng(46.52100506978624, 6.565711721777915))
            addArea("1", listEvent1, "Sushi Demo")

            val listEvent2 = arrayListOf<LatLng>()
            listEvent2.add(LatLng(46.52015447340308, 6.5656305849552155))
            listEvent2.add(LatLng(46.52036049105315, 6.5658414736390105))
            listEvent2.add(LatLng(46.52013394080612, 6.566103324294089))
            addArea("2", listEvent2, "Triangle")

            val listEvent3 = arrayListOf<LatLng>()
            listEvent3.add(LatLng(46.52111073013754, 6.565624214708805))
            listEvent3.add(LatLng(46.52107750943789, 6.565624214708805))
            listEvent3.add(LatLng(46.52108443041863, 6.566078178584576))
            listEvent3.add(LatLng(46.521115113422766, 6.5660761669278145))
            listEvent3.add(LatLng(46.521115113422766, 6.565871313214302))
            listEvent3.add(LatLng(46.52115986905187, 6.565871313214302))
            listEvent3.add(LatLng(46.52115986905187, 6.565824374556541))
            listEvent3.add(LatLng(46.521115113422766, 6.565824374556541))
            addArea("3", listEvent3, "La route en T")
        }
    }

    /**
     * Changes the style of the map
     */
    private fun setMapStyle() {
        map!!.setMapStyle(MapStyleOptions(resources.getString(R.string.style_test3)))
    }

    /**
     * Helper method to add a area to the map and generate an invisible marker in its center to display the area infos
     */
    private fun addArea(id: String, coords: List<LatLng>, name: String) {
        if (!coords.isEmpty()) {
            val poly = PolygonOptions()
            poly.addAll(coords).clickable(true)

            val polygon = map!!.addPolygon(poly)
            polygon.tag = id

            var list = coords
            var lat = 0.0
            var lng = 0.0
            if (list.first() == list.last()) {
                list = coords.subList(0, coords.size - 1)
            }
            for (coord in list) {
                lat += coord.latitude
                lng += coord.longitude
            }

            val center = LatLng(lat / list.size, lng / list.size)

            val marker: Marker = map!!.addMarker(
                MarkerOptions()
                    .position(center)
                    .title(name)
                    .icon(getMarkerIcon())
            )
            areasPoints.put(id, Pair(marker, polygon))
        }
    }

    /**
     * Generates the icon for the invisible icons
     * ref : https://stackoverflow.com/questions/35718103/how-to-specify-the-size-of-the-icon-on-the-marker-in-google-maps-v2-android
     * TODO : Check if we should make it a singleton to save memory/performance
     */
    private fun getMarkerIcon(): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location)
        vectorDrawable?.setBounds(0, 0, 0, 0)
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     * Sets the minimal and the maximal zoom
     */
    private fun setMinAndMaxZoom() {
        map!!.setMinZoomPreference(18f)
        map!!.setMaxZoomPreference(20f)
    }

    /**
     * Set the boundaries of the event
     */
    private fun setBoundaries() {
        val bounds = LatLngBounds(
            LatLng(46.519941764550545, 6.564997248351575),  // SW bounds
            LatLng(46.5213428130699, 6.566603220999241) // NE bounds
        )

        // Constrain the camera target to the bounds.
        map!!.setLatLngBoundsForCameraTarget(bounds)
    }


    override fun onPolylineClick(polyline: Polyline) {
        Toast.makeText(context, "Route type " + polyline.tag.toString(), Toast.LENGTH_SHORT).show()
        //Changes the camera location and parameters
        //changeCameraLocation()
    }

    override fun onPolygonClick(polygon: Polygon) {
        areasPoints.get(polygon.tag)!!.first.showInfoWindow()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()

        //Return true to say that we don't want the event to go further (to the usual event when a marker is clicked)
        return true
    }

    override fun onInfoWindowClick(p0: Marker) {
        Toast.makeText(
            context,
            "Info Window clicked for marker" + p0.title + ", can lanch activity here",
            Toast.LENGTH_SHORT
        ).show()
    }

    //---------------SAMPLE FUNCTIONS-----------------

    /**
     * Example
     * Creates a marker in sydney
     */
    private fun createMarker() {
        val sydney = LatLng(-33.852, 151.211)
        val marker: Marker = map!!.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Salut la fraterie")
        )
        marker.showInfoWindow()
    }

    /**
     * Example
     * Creates a marker in Sydney but in an other way
     */
    private fun createMarker2() {
        map!!.apply {
            val sydney = LatLng(-33.852, 151.215)
            addMarker(MarkerOptions().position(sydney).title("Salut la fraterie2"))
        }

    }

    /**
     * Example
     * Moves the camera to a certain location with a small animation
     * Could be used after the search of an area is done to move the map to the area
     */
    private fun changeCameraLocation() {
        val epfl = LatLng(46.52010210373031, 6.566237434744834)
        val epfl2 = LatLng(46.52013001884851, 6.565851531922817)

        // Move the camera instantly to Sydney with a zoom of 18
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 18f))

        // Zoom in, animating the camera.
        map!!.animateCamera(CameraUpdateFactory.zoomIn())

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //map!!.animateCamera(CameraUpdateFactory.zoomTo(17f), 2000, null)

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        val cameraPosition = CameraPosition.Builder()
            .target(epfl2) // Sets the center of the map to Mountain View
            .zoom(20f)            // Sets the tilt of the camera to 30 degrees
            .build()              // Creates a CameraPosition from the builder
        map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    /**
     * Example
     * Adds a line in Australia
     */
    private fun drawPolyline() {
        // Style the polygon.
        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        val polyline1 = map!!.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.364, 147.891),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)
                )
        )
        polyline1.tag = "route 1"
    }

    /**
     * Example
     * Draws a polygon in Australia
     */
    private fun drawPolygon() {
        // Add polygons to indicate areas on the map.
        val polygon1 = map!!.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-27.457, 153.040),
                    LatLng(-33.852, 151.211),
                    LatLng(-37.813, 144.962),
                    LatLng(-34.928, 138.599)
                )
        )
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.tag = "beta"
    }


    //--------------------------------------------------------------

    /**
     * Asks for permission to use location
     */
    private fun getLocationPermission() {
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

    /*
    TODO : May be useful later to display position
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }
    */


}