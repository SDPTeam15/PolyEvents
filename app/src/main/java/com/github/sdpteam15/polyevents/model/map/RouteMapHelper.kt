package com.github.sdpteam15.polyevents.model.map

import androidx.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.divide
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.minus
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.norm
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.plus
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.scalar
import com.github.sdpteam15.polyevents.model.map.LatLngOpperator.time
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.*
import kotlin.math.pow

const val THRESHOLD = 0.001

object RouteMapHelper {
    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()
    val zone = ObservableList<Zone>()

    var map: MapsInterface? = null
    val toDeleteLines: MutableList<Polyline> = ArrayList()
    val lineToEdge: MutableMap<RouteEdge, Polyline> = mutableMapOf()
    val idToEdge: MutableMap<RouteEdge, Polyline> = mutableMapOf()



    var deleteMode = false
    var tempUid = 0

    /**
     * Add a line to dataBase
     */
    fun addLine(start: LatLng, end: LatLng) {
        val s = RouteNode.fromLatLong(start, null)
        val e = RouteNode.fromLatLong(end, null)
        nodes.add(s)
        nodes.add(e)
        edges.add(RouteEdge("Edge ${tempUid++}", s, e))
    }

    /**
     * TODO
     */
    fun removeLine(edge: RouteEdge) {

    }

    /**
     * Returns the shortest path from a point on the map to the given Zone
     * @param startPosition the person starting position
     * @param targetZoneId the Zone where the person wants to go to
     * @return The list of points that the person needs to follow
     */
    fun getShortestPath(startPosition: LatLng, targetZoneId: String): List<LatLng>? {
        TODO()
    }

    /**
     * TODO
     */
    fun drawRoute() {
        TODO()
    }

    /**
     * TODO
     */
    fun getEdgeOnNearestAttachable(start: LatLng, end: LatLng): Pair<LatLng, LatLng> {
        // TODO
        return Pair(start, end)
    }

    /**
     * TODO
     */
    fun getPosOnNearestAttachable(
        point: LatLng,
        angle: Double? = null,
        exclude: Attachable? = null
    ): Pair<RouteNode, Attachable?> {
        var res: Pair<Pair<RouteNode, Attachable?>, Double?> =
            Pair(Pair(RouteNode.fromLatLong(point), null), null)
        val found: (Attachable) -> Unit = {
            if (it != exclude) {
                val pair = it.getAttachedNewPoint(point, angle)
                if (res.second == null || pair.second < res.second!!)
                    res = Pair(Pair(pair.first, it), pair.second)
            }
        }
        for (e in nodes) found(e)
        for (e in edges) found(e)
        for (e in zone) found(e)
        return res.first
    }

    /**
     * TODO
     */
    fun getNodesAndEdgesFromDB(lifecycleOwner: LifecycleOwner): Observable<Boolean> {
        edges.observeAdd(lifecycleOwner){
            edgeAddedNotification(it.value)
        }
        edges.observeRemove(lifecycleOwner){
            edgeRemovedNotification(it.value)
        }
        return Observable(true)
    }

    private fun addEdgeToMaps(){

    }

    fun edgeRemovedNotification(edge: RouteEdge){
        lineToEdge[edge]!!.remove()
        lineToEdge.remove(edge)
    }

    fun edgeAddedNotification(edge: RouteEdge){
        removeAllLinesToRemove()
        val option = PolylineOptions()
        option.add(edge.start.toLatLng())
        option.add(edge.end.toLatLng())
        val route = map!!.addPolyline(option)
        route.tag = edge.id
        lineToEdge[edge] = route
    }

    fun removeAllLinesToRemove(){
        for(line in toDeleteLines){
            line.remove()
        }
        toDeleteLines.clear()
    }

    fun createNewRoute(context: Context?){
        deleteMode = false
        if(tempPolyline != null){
            tempPolyline!!.remove()
            tempVariableClear()
        }
        setupEditLine(context, map!!.cameraPosition!!.target)
    }

    fun saveNewRoute(){
        deleteMode = false
        toDeleteLines.add(tempPolyline!!)
        //tempPolyline!!.isClickable = true
        tempPolyline!!.color = Color.GREEN
        addLine(tempLatLng[0]!!, tempLatLng[1]!!)
        tempVariableClear()
    }

    fun removeRoute(context: Context?){
        if(tempPolyline != null){
            tempPolyline!!.remove()
            tempVariableClear()
        }else{
            deleteMode = !deleteMode
            HelperFunctions.showToast("Deletion mode is $deleteMode", context)
        }
    }

    fun tempVariableClear(){
        startMarker!!.remove()
        endMarker!!.remove()

        startMarker = null
        endMarker = null
        tempPolyline = null
        tempLatLng.clear()
    }

    var startMarker: Marker? = null
    var endMarker: Marker? = null

    var tempPolyline: Polyline? = null
    var tempLatLng: MutableList<LatLng?> = ArrayList()

    /**
     * Sets up the edition of the line
     * @param context context
     * @param pos position to create the new line
     */
    fun setupEditLine(context: Context?, pos: LatLng){
        val zoom = map!!.cameraPosition!!.zoom
        val divisor = 2.0.pow(zoom.toDouble())
        val longDiff = 188.0 / divisor / 2
        val pos1 = LatLng(pos.latitude, pos.longitude - longDiff)
        val pos2 = LatLng(pos.latitude, pos.longitude + longDiff)
        val option = PolylineOptions().add(pos1).add(pos2).color(Color.RED)
        tempPolyline = map!!.addPolyline(option)
        tempLatLng.clear()
        tempLatLng.add(pos1)
        tempLatLng.add(pos2)

        setupModifyMarkers(context)
    }

    /**
     * Sets up the markers for the modification of the polyline
     * @param context context
     */
    fun setupModifyMarkers(context: Context?){
        val points = tempPolyline!!.points
        val startPos = points[0]
        val endPos = points[1]

        val anchor = IconAnchor(0.5f, 0.5f)
        val bound = IconBound(0, 0, 100, 100)
        val dimension = IconDimension(100, 100)

        startMarker = map!!.addMarker(
            GoogleMapHelper.newMarker(
                context,
                startPos,
                anchor,
                PolygonAction.MARKER_START.toString(),
                null,
                true,
                R.drawable.ic_locate_me,
                bound,
                dimension
            )
        )

        endMarker = map!!.addMarker(
            GoogleMapHelper.newMarker(
                context,
                endPos,
                anchor,
                PolygonAction.MARKER_END.toString(),
                null,
                true,
                R.drawable.ic_locate_me,
                bound,
                dimension
            )
        )
    }

    /**
     * Moves the marker and redraws the polyline
     * @param marker marker that moved
     */
    fun moveMarker(marker: Marker){
        when (marker.snippet) {
            PolygonAction.MARKER_START.toString() ->{

            }

            PolygonAction.MARKER_END.toString() ->{

            }
        }
        tempPolyline!!.points = listOf(startMarker!!.position, endMarker!!.position)
    }

    fun polylineClick(polyline:Polyline){
        if(deleteMode){
            Log.d("DELETE", "POLYLINE TO DELETE")
        }
    }
}