package com.github.sdpteam15.polyevents.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlin.math.pow
import kotlin.math.*

enum class PolygonAction {
    RIGHT,
    DOWN,
    DIAG,
    MOVE,
    ROTATE
}

data class IconBound(var leftBound: Int, var topBound: Int, var rightBound: Int, var bottomBound: Int)
data class IconDimension(var width: Int, var height: Int)
data class IconAnchor(var anchorWidth: Float, var anchorHeight: Float)

@SuppressLint("StaticFieldLeak")
object GoogleMapHelper {

    var context: Context? = null

    //var map: GoogleMap? = null
    var map: MapsInterface? = null
    var uid = 0

    var editMode = false

    //Attributes that can change
    var minZoom = 17f
    var maxZoom = 21f
    const val EARTH_RADIUS = 6371000
    const val TOUPIE = 2 * PI
    private const val INDEX_ROTATION_MARKER = 3

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
    val tempValues: MutableMap<Int, Pair<String, LatLng>> = mutableMapOf()

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
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition,cameraZoom))
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
        if (context != null) {
            map!!.setMapStyle(MapStyleOptions(context!!.resources.getString(R.string.style_test3)))
        }
    }

    /**
     * Helper method to add a area to the map and generate an invisible marker in its center to display the area infos
     * @param id id of the area
     * @param coords coordinates coordinates of the area
     * @param name name of the area
     */
    fun addArea(id: Int, coords: List<LatLng>, name: String) {
        if (coords.isNotEmpty()) {
            coordinates[id] = coords

            val poly = PolygonOptions()
            poly.addAll(coords).clickable(true)

            val polygon = map!!.addPolygon(poly)

            if (context != null) {
                polygon.tag = id
            }

            var list = coords
            var lat = 0.0
            var lng = 0.0
            if (list.first() == list.last()) {
                list = coords.subList(0, coords.size - 1)
            }

            val anchor = IconAnchor(0f, 0f)
            val bound = IconBound(0, 0, 0, 0)
            val dimension = IconDimension(1, 1)

            val center = getCenter(list)
            val marker = map!!.addMarker(newMarker(center, anchor, null, name, false, R.drawable.ic_location, bound, dimension))

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

    /**
     * Clears the temporary variables to have a clean start for editing the area
     */
    fun createNewArea() {
        clearTemp()
        setupEditZone(map!!.cameraPosition!!.target)
    }

    /**
     * Adds an area in the map
     */
    fun saveNewArea() {
        if (tempPoly != null) {
            var name = ""
            if(tempTitle != null){
                name = tempTitle!!
            }else{
                name = "Area $uid"
                uid += 1
            }
            addArea(uid, tempPoly!!.points, name)

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
     * @param pos position of the center of the rectangle
     * */
    fun setupEditZone(pos: LatLng) {
        // Generate the corners of the area
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

    /**
     * Creates all the markers used to edit the areas
     */
    fun setupModifyMarkers() {
        val pos2 = tempLatLng[1]!!
        val pos3 = tempLatLng[2]!!
        val pos4 = tempLatLng[3]!!

        val temp1 = (pos4.latitude + pos3.latitude) / 2
        val temp2 = (pos2.longitude + pos3.longitude) / 2
        val posMidRight = LatLng(temp1, pos4.longitude)
        val posMidDown = LatLng(pos2.latitude, temp2)
        val posCenter = LatLng(temp1, temp2)

        val anchor = IconAnchor(0.5f, 0.5f)
        val bound = IconBound(0, 0, 100, 100)
        val dimension = IconDimension(100, 100)

        moveDiagMarker = map!!.addMarker(newMarker(pos3, anchor, PolygonAction.DIAG.toString(), null, true, R.drawable.ic_downleftarrow, bound, dimension))
        moveDiagPos = moveDiagMarker!!.position

        moveRightMarker = map!!.addMarker(newMarker(posMidRight, anchor, PolygonAction.RIGHT.toString(), null, true, R.drawable.ic_rightarrow,  bound, dimension))
        moveRightPos = moveRightMarker!!.position

        moveDownMarker = map!!.addMarker(newMarker(posMidDown, anchor, PolygonAction.DOWN.toString(), null, true, R.drawable.ic_downarrow,  bound, dimension))
        moveDownPos = moveDownMarker!!.position

        moveMarker = map!!.addMarker(newMarker(posCenter, anchor, PolygonAction.MOVE.toString(), null, true, R.drawable.ic_move,  bound, dimension))
        movePos = moveMarker!!.position

        rotationMarker = map!!.addMarker(newMarker(pos4, anchor, PolygonAction.ROTATE.toString(), null, true, R.drawable.ic_rotation, bound, dimension))
        rotationPos = rotationMarker!!.position
    }

    /**
     * Sets the values for the markers
     * @param pos position of the marker
     * @param anchor position of the icon with respect to the marker
     * @param snippet subtitle of the info window
     * @param title title of the info window
     * @param draggable is the marker draggable
     * @param idDrawable id of the icon
     * @param bound bounds of the icon
     * @param dim dimensions of the icon
     */
    fun newMarker(pos: LatLng, anchor: IconAnchor, snippet: String?, title: String?, draggable: Boolean, idDrawable: Int, bound: IconBound, dim:IconDimension): MarkerOptions {
        var mo = MarkerOptions().position(pos).anchor(anchor.anchorWidth, anchor.anchorHeight).draggable(draggable).snippet(snippet).title(title)
        if (context != null) {
            mo = mo.icon(getMarkerRessource(idDrawable, bound, dim))
        }

        return mo
    }

    /**
     * Generates the icon for the invisible icons
     * @param id id of the icon
     * @param bound bounds of the icon
     * @param dim dimensions of the icon
     * ref : https://stackoverflow.com/questions/35718103/how-to-specify-the-size-of-the-icon-on-the-marker-in-google-maps-v2-android
     * TODO : Check if we should make it a singleton to save memory/performance
     */
    private fun getMarkerRessource(id: Int, bound: IconBound, dim:IconDimension): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context!!, id)
        vectorDrawable?.setBounds(bound.leftBound, bound.topBound, bound.rightBound, bound.bottomBound)
        val bitmap = Bitmap.createBitmap(dim.width, dim.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     * Translate all the corners and the edition markers from the rectangle by the same distance the "moveMarker" is moved
     * @param pos marker that has been dragged : target position for the area
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

        // Moves the edition markers
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

        rotationMarker!!.position =
            LatLng(rotationPos!!.latitude + diffLat, rotationPos!!.longitude + diffLng)
        rotationPos = rotationMarker!!.position
    }

    /**
     * Projection of vector on the perpendicular of the two positions
     * @param vector vector to project
     * @param pos1 position of the first point to compute the perpendicular
     * @param pos2 position of the second point to compute the perpendicular
     */
    fun projectionVector(vector: LatLng, pos1: LatLng, pos2: LatLng): LatLng {
        val v = LatLng(pos1.longitude - pos2.longitude, pos2.latitude - pos1.latitude)
        val norm = v.latitude * v.latitude + v.longitude * v.longitude
        val scalar = (vector.latitude * v.latitude + vector.longitude * v.longitude) / norm
        return LatLng(scalar * v.latitude, scalar * v.longitude)
    }

    /**
     * Transforms the size of the rectangle, either by moving the the right wall(right), the down wall(down) or both(diag)
     * @param pos marker that has been dragged : target position for the area
     */
    fun transformPolygon(pos: Marker) {
        val latlng1 = tempLatLng[1]!!
        val latlng2 = tempLatLng[2]!!
        val latlng3 = tempLatLng[3]!!

        // Vector of the marker
        val vec = LatLng(
            pos.position.latitude - moveDiagPos!!.latitude,
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

        // Move the corresponding corners of the rectangle
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

        // Moves the edition markers
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

        rotationMarker!!.position =
            LatLng(rotationPos!!.latitude + lat2, rotationPos!!.longitude + lng2)
        rotationPos = rotationMarker!!.position
    }

    /**
     * Rotate the current polygon according to the given Marker. It also aligns this
     * marker so that it remains on the circle around the center of rotation.
     * @param pos: the Marker indicating the rotation.
     */
    fun rotatePolygon(pos: Marker) {
        // Get the center of the projection
        val center = getCenter(tempLatLng)

        val posProj = equirectangularProjection(pos.position, center)
        val oldPosProj = equirectangularProjection(rotationPos, center)

        val rotationAngle = getDirection(posProj) - getDirection(oldPosProj)
        val rotationAngleDegree = radianToDegree(rotationAngle).toFloat()

        // Rotate all the points of the polygon
        val cornersRotatedLatLng = tempLatLng.map { applyRotation(it, rotationAngle, center) }
        for (i in cornersRotatedLatLng.indices) {
            tempLatLng[i] = cornersRotatedLatLng[i]
        }

        // Move all the markers on the corresponding corner of the polygon
        rotationMarker!!.position = tempLatLng[INDEX_ROTATION_MARKER]
        rotationPos = rotationMarker!!.position

        moveDiagMarker!!.position = applyRotation(moveDiagMarker!!.position, rotationAngle, center)
        moveDiagPos = moveDiagMarker!!.position
        moveDiagMarker!!.rotation -= rotationAngleDegree

        moveMarker!!.position = applyRotation(moveMarker!!.position, rotationAngle, center)
        movePos = moveMarker!!.position

        moveRightMarker!!.position =
            applyRotation(moveRightMarker!!.position, rotationAngle, center)
        moveRightPos = moveRightMarker!!.position
        moveRightMarker!!.rotation -= rotationAngleDegree

        moveDownMarker!!.position = applyRotation(moveDownMarker!!.position, rotationAngle, center)
        moveDownPos = moveDownMarker!!.position
        moveDownMarker!!.rotation -= rotationAngleDegree
    }

    /**
     * Redirects an interaction with an edition marker to the correct transformation
     * @param marker interaction marker that has been dragged
     */
    fun interactionMarker(marker: Marker) {
        when (marker.snippet) {
            PolygonAction.MOVE.toString() -> translatePolygon(marker)
            PolygonAction.RIGHT.toString() -> transformPolygon(marker)
            PolygonAction.DOWN.toString() -> transformPolygon(marker)
            PolygonAction.DIAG.toString() -> transformPolygon(marker)
            PolygonAction.ROTATE.toString() -> rotatePolygon(marker)
        }
        tempPoly?.points = tempLatLng
    }

    /**
     * Switches the edit mode, and remove/recreates the markers for edition purpose
     */
    fun editMode() {
        editMode = !editMode
        if (editMode) {
            for (a in areasPoints) {
                tempValues[a.key] = Pair(a.value.first.title, a.value.first.position)
                a.value.first.remove()
            }
        } else {
            restoreMarkers()
        }
    }

    /**
     * Restores all markers to the area they belong
     */
    fun restoreMarkers() {
        val anchor = IconAnchor(0f, 0f)
        val bound = IconBound(0, 0, 0, 0)
        val dimension = IconDimension(1, 1)

        for (value in tempValues) {
            areasPoints[value.key] = Pair(map!!.addMarker(newMarker(value.value.second, anchor, null, value.value.first, false, R.drawable.ic_location, bound, dimension)), areasPoints.get(value.key)!!.second)
        }
    }

    /**
     * Set up the area with the tag in parameter
     * @param tag of the area to edit
     */
    fun editArea(tag: String) {
        val t = tag.toInt()
        val area = areasPoints[t] ?: return
        editMode = false
        tempTitle = tempValues[t]!!.first
        tempValues.remove(t)
        restoreMarkers()

        tempPoly = area.second
        tempLatLng = area.second.points.dropLast(1).toMutableList()

        setupModifyMarkers()
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

    /**
     * Compute the center (mean) of the given points.
     * @param list: the list of the latitude/longitude pairs of the points
     * to compute the center of.
     * @return the center of these points
     */
    fun getCenter(list: List<LatLng?>): LatLng {
        var lat = 0.0
        var lng = 0.0
        for (coord in list) {
            lat += coord!!.latitude
            lng += coord.longitude
        }

        return LatLng(lat / list.size, lng / list.size)
    }

    /**
     * Projects a point in lat/lng format onto a cartesian coordinates using the
     * equirectangular projection (approximation).
     * @param point: the point to project
     * @param center: the center of the projection
     * @return the cartesian coordinates of the points in meter wrt to the given center.
     */
    fun equirectangularProjection(point: LatLng?, center: LatLng): Pair<Double, Double> {
        val x = EARTH_RADIUS * (degreeToRadian(point!!.longitude - center.longitude)) * cos(
            degreeToRadian(center.latitude)
        )
        val y = EARTH_RADIUS * (degreeToRadian(point.latitude - center.latitude))
        return Pair(x, y)
    }

    /**
     * Convert cartesian coordinates back to lat/lng coordinates using the
     * equirectangular transformation.
     * @param point: the point to convert in cartesian coordinates
     * @param center: the center of the projection used in lat/lng coordinates
     * @return point in lat/lng coordinates
     */
    fun inverseEquirectangularProjection(point: Pair<Double, Double>, center: LatLng): LatLng {
        val lng =
            radianToDegree(point.first / (EARTH_RADIUS * cos(degreeToRadian(center.latitude)))) + center.longitude
        val lat = radianToDegree(point.second / EARTH_RADIUS) + center.latitude

        return LatLng(lat, lng)
    }

    /**
     * Convert an angle in degrees to radians
     * @param angle: angle in degrees to convert
     * @return angle in radians
     */
    fun degreeToRadian(angle: Double): Double {
        return angle * TOUPIE / 360.0
    }

    /**
     * Convert an angle in radians to degrees
     * @param angle: angle in radians to convert
     * @return angle in degrees
     */
    fun radianToDegree(angle: Double): Double {
        return angle * 360.0 / TOUPIE
    }

    /**
     * Compute the direction (angle wrt x-axis) of the given point
     * @param point: the point in cartesian coordinates to compute the direction of
     * @return direction in radians
     */
    fun getDirection(point: Pair<Double, Double>): Double {
        val direction = atan(point.second / point.first)
        return if (point.first < 0) PI + direction else direction
    }

    /**
     * Compute the rotation of the given point in cartesian coordinates by
     * the given angle
     * @param point: point in cartesian coordinates to rotate
     * @param angle: rotation angle in radians
     * @return rotated point in cartesian coordinates
     */
    fun computeRotation(point: Pair<Double, Double>, angle: Double): Pair<Double, Double> {
        val cosA = cos(angle)
        val sinA = sin(angle)

        return Pair(
            point.first * cosA - point.second * sinA,
            point.first * sinA + point.second * cosA
        )
    }

    /**
     * Compute the mean radius radius of the given points
     * forming a polygon wrt its center (implicitely).
     * @param points: the points forming the polygon
     * @return the mean radius from the center of the polygon
     */
    fun computeMeanRadius(points: List<Pair<Double, Double>>): Double {
        var runningRadius = 0.0
        points.forEach {
            runningRadius += sqrt(it.first * it.first + it.second * it.second)
        }
        return runningRadius / points.size
    }

    /**
     * Apply the whole transformation needed to rotate a point in lat/lng coordinates.
     * @param point: the point in lat/lng coordinates to rotate
     * @param angle: the angle of the rotation
     * @param center: the center of the rotation in lat/lng coordinates
     * @return the rotated point in lat/lng coordinates
     */
    fun applyRotation(point: LatLng?, angle: Double, center: LatLng): LatLng {
        val pointCartesian = equirectangularProjection(point, center)
        val rotatedCartesian = computeRotation(pointCartesian, angle)
        return inverseEquirectangularProjection(rotatedCartesian, center)
    }
}