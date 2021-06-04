package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.LatLngOperator
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.angle
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.divide
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.isTooParallel
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.mean
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.plus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.polygonsUnion
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.THRESHOLD
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.getNearestPoint
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Entity model for a zone. Events occur inside a zone.
 *
 * @property zoneName the name of the zone
 * @property zoneId the id of the zone
 * @property location the location of the zone
 * @property description the description of the zone
 * @property status the status of the zone
 *
 */
@IgnoreExtraProperties
data class Zone(
    var zoneId: String? = null,
    var zoneName: String? = null,
    var location: String? = null,
    var description: String? = null,
    var status: Status = Status.ACTIVE
) : Attachable {
    private var lastLocation: String? = null
    private lateinit var polygons: List<Pair<List<LatLng>, List<List<LatLng>>?>>

    /**
     * Get the coordinates of all the areas on the current Zone
     * @return A list of list of LatLng points composing an area
     */
    fun getZoneCoordinates(): MutableList<MutableList<LatLng>> {
        val listZoneCoordinates: MutableList<MutableList<LatLng>> = ArrayList()
        if (location != null) {
            val arr = location!!.split(AREAS_SEP.value)
            for (s in arr) {
                val curList = ArrayList<LatLng>()
                val points = s.split(POINTS_SEP.value)
                for (p in points) {
                    val coor = p.split(LAT_LONG_SEP.value)
                    try {
                        curList.add(LatLng(coor[0].toDouble(), coor[1].toDouble()))
                    } catch (e: NumberFormatException) {
                        println(coor)
                    }
                }
                listZoneCoordinates.add(curList)
            }
        }
        return listZoneCoordinates
    }

    /**
     * Get the coordinates of all the grouped areas on the current Zone
     * @return A list of list of LatLng points composing an area
     */
    fun getDrawingPolygons(): List<Pair<List<LatLng>, List<List<LatLng>>?>> {
        if (location == null || lastLocation != location) {
            lastLocation = location
            polygons = polygonsUnion(getZoneCoordinates())
        }
        return polygons
    }

    /**
     * Get the coordinates of all the rectangular areas on the current Zone
     * @return A list of list of LatLng points composing an area
     */
    fun getDrawingPoints(): List<List<LatLng>> {
        return getDrawingPolygons().flatMap { (it.second ?: listOf()).plusElement(it.first) }
            .map {
                if (it.first() == it.last())
                    it.drop(1)
                else
                    it
            }
    }

    override fun getAttachedNewPoint(
        position: LatLng,
        angle: Double?
    ): Pair<RouteNode, Double> {
        var res: Pair<RouteNode, Double>? = null
        for (e in getDrawingPoints())
            for (i in e.indices) {
                val from = e[i]
                val to = e[(i + 1) % e.size]
                val lineAngle = angle(from, to)
                if (angle == null || !isTooParallel(angle, lineAngle)) {
                    val newPoint = getNearestPoint(
                        RouteNode.fromLatLong(from),
                        RouteNode.fromLatLong(to),
                        position
                    )
                    newPoint.areaId = zoneId
                    val distance = euclideanDistance(position, newPoint.toLatLng())
                    if (res == null || distance < res.second)
                        res = Pair(newPoint, distance)
                }
            }
        return res!!
    }

    /**
     * Gets the center of gravity of the zone using the mean of its corners
     * @return the center of the area
     */
    fun getZoneCenter(): LatLng {
        var pointCount = 0.0
        var sumLatLng = LatLng(0.0, 0.0)
        for (latLngList in getZoneCoordinates()) {
            for (latLng in latLngList) {
                pointCount++
                sumLatLng = plus(sumLatLng, latLng)
            }
        }
        return divide(sumLatLng, pointCount)
    }

    override fun splitOnIntersection(
        newEdges: MutableList<RouteEdge>,
        removeEdges: MutableList<RouteEdge>
    ) {
        val edgesToRemoveIfInTheZone = mutableListOf<RouteEdge>()
        for (l in getDrawingPoints())
            for (i in l.indices) {
                val from = l[i]
                val to = l[(i + 1) % l.size]
                for (e in newEdges.toList()) {
                    if (e.start != null && e.end != null) {
                        val intersection = LatLngOperator.getIntersection(
                            e.start!!.toLatLng(),
                            e.end!!.toLatLng(),
                            from,
                            to
                        )
                        if (intersection != null) {
                            //intersection type
                            // in the comment `e` is represented by the horizontal line (from left, to right) and `this` is represented by the vertical line (start up, end down)
                            if (
                            // ┃
                            // ┣━━━━
                            // ┃
                                !(e.start!!.areaId == zoneId && euclideanDistance(
                                    e.start!!.toLatLng(),
                                    intersection
                                ) < THRESHOLD) &&
                                //     ┃
                                // ━━━━┫
                                //     ┃
                                !(e.end!!.areaId == zoneId && euclideanDistance(
                                    e.end!!.toLatLng(),
                                    intersection
                                ) < THRESHOLD)
                            ) {
                                newEdges.remove(e)
                                edgesToRemoveIfInTheZone.remove(e)
                                val intersectionNode = RouteNode.fromLatLong(
                                    when {
                                        // ━━┳━━
                                        //   ┃
                                        //   ┃
                                        euclideanDistance(from, intersection) < THRESHOLD -> from
                                        //   ┃
                                        //   ┃
                                        // ━━┻━━
                                        euclideanDistance(to, intersection) < THRESHOLD -> to
                                        //   ┃
                                        // ━━╋━━
                                        //   ┃
                                        else -> intersection
                                    }
                                )
                                intersectionNode.areaId = this.zoneId
                                val add = listOf(
                                    RouteEdge.fromRouteNode(
                                        e.start!!,
                                        intersectionNode
                                    ),
                                    RouteEdge.fromRouteNode(
                                        intersectionNode,
                                        e.end!!
                                    )
                                )
                                newEdges.addAll(add)
                                edgesToRemoveIfInTheZone.addAll(add)
                            }
                        }
                    }
                }
            }
        for (e in edgesToRemoveIfInTheZone)
            if (edgeIsInZone(e))
                newEdges.remove(e)
    }

    private fun edgeIsInZone(edges: RouteEdge): Boolean {
        val list = mutableListOf(
            edges.start!!,
            edges.end!!,
        )
        if (edges.start!!.areaId != zoneId && edges.end!!.areaId == zoneId)
            list.remove(edges.end!!)
        if (edges.end!!.areaId != zoneId && edges.start!!.areaId == zoneId)
            list.remove(edges.start!!)
        val mean = mean(list.map { it.toLatLng() })
        for (l in getZoneCoordinates())
            if (LatLngOperator.pointInsidePolygon(mean, LatLngOperator.Polygon(l)))
                return true
        return false
    }

    enum class Status {
        ACTIVE,
        DELETED;

        companion object {
            private val mapOrdinal = values()

            /**
             * Return the Zone status corresponding to the given ordinal
             * @param ordinal The index of the Zone status we want to retrieve
             */
            fun fromOrdinal(ordinal: Int) = mapOrdinal[ordinal]
        }
    }
}
