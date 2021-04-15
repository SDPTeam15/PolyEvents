package com.github.sdpteam15.polyevents.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlin.math.pow

enum class PolygonAction {
    RIGHT,
    DOWN,
    DIAG,
    MOVE,
    ROTATE
}

@SuppressLint("StaticFieldLeak")
object GoogleMapHelper {
    var context: Context? = null
    var map: GoogleMap? = null
    var uid = 0

    //Attributes that can change
    var minZoom = 17f
    var maxZoom = 21f


    var swBound = LatLng(46.519941764550545, 6.564997248351575)  // SW bounds
    var neBound = LatLng(46.5213428130699, 6.566603220999241)    // NE bounds

    var cameraPosition = LatLng(46.52010210373031, 6.566237434744834)
    var cameraZoom = 18f

    val areasPoints: MutableMap<Int, Pair<Marker, Polygon>> = mutableMapOf()
    var coordinates: MutableMap<Int, List<LatLng>> = mutableMapOf()

    /**
     * Temporary variables when adding and editing an area
     * Markers are to access markers
     * Positions are to remember where the marker was before being moved
     * List of LatLng is to update de area after performing a modification
     * */
    private var tempPoly: Polygon? = null
    private var tempLatLng: MutableList<LatLng?> = ArrayList()
    private var moveRightMarker: Marker? = null
    private var moveDownMarker: Marker? = null
    private var moveDiagMarker: Marker? = null
    private var moveMarker: Marker? = null
    private var rotationMarker: Marker? = null
    private var moveRightPos: LatLng? = null
    private var moveDownPos: LatLng? = null
    private var moveDiagPos: LatLng? = null
    private var rotationPos: LatLng? = null
    private var movePos: LatLng? = null

    //----------START FUNCTIONS----------------------------------------

    /**
     * Saves the current camera position and zoom when changing fragment
     */
    fun saveCamera() {
        //Saves the last position of the camera
        cameraPosition = map!!.cameraPosition.target
        cameraZoom = map!!.cameraPosition.zoom
    }

    /**
     * Setup the map to the desired look
     */
    fun setUpMap() {
        //Restoring the map state
        restoreCameraState()
        restoreMapState()
        setMapStyle()
        //setBoundaries()
        //setMinAndMaxZoom()

        //To deactivate the 3d buildings
        //map!!.isBuildingsEnabled = false
    }

    /**
     * Restores the camera to the location it was before changing fragment or activity, goes to a initial position if it is the first time the map is opened
     */
    fun restoreCameraState() {
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, cameraZoom))
    }


    /**
     * Redraws all areas that were previously drawn before changing fragment or activity, draws some example
     */
    fun restoreMapState() {
        if (areasPoints.isNotEmpty()) {
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
            addArea(uid++, listEvent1, "Sushi Demo")

            val listEvent2 = arrayListOf<LatLng>()
            listEvent2.add(LatLng(46.52015447340308, 6.5656305849552155))
            listEvent2.add(LatLng(46.52036049105315, 6.5658414736390105))
            listEvent2.add(LatLng(46.52013394080612, 6.566103324294089))
            addArea(uid++, listEvent2, "Triangle")

            val listEvent3 = arrayListOf<LatLng>()
            listEvent3.add(LatLng(46.52111073013754, 6.565624214708805))
            listEvent3.add(LatLng(46.52107750943789, 6.565624214708805))
            listEvent3.add(LatLng(46.52108443041863, 6.566078178584576))
            listEvent3.add(LatLng(46.521115113422766, 6.5660761669278145))
            listEvent3.add(LatLng(46.521115113422766, 6.565871313214302))
            listEvent3.add(LatLng(46.52115986905187, 6.565871313214302))
            listEvent3.add(LatLng(46.52115986905187, 6.565824374556541))
            listEvent3.add(LatLng(46.521115113422766, 6.565824374556541))
            addArea(uid++, listEvent3, "La route en T")
        }
    }

    /**
     * Changes the style of the map
     */
    fun setMapStyle() {
        map!!.setMapStyle(MapStyleOptions(context!!.resources.getString(R.string.style_test3)))
    }

    /**
     * Helper method to add a area to the map and generate an invisible marker in its center to display the area infos
     */
    fun addArea(id: Int, coords: List<LatLng>, name: String) {
        if (!coords.isEmpty()) {
            coordinates[id] = coords

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
    fun getMarkerIcon(): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context!!, R.drawable.ic_location)
        vectorDrawable?.setBounds(0, 0, 0, 0)
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     * Sets the minimal and the maximal zoom
     */
    fun setMinAndMaxZoom() {
        map!!.setMinZoomPreference(minZoom)
        map!!.setMaxZoomPreference(maxZoom)
    }

    /**
     * Set the boundaries of the event
     */
    fun setBoundaries() {
        val bounds = LatLngBounds(swBound, neBound)

        // Constrain the camera target to the bounds.
        map!!.setLatLngBoundsForCameraTarget(bounds)
    }

    fun createNewArea() {
        clearTemp()
        setupEditZone(map!!.cameraPosition.target)
    }

    fun saveNewArea() {
        if (tempPoly != null) {
            addArea(uid, tempPoly!!.points, "Area $uid")
            uid += 1
        }
        clearTemp()
    }

    /**
     * Clears the edition markers and temporary data for area edition
     */
    fun clearTemp() {
        tempPoly?.remove()
        tempLatLng.clear()
        moveRightMarker?.remove()
        moveDownMarker?.remove()
        moveDiagMarker?.remove()
        moveMarker?.remove()
        rotationMarker?.remove()

        tempPoly = null
        moveRightPos = null
        moveDownPos = null
        moveDiagPos = null
        movePos = null
        rotationPos = null
    }

    /**
     * Add a new area at the coordinates and add the markers to edit the area
     * */
    fun setupEditZone(pos: LatLng) {
        val zoom = map!!.cameraPosition.zoom
        val divisor = 2.0.pow(zoom.toDouble())
        val longDiff = 188.0 / divisor / 2
        val latDiff = longDiff / 2
        var pos1 = LatLng(pos.latitude + latDiff, pos.longitude - longDiff)
        var pos2 = LatLng(pos.latitude - latDiff, pos.longitude - longDiff)
        var pos3 = LatLng(pos.latitude - latDiff, pos.longitude + longDiff)
        var pos4 = LatLng(pos.latitude + latDiff, pos.longitude + longDiff)
        tempLatLng.add(pos1)
        tempLatLng.add(pos2)
        tempLatLng.add(pos3)
        tempLatLng.add(pos4)
        tempPoly = map!!.addPolygon(PolygonOptions().add(pos1).add(pos2).add(pos3).add(pos4))
        val temp1 = (pos4.latitude + pos3.latitude) / 2
        val temp2 = (pos2.longitude + pos3.longitude) / 2
        val posMidRight = LatLng(temp1, pos4.longitude)
        val posMidDown = LatLng(pos2.latitude, temp2)
        val posCenter = LatLng(temp1, temp2)

        moveDiagMarker = map!!.addMarker(
            MarkerOptions().position(pos3).icon(getMarkerRessource(R.drawable.ic_downleftarrow))
                .anchor(0.5f, 0.5f).draggable(true).snippet(
                    PolygonAction.DIAG.toString()
                )
        )
        moveDiagPos = moveDiagMarker!!.position
        moveRightMarker = map!!.addMarker(
            MarkerOptions().position(posMidRight).icon(
                getMarkerRessource(
                    R.drawable.ic_rightarrow
                )
            ).anchor(0.5f, 0.5f).draggable(true).snippet(PolygonAction.RIGHT.toString())
        )
        moveRightPos = moveRightMarker!!.position
        moveDownMarker = map!!.addMarker(
            MarkerOptions().position(posMidDown).icon(
                getMarkerRessource(
                    R.drawable.ic_downarrow
                )
            ).anchor(0.5f, 0.5f).draggable(true).snippet(PolygonAction.DOWN.toString())
        )
        moveDownPos = moveDownMarker!!.position
        moveMarker = map!!.addMarker(
            MarkerOptions().position(posCenter).icon(getMarkerRessource(R.drawable.ic_move))
                .anchor(0.5f, 0.5f).draggable(true).snippet(
                    PolygonAction.MOVE.toString()
                )
        )
        movePos = moveMarker!!.position
    }

    /**
     * Generates the icon for the invisible icons
     * ref : https://stackoverflow.com/questions/35718103/how-to-specify-the-size-of-the-icon-on-the-marker-in-google-maps-v2-android
     * TODO : Check if we should make it a singleton to save memory/performance
     */
    private fun getMarkerRessource(id: Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context!!, id)
        vectorDrawable?.setBounds(0, 0, 100, 100)
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     * Translate all the corners and the edition markers from the rectangle by the same distance the "moveMarker" is moved
     */
    fun translatePolygon(pos: Marker) {
        val diffLat = pos.position.latitude - movePos!!.latitude
        val diffLng = pos.position.longitude - movePos!!.longitude
        tempLatLng = tempLatLng.map { latLng ->
            LatLng(
                latLng!!.latitude + diffLat,
                latLng.longitude + diffLng
            )
        }.toMutableList()

        //Moves the edition markers
        moveMarker!!.position = LatLng(movePos!!.latitude + diffLat, movePos!!.longitude + diffLng)
        movePos = moveMarker!!.position

        moveDiagMarker!!.position =
            LatLng(moveDiagPos!!.latitude + diffLat, moveDiagPos!!.longitude + diffLng)
        moveDiagPos = moveDiagMarker!!.position

        moveRightMarker!!.position =
            LatLng(moveRightPos!!.latitude + diffLat, moveRightPos!!.longitude + diffLng)
        moveRightPos = moveRightMarker!!.position

        moveDownMarker!!.position =
            LatLng(moveDownPos!!.latitude + diffLat, moveDownPos!!.longitude + diffLng)
        moveDownPos = moveDownMarker!!.position
    }

    /**
     * Transforms the size of the rectangle, either by moving the the right wall(right), the down wall(down) or both(diag)
     */
    fun transformPolygon(pos: Marker) {
        val latlng1 = tempLatLng[1]!!
        val latlng2 = tempLatLng[2]!!
        val latlng3 = tempLatLng[3]!!

        //Vector of the marker
        val vec = LatLng(
            pos.position.latitude - moveDiagPos!!.latitude,
            pos.position.longitude - moveDiagPos!!.longitude
        )

        //Perpendicular of vector (a,b) is (-b,a)

        //Projection on axis perpendicular to marker 1 and marker 2
        val v = LatLng(latlng1.longitude - latlng2.longitude, latlng2.latitude - latlng1.latitude)
        val norm = v.latitude * v.latitude + v.longitude * v.longitude
        val scalar = (vec.latitude * v.latitude + vec.longitude * v.longitude) / norm
        val diffCoord = LatLng(scalar * v.latitude, scalar * v.longitude)

        //Projection on axis perpendicular to marker 2 and marker 3
        val v1 = LatLng(latlng2.longitude - latlng3.longitude, latlng3.latitude - latlng2.latitude)
        val norm1 = v1.latitude * v1.latitude + v1.longitude * v1.longitude
        val scalar1 = (vec.latitude * v1.latitude + vec.longitude * v1.longitude) / norm1
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
                tempLatLng[1] = LatLng(latlng1.latitude + lat1, latlng1.longitude + lng1)
                tempLatLng[2] = pos.position
                tempLatLng[3] = LatLng(latlng3.latitude + lat2, latlng3.longitude + lng2)
            }
        }

        //Moves the edition markers
        moveDiagMarker!!.position =
            LatLng(moveDiagPos!!.latitude + lat1 + lat2, moveDiagPos!!.longitude + lng1 + lng2)
        moveDiagPos = moveDiagMarker!!.position

        moveMarker!!.position =
            LatLng(movePos!!.latitude + (lat1 + lat2) / 2, movePos!!.longitude + (lng1 + lng2) / 2)
        movePos = moveMarker!!.position

        moveRightMarker!!.position = LatLng(
            moveRightPos!!.latitude + lat1 / 2 + lat2,
            moveRightPos!!.longitude + lng2 + lng1 / 2
        )
        moveRightPos = moveRightMarker!!.position

        moveDownMarker!!.position = LatLng(
            moveDownPos!!.latitude + lat1 + lat2 / 2,
            moveDownPos!!.longitude + lng1 + lng2 / 2
        )
        moveDownPos = moveDownMarker!!.position

    }

    /**
     * Redirects an interaction with an edition marker to the correct transformation
     */
    fun interactionMarker(p0: Marker) {
        when (p0.snippet) {
            PolygonAction.MOVE.toString() -> translatePolygon(p0)
            PolygonAction.RIGHT.toString() -> transformPolygon(p0)
            PolygonAction.DOWN.toString() -> transformPolygon(p0)
            PolygonAction.DIAG.toString() -> transformPolygon(p0)
            PolygonAction.ROTATE.toString() -> Log.d("ROTATION", "ROTATION BUTTON CLICKED")
        }
        tempPoly?.points = tempLatLng
    }


    fun areasToFormattedStringLocations(
        points: MutableMap<Int, List<LatLng>> = coordinates,
        from: Int = 0,
        to: Int = points.size
    ): String {
        println("Range " + (from until to).toString())
        println("From $from")
        println("To $to")
        println("Map $points")

        var s = ""
        for (i in from until to) {
            s += areaToFormattedStringLocation(points[i])
            s += DatabaseConstant.AREAS_SEP
        }
        println(s)
        return s.substring(0, s.length - DatabaseConstant.AREAS_SEP.length)
    }

    fun areaToFormattedStringLocation(loc: List<LatLng>?): String {
        if (loc == null){
            return ""
        }
        var s = ""

        for (c in loc) {
            s += c.latitude.toString() + DatabaseConstant.LAT_LONG_SEP + c.longitude.toString() + DatabaseConstant.POINTS_SEP
        }
        return s.substring(0, s.length - DatabaseConstant.POINTS_SEP.length)
    }
    fun removeRangePolygon(from: Int, to: Int){
        for(r in (from until to)){
            areasPoints.remove(r)?.second?.remove()
            coordinates.remove(r)
        }
    }
}