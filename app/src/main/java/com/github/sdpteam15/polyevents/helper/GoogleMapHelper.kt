package com.github.sdpteam15.polyevents.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.Zone
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import kotlin.math.*

enum class PolygonAction {
    RIGHT,
    DOWN,
    DIAG,
    MOVE,
    ROTATE
}

data class IconBound(
    var leftBound: Int,
    var topBound: Int,
    var rightBound: Int,
    var bottomBound: Int
)

data class IconDimension(var width: Int, var height: Int)
data class IconAnchor(var anchorWidth: Float, var anchorHeight: Float)

@SuppressLint("StaticFieldLeak")
object GoogleMapHelper {
    var map: MapsInterface? = null
    var uidArea = 0
    var uidZone = 0

    var editMode = false
    var editingZone = -1
    var selectedZone = -1

    //Attributes that can change
    var minZoom = 17f
    var maxZoom = 21f
    const val EARTH_RADIUS = 6371000
    const val TOUPIE = 2 * PI
    private const val INDEX_ROTATION_MARKER = 3
    private const val DEFAULT_ZONE_STROKE_COLOR = Color.BLACK
    private const val SELECTED_ZONE_STROKE_COLOR = Color.BLUE
    private const val EDITED_ZONE_STROKE_COLOR = Color.GREEN

    var swBound = LatLng(46.519941764550545, 6.564997248351575)  // SW bounds
    var neBound = LatLng(46.5213428130699, 6.566603220999241)    // NE bounds

    var cameraPosition = LatLng(46.52010210373031, 6.566237434744834)
    var cameraZoom = 18f
    val areasPoints: MutableMap<Int, Triple<Int, Marker, Polygon>> = mutableMapOf()
    val zonesToArea: MutableMap<Int, Pair<Zone?, MutableList<Int>>> = mutableMapOf()

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
    var modifyingArea: Int = -1
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
    fun setUpMap(context: Context?) {
        //Restoring the map state
        restoreCameraState()
        restoreMapState(context)
        setMapStyle(context)
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
    fun restoreMapState(context: Context?) {
        if (areasPoints.isNotEmpty()) {
            val currentEditZone = editingZone
            val areaTemp = areasPoints.toMap()
            val zoneTemp = zonesToArea.toMap()
            zonesToArea.clear()
            areasPoints.clear()
            for ((k, v) in zoneTemp) {
                editingZone = k
                zonesToArea[editingZone] = Pair(null, mutableListOf())
                for (id in v.second) {
                    addArea(context, id, areaTemp[id]!!.third.points, areaTemp[id]!!.second.title)
                }
            }

            editingZone = currentEditZone

        } else {
            val currentEditZone = editingZone

            //-----------Create example areas---------------
            val zoneID1 = uidZone++
            editingZone = zoneID1
            zonesToArea[zoneID1] = Pair(null, mutableListOf())
            val listEvent1 = arrayListOf<LatLng>()
            listEvent1.add(LatLng(46.52100506978624, 6.565499156713487))
            listEvent1.add(LatLng(46.52073238207864, 6.565499156713487))
            listEvent1.add(LatLng(46.52073238207864, 6.565711721777915))
            listEvent1.add(LatLng(46.52100506978624, 6.565711721777915))
            addArea(context, uidArea++, listEvent1, "Sushi Demo")
            val zone1 =
                Zone(null, "Salle 1", zoneAreasToFormattedStringLocation(editingZone), "Salle 1")
            setZone(zone1)

            val zoneID2 = uidZone++
            editingZone = zoneID2
            zonesToArea[zoneID2] = Pair(null, mutableListOf())
            val listEvent3 = arrayListOf<LatLng>()
            listEvent3.add(LatLng(46.52111073013754, 6.565624214708805))
            listEvent3.add(LatLng(46.52107750943789, 6.565624214708805))
            listEvent3.add(LatLng(46.52108443041863, 6.566078178584576))
            listEvent3.add(LatLng(46.521115113422766, 6.5660761669278145))
            addArea(context, uidArea++, listEvent3, "La route en T")
            val listEvent4 = arrayListOf<LatLng>()
            listEvent4.add(LatLng(46.52115986905187, 6.565824374556541))
            listEvent4.add(LatLng(46.521115113422766, 6.565824374556541))
            listEvent4.add(LatLng(46.521115113422766, 6.565871313214302))
            listEvent4.add(LatLng(46.52115986905187, 6.565871313214302))
            addArea(context, uidArea++, listEvent4, "La route en T2")
            val zoneEnT = Zone(
                null,
                "Route en T",
                zoneAreasToFormattedStringLocation(editingZone),
                "La route en T"
            )
            setZone(zoneEnT)

            editingZone = currentEditZone
        }
    }

    /**
     * Import zone from database
     * @param zone zone to import
     */
    fun importNewZone(context: Context?, zone: Zone) {
        zonesToArea[uidZone++] = Pair(zone, mutableListOf())
        for (areas in zone.getZoneCoordinates()) {
            addArea(context, uidArea++, areas, zone.zoneName)
        }
    }

    /**
     * Set the texts to the areas of the zone
     * @param zone new zone created
     */
    fun setZone(zone: Zone) {
        val list = zonesToArea[editingZone]!!.second.toMutableList()
        for (l in list) {
            areasPoints[l]!!.second.title = zone.zoneName
        }
        zonesToArea[editingZone] = Pair(zone, list)
        editingZone = -1
    }

    /**
     * Changes the style of the map
     */
    fun setMapStyle(context: Context?) {
        if (context != null) {
            map!!.setMapStyle(MapStyleOptions(context.resources.getString(R.string.style_test3)))
        }
    }

    /**
     * Helper method to add a area to the map and generate an invisible marker in its center to display the area infos
     * @param id id of the area
     * @param coords coordinates coordinates of the area
     * @param name name of the area
     */
    fun addArea(context: Context?, id: Int, coords: List<LatLng>, name: String?) {
        if (coords.isNotEmpty()) {
            val poly = PolygonOptions()
            poly.addAll(coords).clickable(true)

            val polygon = map!!.addPolygon(poly)

            var list = coords
            if (list.first() == list.last()) {
                list = coords.subList(0, coords.size - 1)
            }

            val anchor = IconAnchor(0f, 0f)
            val bound = IconBound(0, 0, 0, 0)
            val dimension = IconDimension(1, 1)

            val center = getCenter(list)
            val marker = map!!.addMarker(
                newMarker(
                    context,
                    center,
                    anchor,
                    null,
                    name,
                    false,
                    R.drawable.ic_location,
                    bound,
                    dimension
                )
            )

            //.tag of these objects are not mockable
            if (context != null) {
                polygon.tag = id
                marker.tag = editingZone
            }

            areasPoints[id] = Triple(editingZone, marker, polygon)
            if (!zonesToArea[editingZone]!!.second.contains(id)) {
                zonesToArea[editingZone]!!.second.add(id)
            }
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
    fun createNewArea(context: Context?) {
        clearTemp()
        setupEditZone(context, map!!.cameraPosition!!.target)
    }

    /**
     * Adds an area in the map
     */
    fun saveNewArea(context: Context?) {
        if (tempPoly != null) {
            var name = ""
            var id = -1
            if (tempTitle != null) {
                name = tempTitle!!
                id = modifyingArea
            } else {
                id = uidArea++
                name = "Area $id"

            }
            addArea(context, id, tempPoly!!.points, name)
            colorAreas(editingZone, EDITED_ZONE_STROKE_COLOR)
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
    fun setupEditZone(context: Context?, pos: LatLng) {
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
        tempPoly = map!!.addPolygon(
            PolygonOptions().add(pos1).add(pos2).add(pos3).add(pos4).strokeColor(Color.RED)
        )

        setupModifyMarkers(context)
    }

    /**
     * Creates all the markers used to edit the areas
     */
    fun setupModifyMarkers(context: Context?) {
        val pos2 = tempLatLng[1]!!
        val pos3 = tempLatLng[2]!!
        val pos4 = tempLatLng[3]!!

        val posMidRight =
            LatLng(0.5 * (pos3.latitude + pos4.latitude), 0.5 * (pos3.longitude + pos4.longitude))
        val posMidDown =
            LatLng(0.5 * (pos3.latitude + pos2.latitude), 0.5 * (pos3.longitude + pos2.longitude))
        val posCenter =
            LatLng(0.5 * (pos4.latitude + pos2.latitude), 0.5 * (pos4.longitude + pos2.longitude))

        val anchor = IconAnchor(0.5f, 0.5f)
        val bound = IconBound(0, 0, 100, 100)
        val dimension = IconDimension(100, 100)

        moveDiagMarker = map!!.addMarker(
            newMarker(
                context,
                pos3,
                anchor,
                PolygonAction.DIAG.toString(),
                null,
                true,
                R.drawable.ic_downleftarrow,
                bound,
                dimension
            )
        )
        moveDiagPos = moveDiagMarker!!.position

        moveRightMarker = map!!.addMarker(
            newMarker(
                context,
                posMidRight,
                anchor,
                PolygonAction.RIGHT.toString(),
                null,
                true,
                R.drawable.ic_rightarrow,
                bound,
                dimension
            )
        )
        moveRightPos = moveRightMarker!!.position

        moveDownMarker = map!!.addMarker(
            newMarker(
                context,
                posMidDown,
                anchor,
                PolygonAction.DOWN.toString(),
                null,
                true,
                R.drawable.ic_downarrow,
                bound,
                dimension
            )
        )
        moveDownPos = moveDownMarker!!.position

        moveMarker = map!!.addMarker(
            newMarker(
                context,
                posCenter,
                anchor,
                PolygonAction.MOVE.toString(),
                null,
                true,
                R.drawable.ic_move,
                bound,
                dimension
            )
        )
        movePos = moveMarker!!.position

        rotationMarker = map!!.addMarker(
            newMarker(
                context,
                pos4,
                anchor,
                PolygonAction.ROTATE.toString(),
                null,
                true,
                R.drawable.ic_rotation,
                bound,
                dimension
            )
        )
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
    fun newMarker(
        context: Context?,
        pos: LatLng,
        anchor: IconAnchor,
        snippet: String?,
        title: String?,
        draggable: Boolean,
        idDrawable: Int,
        bound: IconBound,
        dim: IconDimension
    ): MarkerOptions {
        var mo = MarkerOptions().position(pos).anchor(anchor.anchorWidth, anchor.anchorHeight)
            .draggable(draggable).snippet(snippet).title(title)
        if (context != null) {
            mo = mo.icon(getMarkerRessource(context, idDrawable, bound, dim))
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
    private fun getMarkerRessource(
        context: Context?,
        id: Int,
        bound: IconBound,
        dim: IconDimension
    ): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context!!, id)
        vectorDrawable?.setBounds(
            bound.leftBound,
            bound.topBound,
            bound.rightBound,
            bound.bottomBound
        )
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
    fun editMode(context: Context?) {
        editMode = !editMode
        if (editMode) {
            for (a in areasPoints) {
                tempValues[a.key] = Pair(a.value.second.title, a.value.second.position)
                a.value.second.remove()
            }
            colorAreas(editingZone, EDITED_ZONE_STROKE_COLOR)
        } else {
            colorAreas(editingZone, DEFAULT_ZONE_STROKE_COLOR)
            restoreMarkers(context)
        }
    }

    /**
     * Restores all markers to the area they belong
     */
    fun restoreMarkers(context: Context?) {
        val anchor = IconAnchor(0f, 0f)
        val bound = IconBound(0, 0, 0, 0)
        val dimension = IconDimension(1, 1)

        for (value in tempValues) {
            areasPoints[value.key] = Triple(
                areasPoints.get(value.key)!!.first,
                map!!.addMarker(
                    newMarker(
                        context,
                        value.value.second,
                        anchor,
                        null,
                        value.value.first,
                        false,
                        R.drawable.ic_location,
                        bound,
                        dimension
                    )
                ), areasPoints.get(value.key)!!.third
            )
        }
    }

    /**
     * Can edit the selected area if the area is in the list of areas of the current editingZone
     * @param tag: tag of the area to begin editing
     * @return true if can edit, false if not
     */
    fun canEdit(tag: String): Boolean {
        val t = tag.toInt()
        return zonesToArea[editingZone]?.second?.contains(t) ?: false
    }

    /**
     * Set up the area with the tag in parameter
     * @param tag of the area to edit
     */
    fun editArea(context: Context?, tag: String) {
        val t = tag.toInt()
        val area = areasPoints[t] ?: return
        editMode = false
        tempTitle = tempValues[t]!!.first
        modifyingArea = t
        tempValues.remove(t)
        restoreMarkers(context)

        tempPoly = area.third
        tempPoly!!.strokeColor = Color.RED
        tempLatLng = area.third.points.dropLast(1).toMutableList()

        setupModifyMarkers(context)
    }

    /**
     * Removes the area with given ID
     * @param id id of the area to remove
     */
    fun removeArea(id: Int) {
        areasPoints[id] ?: return
        zonesToArea[areasPoints[id]!!.first]!!.second.remove(id)
        areasPoints[id]!!.second.remove()
        areasPoints[id]!!.third.remove()
        areasPoints.remove(id)
    }

    /**
     * Generate the string format for Firebase of the area points for a given zone
     * @param idZone id of the zone
     */
    fun zoneAreasToFormattedStringLocation(idZone: Int): String {
        var temp = zonesToArea[idZone]!!.second.toMutableList()
        var s = ""
        for (uid in temp) {
            s += areaToFormattedStringLocation(areasPoints[uid]!!.third.points.dropLast(1))
            s += AREAS_SEP
        }
        return s.substring(0, s.length - AREAS_SEP.value.length)
    }

    /**
     * Generate the string format of a list of points
     * @param loc list of the points to save into a string
     * @return formatted string of the points
     */
    fun areaToFormattedStringLocation(loc: List<LatLng>?): String {
        if (loc == null) {
            return ""
        }
        var s = ""

        for (c in loc) {
            s += c.latitude.toString() + LAT_LONG_SEP.value + c.longitude.toString() + POINTS_SEP.value
        }
        return s.substring(0, s.length - POINTS_SEP.value.length)
    }

    /**
     * Deletes the areas of a zone
     * @param id id of the zone
     */
    fun removeZoneAreas(id: Int) {
        zonesToArea[id] ?: return
        val areas = zonesToArea[id]!!.second.toList()
        for (uid in areas) {
            removeArea(uid)
        }
        zonesToArea[id] = Pair(null, mutableListOf())
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

    /**
     * Color all the areas of a zone to a certain color
     * @param idZone id of the zone to color
     * @param color color target for the zone
     */
    fun colorAreas(idZone: Int, color: Int) {
        for (key in zonesToArea[idZone]!!.second) {
            areasPoints[key]!!.third.strokeColor = color
        }
    }

    /**
     * clears the color of the current selected zone
     */
    fun clearSelectedZone() {
        if (selectedZone != -1) {
            colorAreas(selectedZone, DEFAULT_ZONE_STROKE_COLOR)
            selectedZone = -1
        }
    }

    /**
     * Set the selected zone to color it on the map
     * @param tag id of the selected zone
     */
    fun setSelectedZones(tag: Int) {
        clearSelectedZone()
        selectedZone = tag
        colorAreas(tag, SELECTED_ZONE_STROKE_COLOR)
    }

    /**
     * Set the selected zone from to color it on the map from the id of the area
     * @param tag id of the area to find the zone it belongs
     */
    fun setSelectedZoneFromArea(tag: String) {
        setSelectedZones(areasPoints[tag.toInt()]!!.first)
    }

}