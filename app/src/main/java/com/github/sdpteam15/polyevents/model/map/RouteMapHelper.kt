package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.type.LatLng

class RouteMapHelper {
    val nodes = ObservableList<RouteNode>()
    val edges = ObservableList<RouteEdge>()

    /**
     * Add a line to dataBase
     */
    fun addLine(start : LatLng, end : LatLng) {
        TODO()
    }

    /**
     * TODO
     */
    fun removeLine(edge: RouteEdge){
        TODO()
    }

    /**
     * TODO
     */
    fun getShortestPath(start: LatLng, end : RouteNode) : List<LatLng>{
        TODO()
    }

    /**
     * TODO
     */
    fun drawRoute(){
        TODO()
    }
}