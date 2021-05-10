package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.map.Attachable
import com.github.sdpteam15.polyevents.model.map.LatLngOperator
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.angle
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.divide
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.euclideanDistance
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.isTooParallel
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.minus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.plus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.scalar
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.squaredNorm
import com.github.sdpteam15.polyevents.model.map.RouteMapHelper.getNearestPoint
import com.github.sdpteam15.polyevents.model.map.THRESHOLD
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Entity model for a zone. Events occur inside a zone.
 *
 * @property zoneName the name of the zone
 * @property location the location of the zone
 *
 */
@IgnoreExtraProperties
data class Zone(
    var zoneId: String? = null,
    var zoneName: String? = null,
    var location: String? = null,
    var description: String? = null
) : Attachable {
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

    fun getDrawingPolygons(): List<Pair<List<LatLng>,List<List<LatLng>>?>> {
        // TODO reduce the number of element
        return getZoneCoordinates().map { Pair(it, null) }
    }

    /**
     * Get the coordinates of all the grouped areas on the current Zone
     * @return A list of list of LatLng points composing an area
     */
    fun getDrawingPoints(): List<List<LatLng>> {
        // TODO reduce the number of element
        return getZoneCoordinates()
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
        for (e in edgesToRemoveIfInTheZone)
            if (edgeIsInZone(e))
                newEdges.remove(e)
    }

    private fun edgeIsInZone(edges: RouteEdge): Boolean {
        val mid = divide(plus(edges.start!!.toLatLng(), edges.end!!.toLatLng()), 2.0)
        for (l in getZoneCoordinates()) {
            val AM = minus(mid, l[0])
            val AB = minus(l[1], l[0])
            val BM = minus(mid, l[1])
            val BC = minus(l[2], l[1])
            //https://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle
            if ((scalar(AM, AB) in (-THRESHOLD)..(squaredNorm(AB) + THRESHOLD)) &&
                (scalar(BM, BC) in (-THRESHOLD)..(squaredNorm(BC) + THRESHOLD))
            )
                return true
        }
        return false
    }
}