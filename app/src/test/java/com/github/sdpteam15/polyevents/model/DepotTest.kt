package com.github.sdpteam15.polyevents.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DepotTest {
    val depot = Depot

    @Test
    fun testItemAvailabilityInDepot() {
        depot.inventory["PLUG"] = 1
        assertTrue(depot.isAvailable("PLUG"))

        depot.inventory["PLUG"] = 0
        assertFalse(depot.isAvailable("PLUG"))
    }

}