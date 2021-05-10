package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.objects.RouteDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseRoute: RouteDatabaseInterface {
    override fun getRoute(
        nodes: ObservableList<RouteNode>,
        edges: ObservableList<RouteEdge>,
        zone: ObservableList<Zone>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun updateEdges(
        newEdges: List<RouteEdge>,
        removeEdges: List<RouteEdge>,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun removeEdge(
        edge: RouteEdge,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }
}