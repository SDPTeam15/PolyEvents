package com.github.sdpteam15.polyevents.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.sdpteam15.polyevents.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import kotlin.math.pow

enum class PolygonAction {
    RIGHT,
    DOWN,
    DIAG,
    MOVE,
    ROTATE
}

object GoogleMapHelper {
    var context: Context? = null

    //var map: GoogleMap? = null
    var map: MapsInterface? = null
    var uid = 1

    var editMode = false

    //Attributes that can change
    var minZoom = 17f
    var maxZoom = 21f

    var swBound = LatLng(46.519941764550545, 6.564997248351575)  // SW bounds
    var neBound = LatLng(46.5213428130699, 6.566603220999241)    // NE bounds

    var cameraPosition = LatLng(46.52010210373031, 6.566237434744834)
    var cameraZoom = 18f

    val areasPoints: MutableMap<String, Pair<Marker, Polygon>> = mutableMapOf()

    /**
     * Temporary variables when adding and editing an area
     * Markers are to access markers
     * Positions are to remember where the marker was before being moved
     * List of LatLng is to update de area after performing a modification
     * */
    var tempPoly: Polygon? = null
    var tempLatLng: MutableList<LatLng?> = ArrayList()
    var moveRightMarker: Marker? = null
    var moveDownMarker: Marker? = null
    var moveDiagMarker: Marker? = null
    var moveMarker: Marker? = null
    var rotationMarker: Marker? = null
    var moveRightPos: LatLng? = null
    var moveDownPos: LatLng? = null
    var moveDiagPos: LatLng? = null
    var rotationPos: LatLng? = null
    var movePos: LatLng? = null

    var tempTitle: String? = null
    val tempValues: MutableMap<String, Pair<String, LatLng>> = mutableMapOf()

    //----------START FUNCTIONS----------------------------------------

    /**
     * Saves the current camera position and zoom when changing fragment
     */
    fun saveCamera() {
        //Saves the last position of the camera
        cameraPosition = map!!.cameraPosition!!.target
        cameraZoom = map!!.cameraPosition!!.zoom
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
                println("second : ${v.second}")
                println("first : ${v.first}")
                addArea(k, v.second.points, v.first.title)
            }

        } else {
            //-----------Create example areas---------------
            val listEvent1 = arrayListOf<LatLng>()
            listEvent1.add(LatLng(46.52100506978624, 6.565499156713487))
            listEvent1.add(LatLng(46.52073238207864, 6.565499156713487))
            listEvent1.add(LatLng(46.52073238207864, 6.565711721777915))
            listEvent1.add(LatLng(46.52100506978624, 6.565711721777915))
            addArea("uid++.toString()", listEvent1, "Sushi Demo")

            val listEvent2 = arrayListOf<LatLng>()
            listEvent2.add(LatLng(46.52015447340308, 6.5656305849552155))
            listEvent2.add(LatLng(46.52036049105315, 6.5658414736390105))
            listEvent2.add(LatLng(46.52013394080612, 6.566103324294089))
            addArea(uid++.toString(), listEvent2, "Triangle")

            val listEvent3 = arrayListOf<LatLng>()
            listEvent3.add(LatLng(46.52111073013754, 6.565624214708805))
            listEvent3.add(LatLng(46.52107750943789, 6.565624214708805))
            listEvent3.add(LatLng(46.52108443041863, 6.566078178584576))
            listEvent3.add(LatLng(46.521115113422766, 6.5660761669278145))
            listEvent3.add(LatLng(46.521115113422766, 6.565871313214302))
            listEvent3.add(LatLng(46.52115986905187, 6.565871313214302))
            listEvent3.add(LatLng(46.52115986905187, 6.565824374556541))
            listEvent3.add(LatLng(46.521115113422766, 6.565824374556541))
            addArea(uid++.toString(), listEvent3, "La route en T")
        }
    }

    /**
     * Changes the style of the map
     */
    fun setMapStyle() {
        if(context != null){
            map!!.setMapStyle(MapStyleOptions(context!!.resources.getString(R.string.style_test3)))
        }
    }

    /**
     * Helper method to add a area to the map and generate an invisible marker in its center to display the area infos
     */
    fun addArea(id: String, coords: List<LatLng>, name: String) {
        if (coords.isNotEmpty()) {
            val poly = PolygonOptions()
            poly.addAll(coords).clickable(true)

            val polygon = map!!.addPolygon(poly)

            if(context != null){
                polygon.tag = id
            }

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
            val marker = map!!.addMarker(newMarker(center, 0f, 0f, null, name, false, R.drawable.ic_location, 0, 0, 0, 0, 1, 1))

            areasPoints[id] = Pair(marker, polygon)
        }
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
        setupEditZone(map!!.cameraPosition!!.target)
    }

    fun saveNewArea() {
        if (tempPoly != null) {
            var name = ""
            if (tempTitle != null) {
                name = tempTitle!!
            } else {
                name = "Area $uid"
                uid += 1
            }
            addArea(uid.toString(), tempPoly!!.points, name)

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
        moveRightMarker = null
        moveDownMarker = null
        moveDiagMarker = null
        moveMarker = null
        rotationMarker = null

        tempPoly = null
        moveRightPos = null
        moveDownPos = null
        moveDiagPos = null
        movePos = null
        rotationPos = null

        tempValues.clear()
        tempTitle = null
    }


    /**
     * Add a new area at the coordinates and add the markers to edit the area
     * */
    fun setupEditZone(pos: LatLng) {
        val zoom = map!!.cameraPosition!!.zoom
        val divisor = 2.0.pow(zoom.toDouble())
        val longDiff = 188.0 / divisor / 2
        val latDiff = longDiff / 2
        val pos1 = LatLng(pos.latitude + latDiff, pos.longitude - longDiff)
        val pos2 = LatLng(pos.latitude - latDiff, pos.longitude - longDiff)
        val pos3 = LatLng(pos.latitude - latDiff, pos.longitude + longDiff)
        val pos4 = LatLng(pos.latitude + latDiff, pos.longitude + longDiff)
        tempLatLng.add(pos1)
        tempLatLng.add(pos2)
        tempLatLng.add(pos3)
        tempLatLng.add(pos4)
        tempPoly = map!!.addPolygon(PolygonOptions().add(pos1).add(pos2).add(pos3).add(pos4))

        setupModifyMarkers()
    }

    fun setupModifyMarkers() {
        val pos2 = tempLatLng[1]!!
        val pos3 = tempLatLng[2]!!
        val pos4 = tempLatLng[3]!!

        val temp1 = (pos4.latitude + pos3.latitude) / 2
        val temp2 = (pos2.longitude + pos3.longitude) / 2
        val posMidRight = LatLng(temp1, pos4.longitude)
        val posMidDown = LatLng(pos2.latitude, temp2)
        val posCenter = LatLng(temp1, temp2)

        moveDiagMarker = map!!.addMarker(newMarker(pos3, 0.5f, 0.5f, PolygonAction.DIAG.toString(), null, true, R.drawable.ic_downleftarrow, 0, 0, 100, 100, 100, 100))
        moveDiagPos = moveDiagMarker!!.position

        moveRightMarker = map!!.addMarker(newMarker(posMidRight, 0.5f, 0.5f, PolygonAction.RIGHT.toString(), null, true, R.drawable.ic_rightarrow, 0, 0, 100, 100, 100, 100))
        moveRightPos = moveRightMarker!!.position

        moveDownMarker = map!!.addMarker(newMarker(posMidDown, 0.5f, 0.5f, PolygonAction.DOWN.toString(), null, true, R.drawable.ic_downarrow, 0, 0, 100, 100, 100, 100))
        moveDownPos = moveDownMarker!!.position

        moveMarker = map!!.addMarker(newMarker(posCenter, 0.5f, 0.5f, PolygonAction.MOVE.toString(), null, true, R.drawable.ic_move, 0, 0, 100, 100, 100, 100))
        movePos = moveMarker!!.position
    }

    /**
     * Sets the values for the markers
     */
    fun newMarker(pos: LatLng, hAnchor: Float, vAnchor: Float, snippet: String?, title: String?, draggable: Boolean, idDrawable: Int, leftBound: Int, topBound: Int, rightBound: Int, bottomBound: Int, width: Int, height: Int): MarkerOptions {
        var mo = MarkerOptions().position(pos).anchor(hAnchor, vAnchor).draggable(draggable).snippet(snippet).title(title)
        if (context != null) {
            mo = mo.icon(getMarkerRessource(idDrawable, leftBound, topBound, rightBound, bottomBound, width, height))
        }

        return mo
    }

    /**
     * Generates the icon for the invisible icons
     * ref : https://stackoverflow.com/questions/35718103/how-to-specify-the-size-of-the-icon-on-the-marker-in-google-maps-v2-android
     * TODO : Check if we should make it a singleton to save memory/performance
     */
    private fun getMarkerRessource(id: Int, leftBound: Int, topBound: Int, rightBound: Int, bottomBound: Int, width: Int, height: Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context!!, id)
        vectorDrawable?.setBounds(leftBound, topBound, rightBound, bottomBound)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
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
     * Projection of vector on the perpendicular of the two positions
     */
    fun projectionVector(vector: LatLng, pos1: LatLng, pos2: LatLng): LatLng {
        val v = LatLng(pos1.longitude - pos2.longitude, pos2.latitude - pos1.latitude)
        val norm = v.latitude * v.latitude + v.longitude * v.longitude
        val scalar = (vector.latitude * v.latitude + vector.longitude * v.longitude) / norm
        return LatLng(scalar * v.latitude, scalar * v.longitude)
    }

    /**
     * Transforms the size of the rectangle, either by moving the the right wall(right), the down wall(down) or both(diag)
     */
    fun transformPolygon(pos: Marker) {
        val latlng1 = tempLatLng[1]!!
        val latlng2 = tempLatLng[2]!!
        val latlng3 = tempLatLng[3]!!

        val t1 =  pos.position.latitude
        val t2 = moveDiagPos!!.latitude

        //Vector of the marker
        val vec = LatLng(
                t1 - t2,
                pos.position.longitude - moveDiagPos!!.longitude
        )

        //Perpendicular of vector (a,b) is (-b,a)

        //Projection on axis perpendicular to marker 1 and marker 2
        val diffCoord = projectionVector(vec, latlng1, latlng2)
        val diffCoord1 = projectionVector(vec, latlng2, latlng3)

        //Projection on axis perpendicular to marker 2 and marker 3

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
            PolygonAction.ROTATE.toString() -> println("ROTATION ROTATION BUTTON CLICKED")
        }
        tempPoly?.points = tempLatLng
    }

    fun editMode() {
        editMode = !editMode
        //println("EDITMODE Edit mode = $editMode")
        if (editMode) {
            for (a in areasPoints) {
                tempValues[a.key] = Pair(a.value.first.title, a.value.first.position)
                a.value.first.remove()
            }
        } else {
            restoreMarkers()
        }
    }

    fun restoreMarkers() {
        for (value in tempValues) {
            areasPoints[value.key] = Pair(map!!.addMarker(newMarker(value.value.second, 0f, 0f, null, value.value.first, false, R.drawable.ic_location, 0, 0, 0, 0, 1, 1)), areasPoints.get(value.key)!!.second)
        }
    }

    fun editArea(tag: String) {
        val area = areasPoints[tag] ?: return
        editMode = false
        tempTitle = tempValues[tag]!!.first
        tempValues.remove(tag)
        restoreMarkers()

        tempPoly = area.second
        tempLatLng = area.second.points.dropLast(1).toMutableList()

        setupModifyMarkers()
    }
}