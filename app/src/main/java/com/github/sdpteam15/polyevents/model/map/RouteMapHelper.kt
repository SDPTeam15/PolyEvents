package com.github.sdpteam15.polyevents.model.map

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.minus
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.norm
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.fragments.MapsFragment
import com.google.android.gms.maps.model.*
import kotlin.math.pow


object RouteMapHelper {
    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()

    var map: MapsInterface? = null
    val toDeleteLines: MutableList<Polyline> = ArrayList()
    val lineToEdge: MutableMap<RouteEdge, Polyline> = mutableMapOf()
    val idToEdge: MutableMap<String, RouteEdge> = mutableMapOf()
    var attachables: Pair<Attachable?, Attachable?> = Pair(null, null)


    var deleteMode = false
    var tempUid = 0
    var routing = false
    var currentTarget: LatLng? = null
    var chemin: MutableList<LatLng> = mutableListOf()
    var route:MutableList<Polyline> = mutableListOf()
    val epsilon = 0.0001

    fun addLine(
            start: Pair<LatLng, Attachable?>,
            end: Pair<LatLng, Attachable?>
    ) {
        val s = RouteNode.fromLatLong(start.first, null)
        val e = RouteNode.fromLatLong(end.first, null)
        nodes.add(s)
        nodes.add(e)
        edges.add(RouteEdge("Edge ${tempUid++}", s, e))
    }

    /**
     * TODO
     */
    fun removeLine(edge: RouteEdge) {
        edges.remove(edge)
    }

    /**
     * TODO
     */
    fun getShortestPath(startPosition: LatLng, targetZoneId: String): List<LatLng> {
        TODO()
    }

    /**
     * TODO
     */
    fun drawRoute() {
        undrawRoute()
        if(chemin.isNotEmpty()){
            var start = chemin[0]
            currentTarget = chemin[1]
            val cheminTemp = chemin.drop(1)
            for(end in cheminTemp){
                route.add(map!!.addPolyline(PolylineOptions().add(start).add(end).color(Color.BLUE).width(15f)))
                start = end
            }
        }
    }

    fun updateRoute(){
        if(route.isNotEmpty()){
            val position = minus(LatLng(0.0,0.0), currentTarget!!)
            val position2 = minus(chemin[1], currentTarget!!)
            if(norm(minus(position, position2)) < LatLngOperator.epsilon){
                route.first().remove()
                route = route.drop(1).toMutableList()
                chemin = chemin.drop(1).toMutableList()
                if(chemin.size > 1){
                    currentTarget = chemin[1]
                }else{
                    chemin.clear()
                    currentTarget = null
                }
            }else{
                //DO Projection and redraw the line with route[0].points = listOf(...)
            }
        }
    }

    /**
     * Undraws the route
     */
    fun undrawRoute(){
        for(r in route){
            r.remove()
        }
        route.clear()
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
    fun getPosOnNearestAttachableFrom(
            fixed: LatLng,
            moving: LatLng,
            attachable: Attachable?
    ): Pair<LatLng, Attachable?>{
        return Pair(moving, null)
    }

    /**
     * TODO
     */
    fun getNodesAndEdgesFromDB(context: Context?,lifecycleOwner: LifecycleOwner): Observable<Boolean> {
        edges.observeAdd(lifecycleOwner){
            edgeAddedNotification(context, it.value)
        }
        edges.observeRemove(lifecycleOwner){
            edgeRemovedNotification(it.value)
        }
        return Observable(true)
    }


    /**
     * Function that handles the remove of a route from the database
     * @param edge deleted edge
     */
    fun edgeRemovedNotification(edge: RouteEdge){
        lineToEdge[edge]!!.remove()
        lineToEdge.remove(edge)
        idToEdge.remove(edge.id)
    }

    /**
     * Function that handles the addition of a route from the database
     * @param edge new edge
     */
    fun edgeAddedNotification(context: Context?,edge: RouteEdge){
        //Remove all creation lines when we get an answer from the database
        removeAllLinesToRemove()
        val option = PolylineOptions()
        option.add(edge.start.toLatLng())
        option.add(edge.end.toLatLng())
        option.clickable(true)
        val route = map!!.addPolyline(option)

        //tag used to know which polyline has been clicked
        if(context != null){
            route.tag = edge.id
        }

        lineToEdge[edge] = route
        idToEdge[edge.id!!] = edge
    }

    /**
     * Remove all the lines used for creation from the map when we get an answer from the database
     */
    fun removeAllLinesToRemove(){
        for(line in toDeleteLines){
            line.remove()
        }
        toDeleteLines.clear()
    }

    /**
     * Adds a new route to google map and to the temporary variables
     * @param context context
     */
    fun createNewRoute(context: Context?){
        deleteMode = false
        if(tempPolyline != null){
            tempPolyline!!.remove()
            tempVariableClear()
        }
        setupEditLine(context, map!!.cameraPosition!!.target)
        MapsFragment.instance?.showSaveButton()
    }

    /**
     * Saves the route that was being created
     */
    fun saveNewRoute(){
        deleteMode = false
        toDeleteLines.add(tempPolyline!!)
        tempPolyline!!.color = Color.GREEN
        addLine(Pair(tempLatLng[0]!!, attachables.first), Pair(tempLatLng[1]!!, attachables.second))
        tempVariableClear()
        MapsFragment.instance?.showSaveButton()
    }

    /**
     * Either removes the route that is being created, or activates the remove mode
     * @param context context
     */
    fun removeRoute(context: Context?){
        if(tempPolyline != null){
            tempPolyline!!.remove()
            tempVariableClear()
        }else{
            deleteMode = !deleteMode
            MapsFragment.instance?.switchIconDelete()
        }
        MapsFragment.instance?.showSaveButton()
    }

    /**
     * Clears the variables
     */
    fun tempVariableClear(){
        startMarker!!.remove()
        endMarker!!.remove()

        startMarker = null
        endMarker = null
        tempPolyline = null
        tempLatLng.clear()
        attachables = Pair(null,null)
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
     * @param dragMode what function called the moveMarker method (DRAG_START, DRAG, DRAG_END)
     */
    fun moveMarker(marker: Marker, dragMode: MarkerDragMode){
        if(dragMode == MarkerDragMode.DRAG || dragMode == MarkerDragMode.DRAG_START){
            //Changes the coordinates of the polyline to where it can be displayed
            val res = getEdgeOnNearestAttachable(startMarker!!.position, endMarker!!.position)
            val points = tempPolyline!!.points
            when (marker.snippet) {
                PolygonAction.MARKER_START.toString() ->{
                    val res = getPosOnNearestAttachableFrom(endMarker!!.position, startMarker!!.position, attachables.first)
                    tempLatLng[0] = startMarker!!.position
                    attachables = Pair(res.second, attachables.second)
                    tempPolyline!!.points = listOf(res.first, points[0])
                }

                PolygonAction.MARKER_END.toString() ->{
                    val res = getPosOnNearestAttachableFrom(startMarker!!.position, endMarker!!.position, attachables.second)
                    tempLatLng[1] = endMarker!!.position
                    attachables = Pair(attachables.first, res.second)
                    tempPolyline!!.points = listOf(points[0], res.first)
                }
            }
        }else if(dragMode == MarkerDragMode.DRAG_END){
            //On end drag, we set the position of the markers to the position of the line
            val points = tempPolyline!!.points
            startMarker!!.position = points[0]
            endMarker!!.position = points[1]
            tempLatLng[0] = startMarker!!.position
            tempLatLng[1] = endMarker!!.position
        }
    }

    /**
     * Handles the click on a polyline : if on delete mode, deletes the polyline
     * @param polyline polyline clicked
     */
    fun polylineClick(polyline:Polyline){
        if(deleteMode){
            removeLine(idToEdge[polyline.tag]!!)
        }
    }
}