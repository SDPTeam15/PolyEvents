package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.model.database.local.LocalAdapter
import org.junit.Test
import kotlin.test.assertEquals

class LocalAdapterTest {
    @Test
    fun adapter() {
        val element =
            mapOf<String, Any?>(
                "null" to null,
                "String" to "String",
                "Boolean" to true,
                "Int" to 1,
                "Long" to 1L,
                "Float" to 0.1f,
                "Double" to 0.1,
                "Date" to LocalAdapter.SimpleDateFormat.parse("2001-01-01 01:01:01"),
                "Map" to mapOf(0 to 0),
                "List" to listOf(0, 1),
                "Set" to setOf(0, 1),
            )
        val id = "id"
        val collection = "collection"
        val doc = LocalAdapter.toDocument(element, id, collection)

        assertEquals(
            "{\"snull\":\"n\", \"sString\":\"sString\", \"sBoolean\":\"bT\", \"sInt\":\"i1\", \"sLong\":\"l1\", \"sFloat\":\"f0.1\", \"sDouble\":\"d0.1\", \"sDate\":\"a2001-01-01 01:01:01\", \"sMap\":\"M{\\\"i0\\\":\\\"i0\\\"}\", \"sList\":\"L{\\\"0\\\":\\\"i0\\\", \\\"1\\\":\\\"i1\\\", \\\":\\\":\\\"2\\\"}\", \"sSet\":\"S{\\\"0\\\":\\\"i0\\\", \\\"1\\\":\\\"i1\\\"}\"}",
            doc.data
        )
        assertEquals(
            "id",
            doc.id
        )
        assertEquals(
            "collection",
            doc.collection
        )

        val entity = LocalAdapter.fromDocument(doc)
        assertEquals(
            "{null=null, String=String, Boolean=true, Int=1, Long=1, Float=0.1, Double=0.1, Date=Mon Jan 01 01:01:01 CET 2001, Map={0=0}, List=[0, 1], Set=[0, 1]}",
            entity.first.toString()
        )
        assertEquals(
            "id",
            entity.second
        )
    }
}
