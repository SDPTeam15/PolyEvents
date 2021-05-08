package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.adapter.RouteEdgeAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.RouteNodeAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ZoneAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.RouteDatabase
import com.github.sdpteam15.polyevents.model.entity.RouteEdge
import com.github.sdpteam15.polyevents.model.entity.RouteNode
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class RouteDatabaseTest {
    lateinit var mackRouteDatabase: RouteDatabase

    @Before
    fun setup() {
        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mackRouteDatabase = RouteDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun getRoute() {
        val nodes = ObservableList<RouteNode>()
        val edges = ObservableList<RouteEdge>()
        val zone = ObservableList<Zone>()

        HelperTestFunction.nextBoolean(true)
        HelperTestFunction.nextBooleanFun.add {
            nodes.addAll(
                listOf(
                    RouteNode("id1", 0.0, 0.0),
                    RouteNode("id2", 1.0, 1.0)
                )
            )
            true
        }
        HelperTestFunction.nextBooleanFun.add {
            edges.addAll(
                listOf(
                    RouteEdge("id0", "id1", "id2"),
                )
            )
            true
        }
        mackRouteDatabase.getRoute(nodes, edges, zone)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getListZone = HelperTestFunction.getListEntityQueue.poll()!!
        val getListNode = HelperTestFunction.getListEntityQueue.poll()!!
        val getListEdges = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(zone, getListZone.element)
        assertEquals(null, getListZone.ids)
        assertEquals(null, getListZone.matcher)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, getListZone.collection)
        assertEquals(ZoneAdapter, getListZone.adapter)

        assertEquals(nodes, getListNode.element)
        assertEquals(null, getListNode.ids)
        assertEquals(null, getListNode.matcher)
        assertEquals(DatabaseConstant.CollectionConstant.NODE_COLLECTION, getListNode.collection)
        assertEquals(RouteNodeAdapter, getListNode.adapter)

        assertEquals(null, getListEdges.ids)
        assertEquals(null, getListEdges.matcher)
        assertEquals(DatabaseConstant.CollectionConstant.EDGE_COLLECTION, getListEdges.collection)
        assertEquals(RouteEdgeAdapter, getListEdges.adapter)

        HelperTestFunction.nextBoolean(false)
        HelperTestFunction.nextBoolean(true)
        HelperTestFunction.nextBoolean(false)
        HelperTestFunction.nextBoolean(true)
        HelperTestFunction.nextBoolean(true)
        HelperTestFunction.nextBoolean(false)

        mackRouteDatabase.getRoute(nodes, edges, zone)
            .observeOnce { assert(!it.value) }.then.postValue(true)
        mackRouteDatabase.getRoute(nodes, edges, zone)
            .observeOnce { assert(!it.value) }.then.postValue(true)
        mackRouteDatabase.getRoute(nodes, edges, zone)
            .observeOnce { assert(!it.value) }.then.postValue(true)
    }

    @Test
    fun updateEdges() {
        val newEdges = mutableListOf<RouteEdge>()
        val removeEdges = mutableListOf<RouteEdge>()
        val edges = ObservableList<RouteEdge>()
        val nodes = ObservableList<RouteNode>()

        val n1 = RouteNode("n1", 0.0, 0.0)
        val n2 = RouteNode(null, 1.0, 1.0)
        val n3 = RouteNode(null, 2.0, 2.0)
        val n4 = RouteNode("n4", 3.0, 3.0)
        val n5 = RouteNode("n5", 4.0, 4.0)


        nodes.addAll(
            listOf(
                n1,
                n4,
                n5
            )
        )
        edges.addAll(
            listOf(
                RouteEdge.fromRouteNode(n1, n4, "e1"),
                RouteEdge.fromRouteNode(n4, n5, "e2")
            )
        )
        newEdges.add(RouteEdge.fromRouteNode(n1, n2))
        newEdges.add(RouteEdge.fromRouteNode(n3, n4))
        removeEdges.add(edges[0])

        HelperTestFunction.nextPairStringList(Pair(true, listOf("n2", "n3")))
        HelperTestFunction.nextPairStringList(Pair(true, listOf("e3", "e4")))
        HelperTestFunction.nextBoolean(true)
        mackRouteDatabase.updateEdges(newEdges, removeEdges, edges, nodes)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val addListNode = HelperTestFunction.addListEntityQueue.poll()!!
        val addListEdges = HelperTestFunction.addListEntityQueue.poll()!!
        val deleteListEdges = HelperTestFunction.deleteListEntityQueue.poll()!!


        assertEquals(2, addListNode.elements.size)
        assertEquals(DatabaseConstant.CollectionConstant.NODE_COLLECTION, addListNode.collection)
        assertEquals(RouteNodeAdapter, addListNode.adapter)

        assertEquals(newEdges, addListEdges.elements)
        assertEquals(DatabaseConstant.CollectionConstant.EDGE_COLLECTION, addListEdges.collection)
        assertEquals(RouteEdgeAdapter, addListEdges.adapter)

        assertEquals(removeEdges.map { it.id!! }, deleteListEdges.ids)
        assertEquals(
            DatabaseConstant.CollectionConstant.EDGE_COLLECTION,
            deleteListEdges.collection
        )

        assertEquals("n2", n2.id)
        assertEquals("n3", n3.id)

        assertEquals(3, edges.size)
        assertEquals(5, nodes.size)
    }

    @Test
    fun removeEdge() {
        val edges = ObservableList<RouteEdge>()
        val nodes = ObservableList<RouteNode>()

        val n1 = RouteNode("n1", 0.0, 0.0)
        val n2 = RouteNode("n2", 1.0, 1.0)

        nodes.addAll(
            listOf(n1, n2)
        )
        edges.addAll(
            listOf(
                RouteEdge.fromRouteNode(n1, n2, "e1"),
                RouteEdge.fromRouteNode(n2, n1, "e2")
            )
        )

        mackRouteDatabase.removeEdge(edges[0], edges, nodes)
            .observeOnce { assert(it.value) }.then.postValue(false)

        var deleteEdge = HelperTestFunction.deleteEntityQueue.poll()!!
        var deleteListEdges = HelperTestFunction.deleteListEntityQueue.poll()!!

        assertEquals("e1", deleteEdge.id)
        assertEquals(
            DatabaseConstant.CollectionConstant.EDGE_COLLECTION,
            deleteEdge.collection
        )

        assertEquals(0, deleteListEdges.ids.size)
        assertEquals(
            DatabaseConstant.CollectionConstant.NODE_COLLECTION,
            deleteListEdges.collection
        )

        mackRouteDatabase.removeEdge(edges[0], edges, nodes)
            .observeOnce { assert(it.value) }.then.postValue(false)

        deleteEdge = HelperTestFunction.deleteEntityQueue.poll()!!
        deleteListEdges = HelperTestFunction.deleteListEntityQueue.poll()!!

        assertEquals("e2", deleteEdge.id)
        assertEquals(
            DatabaseConstant.CollectionConstant.EDGE_COLLECTION,
            deleteEdge.collection
        )

        assertEquals(2, deleteListEdges.ids.size)
        assertEquals(
            DatabaseConstant.CollectionConstant.NODE_COLLECTION,
            deleteListEdges.collection
        )

        assert(edges.isEmpty())
    }
}