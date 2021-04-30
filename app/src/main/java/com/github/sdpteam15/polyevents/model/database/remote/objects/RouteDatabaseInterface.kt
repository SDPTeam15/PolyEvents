package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface RouteDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    /**
     * TODO
     */
    fun getRoute(
        nodes: ObservableList<RouteNode>,
        edges: ObservableList<RouteEdge>,
        zone: ObservableList<Zone>
    ): Observable<Boolean>

    /**
     * TODO
     */
    fun addEdge(
        edge : RouteEdge,
        edges : List<RouteNode>
    ): Observable<Boolean>

    /**
     * TODO
     */
    fun removeEdge(
        edge : RouteEdge,
        edges : List<RouteEdge>
    ): Observable<Pair<Boolean,Boolean>>
}