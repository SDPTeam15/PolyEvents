package com.github.sdpteam15.polyevents.model.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelperFunctions.newMarker
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelperFunctions.zoneAreasToFormattedStringLocation
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.DEFAULT_ZONE_STROKE_COLOR
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.EDITED_ZONE_STROKE_COLOR
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.colorAreas
import com.github.sdpteam15.polyevents.model.map.GoogleMapVectorHelper.applyRotation
import com.github.sdpteam15.polyevents.model.map.GoogleMapVectorHelper.equirectangularProjection
import com.github.sdpteam15.polyevents.model.map.GoogleMapVectorHelper.getCenter
import com.github.sdpteam15.polyevents.model.map.GoogleMapVectorHelper.getDirection
import com.github.sdpteam15.polyevents.model.map.GoogleMapVectorHelper.projectionVector
import com.github.sdpteam15.polyevents.model.map.GoogleMapVectorHelper.radianToDegree
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import kotlin.math.*

//TODO : Refactor file, it is too long

@SuppressLint("StaticFieldLeak")
object GoogleMapHelper {
    var map: MapsInterface? = null
    var uidArea = 0
    var uidZone = 0

    var editMode = false
    var deleteMode = false
    var editingZone: String? = null
    var selectedZone: String? = null

    private const val INDEX_ROTATION_MARKER = 3


    val areasPoints: MutableMap<Int, Triple<String, Marker, Polygon>> = mutableMapOf()
    val zonesToArea: MutableMap<String, Pair<Zone?, MutableList<Int>>> = mutableMapOf()
    val waitingZones: MutableList<Zone> = mutableListOf()

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
     * Redraws all areas that were previously drawn before changing fragment or activity, draws some example
     */
    fun restoreMapState(context: Context?, drawingMod: Boolean) {
        val currentEditZone = editingZone
        val areaTemp = areasPoints.toMap()
        val zoneTemp = zonesToArea.toMap()
        zonesToArea.clear()
        areasPoints.clear()
        for ((k, v) in zoneTemp) {
            editingZone = k
            zonesToArea[editingZone!!] = Pair(null, mutableListOf())
            for (id in v.second) {
                addArea(
                    context,
                    id,
                    Pair(areaTemp[id]!!.third.points, areaTemp[id]!!.third.holes),
                    areaTemp[id]!!.second.title
                )
            }
        }
        val copyWaitingZones = waitingZones.toList()
        for (zone in copyWaitingZones) {
            importNewZone(context, zone, drawingMod)
            waitingZones.remove(zone)
        }

        editingZone = currentEditZone
    }

    /**
     * Import zone from database
     * @param zone zone to import
     */
    fun importNewZone(context: Context?, zone: Zone, drawingMod: Boolean) {
        if (map == null) {
            waitingZones.add(zone)
            return
        }
        val temp = editingZone
        editingZone = zone.zoneId

        if (zonesToArea.containsKey(zone.zoneId)) {
            removeZoneAreas(zone.zoneId!!)
        }

        zonesToArea[editingZone ?: "zone ${uidZone++}"] = Pair(zone, mutableListOf())

        if (drawingMod)
            for (area in zone.getDrawingPolygons()) {

                addArea(context, uidArea++, area, zone.zoneName)
            }
        else
            for (area in zone.getZoneCoordinates()) {
                addArea(context, uidArea++, Pair(area, null), zone.zoneName)
            }
        editingZone = temp
    }

    /**
     * Helper method to add a area to the map and generate an invisible marker in its center to display the area infos
     * @param id id of the area
     * @param coords coordinates of the area (polygons)
     * @param name name of the area
     */
    fun addArea(
        context: Context?,
        id: Int,
        coords: Pair<List<LatLng>, List<List<LatLng>>?>,
        name: String?
    ) {
        if (coords.first.isNotEmpty()) {
            val poly = PolygonOptions()
            poly.addAll(coords.first)

            if (coords.second != null)
                for (hole in coords.second!!)
                    if (hole.isNotEmpty())
                        poly.addHole(hole)

            poly.clickable(true).strokeColor(DEFAULT_ZONE_STROKE_COLOR)

            val polygon = map!!.addPolygon(poly)

            var list = coords.first
            if (list.first() == list.last())
                list = coords.first.dropLast(1)

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
            areasPoints[id] = Triple(editingZone!!, marker, polygon)
            if (!zonesToArea[editingZone!!]!!.second.contains(id)) {
                zonesToArea[editingZone!!]!!.second.add(id)
            }
        }
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
            val name: String
            val id: Int
            if (tempTitle != null) {
                name = tempTitle!!
                id = modifyingArea
            } else {
                id = uidArea++
                name = "Area $id"

            }
            addArea(context, id, Pair(tempPoly!!.points, null), name)
            colorAreas(editingZone!!, EDITED_ZONE_STROKE_COLOR)
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
        editMode = false
        deleteMode = false
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
     * Switches the edit mode, and remove/recreates the markers for edition purpose
     */
    fun editMode(context: Context?) {
        editMode = !editMode
        deleteMode = false
        if (editMode) {
            for (a in areasPoints) {
                tempValues[a.key] = Pair(a.value.second.title, a.value.second.position)
                a.value.second.remove()
            }
            colorAreas(editingZone!!, EDITED_ZONE_STROKE_COLOR)
        } else {
            colorAreas(editingZone!!, DEFAULT_ZONE_STROKE_COLOR)
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
     * Deletes the areas of a zone
     * @param id id of the zone to remove areas
     */
    fun removeZoneAreas(id: String) {
        zonesToArea[id] ?: return
        val areas = zonesToArea[id]!!.second.toList()
        for (uid in areas) {
            removeArea(uid)
        }
        zonesToArea[id] = Pair(null, mutableListOf())
    }

    /**
     * Remove a zone from the map
     * @param id id of the zone to remove
     */
    fun removeZone(id: String) {
        removeZoneAreas(id)
        zonesToArea.remove(id)
    }

    /*
    TODO : Implement the deletion of one area while editing
    fun deleteMode(context: Context) {
        Log.d("DELETE AREA", "Area delete mode = $deleteMode")
        editMode = false
        deleteMode = !deleteMode
        if (deleteMode) {
            for (a in areasPoints) {
                tempValues[a.key] = Pair(a.value.second.title, a.value.second.position)
                a.value.second.remove()
            }
            colorAreas(editingZone!!, Color.RED)
        } else {
            colorAreas(editingZone!!, DEFAULT_ZONE_STROKE_COLOR)
            restoreMarkers(context)
        }
    }
    */

    var zone: Zone? = null
    fun saveArea() {
        editMode = false
        clearTemp()
        //TODO : Save the areas in the map
        //val location = GoogleMapHelper.areasToFormattedStringLocations(from = startId)
        val location =
            zoneAreasToFormattedStringLocation(editingZone!!)
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
}