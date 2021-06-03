package com.github.sdpteam15.polyevents.matcher

import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.database.remote.matcher.CodeQuery
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Query
import com.github.sdpteam15.polyevents.model.database.remote.matcher.QueryDocumentSnapshot
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CodeQueryTest {
    lateinit var query: Query

    @Before
    fun setup() {
        query = CodeQuery.CodeQueryFromIterator(listOf(0, 1, 2).iterator()) {
            QueryDocumentSnapshot(
                mapOf(
                    "i" to it,
                    "l" to listOf(it),
                ),
                it.toString()
            )
        }
    }

    @Test
    fun get() {
        query.get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertEquals(i.id, i.data["i"].toString())
                }
                assertEquals(3, nb)
            }
        }
    }

    @Test
    fun limit() {
        query.limit(1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertEquals(i.id, i.data["i"].toString())
                }
                assertEquals(1, nb)
            }
        }
    }

    @Test
    fun whereEqualTo() {
        query.whereEqualTo("i", 1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertEquals(1, i.data["i"])
                }
                assertEquals(1, nb)
            }
        }
    }

    @Test
    fun whereNotEqualTo() {
        query.whereNotEqualTo("i", 1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertNotEquals(1, i.data["i"])
                }
                assertEquals(2, nb)
            }
        }
    }

    @Test
    fun whereArrayContains() {
        query.whereArrayContains("l", 1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertEquals(1, i.data["i"])
                }
                assertEquals(1, nb)
            }
        }
    }

    @Test
    fun whereGreaterThan() {
        query.whereGreaterThan("i", 1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertEquals(2, i.data["i"])
                }
                assertEquals(1, nb)
            }
        }
    }

    @Test
    fun whereLessThan() {
        query.whereLessThan("i", 1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertEquals(0, i.data["i"])
                }
                assertEquals(1, nb)
            }
        }
    }

    @Test
    fun whereGreaterThanOrEqualTo() {
        query.whereGreaterThanOrEqualTo("i", 1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertNotEquals(0, i.data["i"])
                }
                assertEquals(2, nb)
            }
        }
    }

    @Test
    fun whereLessThanOrEqualTo() {
        query.whereLessThanOrEqualTo("i", 1).get().observeOnce { p ->
            p.value.first.apply {
                var nb = 0
                for (i in it) {
                    ++nb
                    assertNotEquals(2, i.data["i"])
                }
                assertEquals(2, nb)
            }
        }
    }
}