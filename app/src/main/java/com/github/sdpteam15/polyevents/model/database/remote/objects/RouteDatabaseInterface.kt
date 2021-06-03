package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface RouteDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser

    /**
     * get all route from the the database and add it into all the ObservableList
     * @param nodes list of RouteNode
     * @param edges list of RouteEdge
     * @param zone list of Zone
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getRoute(
        nodes: ObservableList<RouteNode>,
        edges: ObservableList<RouteEdge>,
        zone: ObservableList<Zone>
    ): Observable<Boolean>

    /**
     * update the route in the database
     * @param newEdges list of new RouteEdges
     * @param removeEdges list of RouteEdges to remove
     * @param edges list of RouteEdge
     * @param nodes list of RouteNode
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateEdges(
        newEdges: List<RouteEdge>,
        removeEdges: List<RouteEdge>,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean>

    /**
     * remove a single RouteEdge and the RouteNode if it's no more used
     * @param edge edge to remove
     * @param edges list of RouteEdge
     * @param nodes list of RouteNode
     */
    fun removeEdge(
        edge: RouteEdge,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean>
}