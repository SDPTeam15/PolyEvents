package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.objects.RouteDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseRoute: RouteDatabaseInterface {
    val nodes = mutableMapOf<String,RouteNode>()
    val edges = mutableMapOf<String,RouteEdge>()
    override fun getRoute(
        nodes: ObservableList<RouteNode>,
        edges: ObservableList<RouteEdge>,
        zone: ObservableList<Zone>
    ): Observable<Boolean> {
        nodes.addAll(this.nodes.mapValues { it.value.copy(id = it.key) }.values,FakeDatabase)
        edges.addAll(this.edges.mapValues { it.value.copy(id = it.key) }.values,FakeDatabase)
        return FakeDatabaseZone.getActiveZones(zone)
    }

    override fun updateEdges(
        newEdges: List<RouteEdge>,
        removeEdges: List<RouteEdge>,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean> {
        this.edges.putAll(newEdges.map { FakeDatabase.generateRandomKey() to it })
        removeEdges.forEach { removeEdge(it,edges,nodes) }
        return Observable(true)
    }

    override fun removeEdge(
        edge: RouteEdge,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean> {
        this.edges.remove(edge.id)
        nodes.addAll(this.nodes.mapValues { it.value.copy(id = it.key) }.values,FakeDatabase)
        edges.addAll(this.edges.mapValues { it.value.copy(id = it.key) }.values,FakeDatabase)
        return Observable(true)
    }
}