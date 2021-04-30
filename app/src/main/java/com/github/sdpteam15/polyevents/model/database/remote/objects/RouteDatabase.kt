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
                            end.postValue(it.value, it.sender)
                        }
                }
        }
        return end
    }

    override fun addEdge(edge: RouteEdge, edges: List<RouteNode>): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun removeEdge(
        edge: RouteEdge,
        edges: List<RouteEdge>
    ): Observable<Pair<Boolean, Boolean>> {
        TODO("Not yet implemented")
    }
}