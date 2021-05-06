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
                    else {
                        val tempEdges = ObservableList<RouteEdge>()
                        db.getListEntity(
                            tempEdges,
                            null,
                            null,
                            EDGE_COLLECTION
                        ).observeOnce {
                            if (it.value) {
                                val keyToNode =
                                    nodes.groupOnce { it.id }.then.mapOnce { it[0] }.then
                                for (e in tempEdges) {
                                    e.start = keyToNode[e.startId]
                                    e.end = keyToNode[e.endId]
                                }
                            }
                            edges.clear(it.sender)
                            edges.addAll(tempEdges, it.sender)
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
        val listNode = mutableListOf<RouteNode>()
        for (e in newEdges.toList()) {
            if (e.start != null && e.start!!.id == null && e.start!! !in listNode)
                listNode.add(e.start!!)
            if (e.end != null && e.end!!.id == null && e.end!! !in listNode)
                listNode.add(e.end!!)
        }
        db.addListEntity(
            listNode,
            NODE_COLLECTION
        ).observeOnce {
            if (it.value.first) {
                nodes.addAll(listNode, db)
                for (n in listNode.withIndex())
                    n.value.id = it.value.second!![n.index]
                db.addListEntity(
                    newEdges,
                    EDGE_COLLECTION
                ).observeOnce {
                    if (it.value.first) {
                        edges.addAll(newEdges, db)
                        for (e in newEdges.withIndex())
                            e.value.id = it.value.second!![e.index]
                        db.deleteListEntity(
                            removeEdges.map { it.id!! },
                            EDGE_COLLECTION
                        ).observeOnce {
                            if (it.value)
                                edges.removeAll(removeEdges, db)
                            end.postValue(it.value, it.sender)
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
                val removeNode = mutableListOf<String>()
                if (startIsNotConnected)
                    removeNode.add(edge.startId!!)
                if (endIsNotConnected)
                    removeNode.add(edge.endId!!)
                db.deleteListEntity(
                    removeNode,
                    NODE_COLLECTION
                ).observeOnce {
                    end.postValue(it.value, it.sender)
                }
            }
        }
        return end
    }
}