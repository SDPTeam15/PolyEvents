package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import android.graphics.Color
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.DEFAULT_ZONE_STROKE_COLOR
import com.github.sdpteam15.polyevents.model.map.GoogleMapMode.colorAreas
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementActivity
import com.github.sdpteam15.polyevents.view.fragments.MapsFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import kotlin.math.pow

object ZoneAreaMapHelper {
    private const val INDEX_ROTATION_MARKER = 3
    var editingZone: String? = null
    var zone: Zone? = null
    var editMode = false
    var deleteMode = false
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

    /**
     * Import zone from database
     * @param zone zone to import
     */
    fun importNewZone(context: Context?, zone: Zone, drawingMod: Boolean) {
        if (GoogleMapHelper.map == null) {
            waitingZones.add(zone)
            return
        }
        val temp = editingZone
        editingZone = zone.zoneId

        if (zonesToArea.containsKey(zone.zoneId)) {
            removeZoneAreas(zone.zoneId!!)
        }

        zonesToArea[editingZone ?: "zone ${GoogleMapHelper.uidZone++}"] =
            Pair(zone, mutableListOf())

        if (drawingMod)
            for (area in zone.getDrawingPolygons()) {

                addArea(context, GoogleMapHelper.uidArea++, area, zone.zoneName)
            }
        else
            for (area in zone.getZoneCoordinates()) {
                addArea(context, GoogleMapHelper.uidArea++, Pair(area, null), zone.zoneName)
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

            poly.clickable(true).strokeColor(GoogleMapMode.DEFAULT_ZONE_STROKE_COLOR)

            val polygon = GoogleMapHelper.map!!.addPolygon(poly)

            var list = coords.first
            if (list.first() == list.last())
                list = coords.first.dropLast(1)

            val anchor = IconAnchor(0f, 0f)
            val bound = IconBound(0, 0, 0, 0)
            val dimension = IconDimension(1, 1)

            val center = LatLngOperator.mean(list)
            val marker = GoogleMapHelper.map!!.addMarker(
                GoogleMapHelperFunctions.newMarker(
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
        //If in delete mode, deactivate delete mode
        if (deleteMode)
            deleteMode(context)
        //If in edit mode, deactivate edit mode
        if (editMode)
            editMode(context)
        clearTemp()
        setupEditZone(context, GoogleMapHelper.map!!.cameraPosition!!.target)
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
                id = GoogleMapHelper.uidArea++
                name = "Area $id"

            }
            addArea(context, id, Pair(tempPoly!!.points, null), name)
            colorAreas(
                editingZone!!,
                GoogleMapMode.EDITED_ZONE_STROKE_COLOR
            )
        }
        clearTemp()
    }

    /**
     * Activate/Deactivate the delete mode
     */
    fun deleteMode(context: Context?) {
        //If in edit mode, deactivate edit mode
        if (editMode)
            editMode(context)

        //If a polygon was being created, then removes it, else changes the deletion mode
        if (tempPoly != null) {
            tempPoly!!.remove()
            clearTemp()
        } else {
            deleteMode = !deleteMode
            MapsFragment.instance?.switchIconDeleteArea()

            //If already in delete mode, restores the map else prepare for deletion
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
    }

    /**
     * Saves an area
     */
    fun saveArea() {
        editMode = false
        clearTemp()
        //TODO : Save the areas in the map
        //val location = GoogleMapHelper.areasToFormattedStringLocations(from = startId)
        val location =
            GoogleMapHelperFunctions.zoneAreasToFormattedStringLocation(editingZone!!)
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
        val zoom = GoogleMapHelper.map!!.cameraPosition!!.zoom
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
        tempPoly = GoogleMapHelper.map!!.addPolygon(
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

        val posMidRight = LatLngOperator.mean(listOf(pos3, pos4))
        val posMidDown = LatLngOperator.mean(listOf(pos3, pos2))
        val posCenter = LatLngOperator.mean(listOf(pos4, pos2))

        val anchor = IconAnchor(0.5f, 0.5f)
        val bound = IconBound(0, 0, 100, 100)
        val dimension = IconDimension(100, 100)

        moveDiagMarker = GoogleMapHelper.map!!.addMarker(
            GoogleMapHelperFunctions.newMarker(
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

        moveRightMarker = GoogleMapHelper.map!!.addMarker(
            GoogleMapHelperFunctions.newMarker(
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

        moveDownMarker = GoogleMapHelper.map!!.addMarker(
            GoogleMapHelperFunctions.newMarker(
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

        moveMarker = GoogleMapHelper.map!!.addMarker(
            GoogleMapHelperFunctions.newMarker(
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

        rotationMarker = GoogleMapHelper.map!!.addMarker(
            GoogleMapHelperFunctions.newMarker(
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
        val diff = LatLngOperator.minus(pos.position, movePos!!)

        tempLatLng = tempLatLng.map { latLng ->
            LatLngOperator.plus(latLng!!, diff)
        }.toMutableList()

        // Moves the edition markers
        moveMarker!!.position = LatLngOperator.plus(movePos!!, diff)
        movePos = moveMarker!!.position

        moveDiagMarker!!.position = LatLngOperator.plus(moveDiagPos!!, diff)
        moveDiagPos = moveDiagMarker!!.position

        moveRightMarker!!.position = LatLngOperator.plus(moveRightPos!!, diff)
        moveRightPos = moveRightMarker!!.position

        moveDownMarker!!.position = LatLngOperator.plus(moveDownPos!!, diff)
        moveDownPos = moveDownMarker!!.position

        rotationMarker!!.position = LatLngOperator.plus(rotationPos!!, diff)
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
        val vec = LatLngOperator.minus(pos.position, moveDiagPos!!)

        //Projection of the vector on the two axis (in cartesian space)
        var diffCoord =
            GoogleMapVectorHelper.projectionVectorThroughCartesian(vec, latlng2, latlng3)
        var diffCoord1 =
            GoogleMapVectorHelper.projectionVectorThroughCartesian(vec, latlng1, latlng2)


        // Move the corresponding corners of the rectangle
        when (pos.snippet) {
            PolygonAction.RIGHT.toString() -> {
                diffCoord = LatLng(0.0, 0.0)
                tempLatLng[2] = LatLngOperator.plus(latlng2, diffCoord1)
                tempLatLng[3] = LatLngOperator.plus(latlng3, diffCoord1)
            }
            PolygonAction.DOWN.toString() -> {
                diffCoord1 = LatLng(0.0, 0.0)
                tempLatLng[1] = LatLngOperator.plus(latlng1, diffCoord)
                tempLatLng[2] = LatLngOperator.plus(latlng2, diffCoord)
            }
            else -> { //Should only be DIAG
                tempLatLng[1] = LatLngOperator.plus(latlng1, diffCoord)
                tempLatLng[2] =
                    LatLngOperator.plus(LatLngOperator.plus(latlng2, diffCoord), diffCoord1)
                tempLatLng[3] = LatLngOperator.plus(latlng3, diffCoord1)
            }
        }

        // Moves the edition markers
        val v = LatLngOperator.plus(diffCoord, diffCoord1)

        moveDiagMarker!!.position = LatLngOperator.plus(moveDiagPos!!, v)
        moveDiagPos = moveDiagMarker!!.position

        moveMarker!!.position = LatLngOperator.plus(movePos!!, LatLngOperator.divide(v, 2.0))
        movePos = moveMarker!!.position

        moveRightMarker!!.position = LatLngOperator.plus(
            LatLngOperator.plus(moveRightPos!!, diffCoord1),
            LatLngOperator.divide(diffCoord, 2.0)
        )
        moveRightPos = moveRightMarker!!.position

        moveDownMarker!!.position = LatLngOperator.plus(
            LatLngOperator.plus(moveDownPos!!, diffCoord),
            LatLngOperator.divide(diffCoord1, 2.0)
        )
        moveDownPos = moveDownMarker!!.position

        rotationMarker!!.position = LatLngOperator.plus(rotationPos!!, diffCoord1)
        rotationPos = rotationMarker!!.position
    }

    /**
     * Rotate the current polygon according to the given Marker. It also aligns this
     * marker so that it remains on the circle around the center of rotation.
     * @param pos: the Marker indicating the rotation.
     */
    fun rotatePolygon(pos: Marker) {
        // Get the center of the projection
        val center = GoogleMapVectorHelper.getCenter(tempLatLng)

        val posProj = GoogleMapVectorHelper.equirectangularProjection(pos.position, center)
        val oldPosProj = GoogleMapVectorHelper.equirectangularProjection(rotationPos, center)

        val rotationAngle =
            GoogleMapVectorHelper.getDirection(posProj) - GoogleMapVectorHelper.getDirection(
                oldPosProj
            )
        val rotationAngleDegree = GoogleMapVectorHelper.radianToDegree(rotationAngle).toFloat()

        // Rotate all the points of the polygon
        val cornersRotatedLatLng = tempLatLng.map {
            GoogleMapVectorHelper.applyRotation(
                it,
                rotationAngle,
                center
            )
        }
        for (i in cornersRotatedLatLng.indices) {
            tempLatLng[i] = cornersRotatedLatLng[i]
        }

        // Move all the markers on the corresponding corner of the polygon
        rotationMarker!!.position = tempLatLng[INDEX_ROTATION_MARKER]
        rotationPos = rotationMarker!!.position

        moveDiagMarker!!.position =
            GoogleMapVectorHelper.applyRotation(moveDiagMarker!!.position, rotationAngle, center)
        moveDiagPos = moveDiagMarker!!.position
        moveDiagMarker!!.rotation -= rotationAngleDegree

        moveMarker!!.position =
            GoogleMapVectorHelper.applyRotation(moveMarker!!.position, rotationAngle, center)
        movePos = moveMarker!!.position

        moveRightMarker!!.position =
            GoogleMapVectorHelper.applyRotation(moveRightMarker!!.position, rotationAngle, center)
        moveRightPos = moveRightMarker!!.position
        moveRightMarker!!.rotation -= rotationAngleDegree

        moveDownMarker!!.position =
            GoogleMapVectorHelper.applyRotation(moveDownMarker!!.position, rotationAngle, center)
        moveDownPos = moveDownMarker!!.position
        moveDownMarker!!.rotation -= rotationAngleDegree
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
                GoogleMapHelper.map!!.addMarker(
                    GoogleMapHelperFunctions.newMarker(
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
        tempValues.remove(id)
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

    /**
     * Switches the edit mode, and remove/recreates the markers for edition purpose
     */
    fun editMode(context: Context?) {
        //If in delete mode, deactivate delete mode
        if (deleteMode)
            deleteMode(context)
        editMode = !editMode

        //If already in edit mode, restores the map to go outside of edit mode, else prepare for edition
        if (editMode) {
            for (a in areasPoints) {
                tempValues[a.key] =
                    Pair(a.value.second.title, a.value.second.position)
                a.value.second.remove()
            }
            colorAreas(
                editingZone!!,
                GoogleMapMode.EDITED_ZONE_STROKE_COLOR
            )
        } else {
            colorAreas(
                editingZone!!,
                DEFAULT_ZONE_STROKE_COLOR
            )
            restoreMarkers(context)
        }
    }
}