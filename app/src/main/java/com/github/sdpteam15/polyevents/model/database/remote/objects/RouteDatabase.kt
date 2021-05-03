package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class RouteDatabase(private val db: DatabaseInterface) : RouteDatabaseInterface {
    override fun getRoute(
        nodes: ObservableList<RouteNode>,
        edges: ObservableList<RouteEdge>,
        zone: ObservableList<Zone>
    ): Observable<Boolean> {
        val end = Observable<Boolean>()
        db.getListEntity(
            zone,
            null,
            null,
            ZONE_COLLECTION
        ).observeOnce {
            if (!it.value)
                end.postValue(it.value, it.sender)
            else
                db.getListEntity(
                    nodes,
                    null,
                    null,
                    NODE_COLLECTION
                ).observeOnce {
                    if (!it.value)
                        end.postValue(it.value, it.sender)
                    else
                        db.getListEntity(
                            edges,
                            null,
                            null,
                            EDGE_COLLECTION
                        ).observeOnce {
                            if (it.value) {
                                val keyToNode =
                                    nodes.groupOnce { it.id }.then.mapOnce { it[0] }.then
                                for (e in edges) {
                                    e.start = keyToNode[e.startId]
                                    e.end = keyToNode[e.endId]
                                }
                            }
                            end.postValue(it.value, it.sender)
                        }
                }
        }
        return end
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
        edges: ObservableList<RouteEdge>
    ): Observable<Boolean> {
        val end = Observable<Boolean>()
        db.deleteEntity(
            edge.id!!,
            EDGE_COLLECTION
        ).observeOnce {
            if (!it.value)
                end.postValue(it.value, it.sender)
            else {
                edges.remove(edge, it.sender)
                var startIsNotConnected = true
                var endIsNotConnected = true
                for (e in edges) {
                    startIsNotConnected =
                        startIsNotConnected && edge.start != e.start && edge.start != e.end
                    endIsNotConnected =
                        endIsNotConnected && edge.end != e.start && edge.end != e.end
                }
                if (startIsNotConnected) {
                    TODO()
                }
                if (endIsNotConnected) {
                    TODO()
                }
            }
        }
        return end
    }
}