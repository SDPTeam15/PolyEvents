package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap

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
                    else {
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
        }
        return end
    }

    override fun updateEdges(
        newEdges: List<RouteEdge>,
        removeEdges: List<RouteEdge>,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean> {
        val end = Observable<Boolean>()

        //Get all new Node
        val listNode = mutableSetOf<RouteNode>()
        for (e in newEdges.toList()) {
            if (e.start != null && e.start!!.id == null)
                listNode.add(e.start!!)
            if (e.end != null && e.end!!.id == null)
                listNode.add(e.end!!)
        }

        //add new Node in db
        db.addListEntity(
            listNode.toList(),
            NODE_COLLECTION
        ).observeOnce {
            if (it.value.first) {
                nodes.addAll(listNode, db)
                for (n in listNode.withIndex())
                    n.value.id = it.value.second!![n.index]

                //add new Edges in db
                db.addListEntity(
                    newEdges,
                    EDGE_COLLECTION
                ).observeOnce {
                    if (it.value.first) {
                        edges.addAll(newEdges, db)
                        for (e in newEdges.withIndex())
                            e.value.id = it.value.second!![e.index]

                        //remove Edges no more used in db
                        db.deleteListEntity(
                            removeEdges.map { it.id!! },
                            EDGE_COLLECTION
                        ).observeOnce {
                            if (it.value.first)
                                edges.removeAll(removeEdges, db)
                            end.postValue(it.value.first, it.sender)
                        }
                    } else
                        end.postValue(false, it.sender)
                }
            } else
                end.postValue(false, it.sender)
        }
        return end
    }

    override fun removeEdge(
        edge: RouteEdge,
        edges: ObservableList<RouteEdge>,
        nodes: ObservableList<RouteNode>
    ): Observable<Boolean> {
        val end = Observable<Boolean>()

        //remove the edge in db
        db.deleteEntity(
            edge.id!!,
            EDGE_COLLECTION
        ).observeOnce {
            if (!it.value)
                end.postValue(it.value, it.sender)
            else {
                edges.remove(edge, it.sender)

                //get and remove from db Nodes no more used
                var startIsNotConnected = true
                var endIsNotConnected = true
                for (e in edges) {
                    startIsNotConnected =
                        startIsNotConnected && edge.start != e.start && edge.start != e.end
                    endIsNotConnected =
                        endIsNotConnected && edge.end != e.start && edge.end != e.end
                }
                val removeNodeID = mutableListOf<String>()
                val removeNode = mutableListOf<RouteNode>()
                if (startIsNotConnected) {
                    removeNode.add(edge.start!!)
                    removeNodeID.add(edge.startId!!)
                }
                if (endIsNotConnected) {
                    removeNode.add(edge.end!!)
                    removeNodeID.add(edge.endId!!)
                }
                db.deleteListEntity(
                    removeNodeID,
                    NODE_COLLECTION
                ).observeOnce {
                    nodes.removeAll(removeNode, it.sender)
                    end.postValue(it.value.first, it.sender)
                }
            }
        }
        return end
    }
}