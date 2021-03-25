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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlin.math.pow
import kotlin.math.sqrt

enum class PolygonAction{
    RIGHT,
    DOWN,
    DIAG,
    MOVE,
    ROTATE
}

class MapsFragment : Fragment(), OnMapReadyCallback, OnPolylineClickListener,
    OnPolygonClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMapLongClickListener, OnMarkerDragListener {

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

        setUpMap()
    }

    override fun onPolylineClick(polyline: Polyline) {

    }

    override fun onPolygonClick(polygon: Polygon) {
        //Shows the info window of the marker assigned to the area
        areasPoints.get(polygon.tag)!!.first.showInfoWindow()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()

        //Return true to say that we don't want the event to go further (to the usual event when a marker is clicked)
        return true
    }

    override fun onInfoWindowClick(p0: Marker) {
        HelperFunctions.showToast("Info Window clicked for marker" + p0.title + ", can lanch activity here", requireContext())
    }


    override fun onMapLongClick(pos: LatLng) {
        clearTemp()
        setupEditZone(pos)
    }

    override fun onMarkerDragEnd(p0: Marker) {
        interactionMarker(p0)
    }

    override fun onMarkerDragStart(p0: Marker) {
        interactionMarker(p0)
    }


    override fun onMarkerDrag(p0: Marker) {
        interactionMarker(p0)
    }
    //-----------END LISTENER---------------------------------------

    //----------START FUNCTIONS----------------------------------------

    private fun setUpMap(){
        //Restoring the map state
        restoreCameraState()
        restoreMapState()
        setMapStyle()
        //setBoundaries()
        //setMinAndMaxZoom()

        //Sample example from
        //drawPolygon()
        //drawPolyline()
        //createMarker()
        //createMarker2()

        // Set listeners for click events.
        map!!.setOnPolylineClickListener(this)
        map!!.setOnPolygonClickListener(this)
        map!!.setOnMarkerClickListener(this)
        map!!.setOnMapLongClickListener(this)
        map!!.setOnMarkerDragListener(this)
        map!!.setOnInfoWindowClickListener(this)
        //This is to get easily the coordinates from a position
        //map!!.setOnMapClickListener { latLng ->Log.d("POSITION","Position : " + latLng.latitude + "," + latLng.longitude + ", zoom lvl : " + map!!.cameraPosition.zoom)}

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
            areasPoints[id] = Pair(marker, polygon)
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
        map!!.setMinZoomPreference(10f)
        map!!.setMaxZoomPreference(22f)
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

    /**
     * Temporary variables when adding and editing an area
     * Markers are to access markers
     * Positions are to remember where the marker was before being moved
     * List of LatLng is to update de area after performing a modification
     * */
    private var tempPoly:Polygon? = null
    private var tempLatLng: MutableList<LatLng?> = ArrayList()
    private var moveRightMarker:Marker? = null
    private var moveDownMarker:Marker? = null
    private var moveDiagMarker:Marker? = null
    private var moveMarker:Marker? = null
    private var rotationMarker:Marker? = null
    private var moveRightPos:LatLng? = null
    private var moveDownPos:LatLng? = null
    private var moveDiagPos:LatLng? = null
    private var rotationPos:LatLng? = null
    private var movePos:LatLng? = null


    /**
     * Add a new area at the coordinates and add the markers to edit the area
     * */
    private fun setupEditZone(pos: LatLng){
        val zoom = map!!.cameraPosition.zoom
        val divisor = 2.0.pow(zoom.toDouble())
        val longDiff = 188.0/divisor
        val latDiff = longDiff/2

        var pos2 = LatLng(pos.latitude - latDiff, pos.longitude)
        var pos3 = LatLng(pos.latitude - latDiff, pos.longitude + longDiff)
        var pos4 = LatLng(pos.latitude, pos.longitude + longDiff)
        tempLatLng.add(pos)
        tempLatLng.add(pos2)
        tempLatLng.add(pos3)
        tempLatLng.add(pos4)
        tempPoly = map!!.addPolygon(PolygonOptions().add(pos).add(pos2).add(pos3).add(pos4))
        val temp1 = (pos4.latitude + pos3.latitude)/2
        val temp2 = (pos2.longitude + pos3.longitude)/2
        val posMidRight = LatLng(temp1, pos4.longitude)
        val posMidDown = LatLng(pos2.latitude, temp2)
        val posCenter = LatLng(temp1, temp2)

        moveDiagMarker = map!!.addMarker(MarkerOptions().position(pos3).icon(getMarkerRessource(R.drawable.ic_downleftarrow)).anchor(0.5f,0.5f).draggable(true).snippet(PolygonAction.DIAG.toString()))
        moveDiagPos = moveDiagMarker!!.position
        moveRightMarker = map!!.addMarker(MarkerOptions().position(posMidRight).icon(getMarkerRessource(R.drawable.ic_rightarrow)).anchor(0.5f,0.5f).draggable(true).snippet(PolygonAction.RIGHT.toString()))
        moveRightPos = moveRightMarker!!.position
        moveDownMarker = map!!.addMarker(MarkerOptions().position(posMidDown).icon(getMarkerRessource(R.drawable.ic_downarrow)).anchor(0.5f,0.5f).draggable(true).snippet(PolygonAction.DOWN.toString()))
        moveDownPos = moveDownMarker!!.position
        moveMarker = map!!.addMarker(MarkerOptions().position(posCenter).icon(getMarkerRessource(R.drawable.ic_move)).anchor(0.5f,0.5f).draggable(true).snippet(PolygonAction.MOVE.toString()))
        movePos = moveMarker!!.position
        //TODO
        //rotationMarker = map!!.addMarker(MarkerOptions().position(pos4).icon(getMarkerRessource(R.drawable.ic_rotation)).anchor(0.5f,0.5f).draggable(true).snippet(PolygonAction.ROTATE.toString()))
        //rotationPos = rotationMarker!!.position
    }

    /**
     * Generates the icon for the invisible icons
     * ref : https://stackoverflow.com/questions/35718103/how-to-specify-the-size-of-the-icon-on-the-marker-in-google-maps-v2-android
     * TODO : Check if we should make it a singleton to save memory/performance
     */
    private fun getMarkerRessource(id: Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), id)
        vectorDrawable?.setBounds(0, 0, 100, 100)
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     * Clears the edition markers and temporary data for area edition
     */
    private fun clearTemp(){
        tempPoly = null
        tempLatLng.clear()
        moveRightMarker?.remove()
        moveDownMarker?.remove()
        moveDiagMarker?.remove()
        moveMarker?.remove()
        rotationMarker?.remove()

        moveRightPos = null
        moveDownPos = null
        moveDiagPos = null
        movePos = null
        rotationPos = null
    }

    /**
     * Translate all the corners and the edition markers from the rectangle by the same distance the "moveMarker" is moved
     */
    private fun translatePolygon(pos: Marker){
        val diffLat = pos.position.latitude - movePos!!.latitude
        val diffLng = pos.position.longitude - movePos!!.longitude
        tempLatLng = tempLatLng.map { latLng -> LatLng(latLng!!.latitude + diffLat,latLng.longitude + diffLng) }.toMutableList()

        //Moves the edition markers
        moveMarker!!.position = LatLng(movePos!!.latitude + diffLat, movePos!!.longitude + diffLng)
        movePos = moveMarker!!.position

        moveDiagMarker!!.position = LatLng(moveDiagPos!!.latitude + diffLat, moveDiagPos!!.longitude + diffLng)
        moveDiagPos = moveDiagMarker!!.position

        moveRightMarker!!.position = LatLng(moveRightPos!!.latitude + diffLat, moveRightPos!!.longitude + diffLng)
        moveRightPos = moveRightMarker!!.position

        moveDownMarker!!.position = LatLng(moveDownPos!!.latitude + diffLat, moveDownPos!!.longitude + diffLng)
        moveDownPos = moveDownMarker!!.position
        //TODO
        //rotationMarker!!.position = LatLng(rotationPos!!.latitude + diffLat, rotationPos!!.longitude + diffLng)
        //rotationPos = rotationMarker!!.position
    }

    /**
     * Transforms the size of the rectangle, either by moving the the right wall(right), the down wall(down) or both(diag)
     */
    private fun transformPolygon(pos:Marker){
        val latlng1 = tempLatLng[1]!!
        val latlng2 = tempLatLng[2]!!
        val latlng3 = tempLatLng[3]!!

        //Vector of the marker
        val vec = LatLng(pos.position.latitude - moveDiagPos!!.latitude, pos.position.longitude - moveDiagPos!!.longitude)

        //Perpendicular of vector (a,b) is (-b,a)

        //Projection on axis perpendicular to marker 1 and marker 2
        val v = LatLng(latlng1.longitude - latlng2.longitude, latlng2.latitude-latlng1.latitude)
        val norm = v.latitude*v.latitude + v.longitude*v.longitude
        val scalar = (vec.latitude*v.latitude + vec.longitude*v.longitude) / norm
        val diffCoord = LatLng(scalar * v.latitude, scalar * v.longitude)

        //Projection on axis perpendicular to marker 2 and marker 3
        val v1 = LatLng(latlng2.longitude - latlng3.longitude, latlng3.latitude-latlng2.latitude)
        val norm1 = v1.latitude*v1.latitude + v1.longitude*v1.longitude
        val scalar1 = (vec.latitude*v1.latitude + vec.longitude*v1.longitude) / norm1
        val diffCoord1 = LatLng(scalar1 * v1.latitude, scalar1 * v1.longitude)

        var lat1 = diffCoord.latitude
        var lng1 = diffCoord.longitude
        var lat2 = diffCoord1.latitude
        var lng2 = diffCoord1.longitude

        //Move the corresponding corners of the rectangle
        when (pos.snippet) {
            PolygonAction.RIGHT.toString() -> {
                lat1 = 0.0
                lng1 = 0.0
                tempLatLng[2] = LatLng(latlng2.latitude + lat2, latlng2.longitude + lng2)
                tempLatLng[3] = LatLng(latlng3.latitude + lat2, latlng3.longitude + lng2)
            }
            PolygonAction.DOWN.toString() -> {
                lat2 = 0.0
                lng2 = 0.0
                tempLatLng[1] = LatLng(latlng1.latitude + lat1, latlng1.longitude + lng1)
                tempLatLng[2] = LatLng(latlng2.latitude + lat1, latlng2.longitude + lng1)
            }
            else -> { //Should only be DIAG
                tempLatLng[1] = LatLng(latlng1.latitude + lat1,latlng1.longitude + lng1)
                tempLatLng[2] = pos.position
                tempLatLng[3] = LatLng(latlng3.latitude + lat2,latlng3.longitude + lng2)
            }
        }

        //Moves the edition markers
        moveDiagMarker!!.position = LatLng(moveDiagPos!!.latitude + lat1 + lat2, moveDiagPos!!.longitude + lng1 + lng2)
        moveDiagPos = moveDiagMarker!!.position

        moveMarker!!.position = LatLng(movePos!!.latitude + (lat1 + lat2)/2,movePos!!.longitude + (lng1 + lng2)/2)
        movePos = moveMarker!!.position

        moveRightMarker!!.position = LatLng(moveRightPos!!.latitude + lat1/2 + lat2, moveRightPos!!.longitude + lng2 + lng1/2)
        moveRightPos = moveRightMarker!!.position

        moveDownMarker!!.position = LatLng(moveDownPos!!.latitude + lat1 + lat2/2,moveDownPos!!.longitude + lng1 + lng2/2)
        moveDownPos = moveDownMarker!!.position

        //TODO
        //rotationMarker!!.position = LatLng(rotationPos!!.latitude + lat2, rotationPos!!.longitude + lng2)
        //rotationPos = rotationMarker!!.position
    }

    /*
    /**
     * TODO: Draw a circle but constraint the positions on meters and not difference in LatLng (it doesn't draw a circle since latlng is not a cartesian space)
     * Performs a rotation on the rectangle
     */
    private fun rotatePolygon(pos:Marker){

        val corner = tempLatLng[3]!!
        val rayonVector = LatLng(rotationPos!!.latitude - movePos!!.latitude, rotationPos!!.longitude - movePos!!.longitude)
        val rayonVector2 = LatLng(corner.latitude - movePos!!.latitude, corner.longitude - movePos!!.longitude)
        val r = sqrt(rayonVector.latitude*rayonVector.latitude + rayonVector.longitude*rayonVector.longitude)
        val r2 = sqrt(rayonVector2.latitude*rayonVector2.latitude + rayonVector2.longitude*rayonVector2.longitude)
        map!!.addCircle(CircleOptions().center(movePos).radius(r2))

        val vector = LatLng(pos.position.latitude - movePos!!.latitude, pos.position.longitude - movePos!!.longitude)
        val norm = sqrt(vector.latitude*vector.latitude + vector.longitude*vector.longitude)
        val v = LatLng(vector.latitude*r/norm, vector.longitude*r/norm)
        Log.d("AFTER", "Vector : $vector; norm : $norm; final vector : $v")
        Log.d("RAYON EQUAL", "r1=" + r + ", r2 = " + sqrt(v.longitude*v.longitude + v.latitude*v.latitude))

        rotationMarker!!.position = LatLng(movePos!!.latitude + v.latitude, movePos!!.longitude + v.longitude)
        rotationPos = rotationMarker!!.position
        //map!!.addMarker(MarkerOptions().position(rotationPos!!))
        //val circle = map!!.addCircle(CircleOptions().center(pos.position).radius(10.0).strokeColor(Color.RED).fillColor(Color.BLUE))
    }
    */

    /**
     * Redirects an interaction with an edition marker to the correct transformation
     */
    private fun interactionMarker(p0:Marker){
        when(p0.snippet){
            PolygonAction.MOVE.toString() -> translatePolygon(p0)
            PolygonAction.RIGHT.toString() -> transformPolygon(p0)
            PolygonAction.DOWN.toString() -> transformPolygon(p0)
            PolygonAction.DIAG.toString() -> transformPolygon(p0)
            //PolygonAction.ROTATE.toString() -> rotatePolygon(p0) TODO
            PolygonAction.ROTATE.toString() -> Log.d("ROTATION", "ROTATION BUTTON CLICKED")
        }
        tempPoly?.points = tempLatLng
    }

    //------------END FUNCTIONS------------------------

    //-------------START PLAYGROUND-------------------------

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