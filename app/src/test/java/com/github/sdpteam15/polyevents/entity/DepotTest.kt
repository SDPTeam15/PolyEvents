package com.github.sdpteam15.polyevents.entity

import com.github.sdpteam15.polyevents.model.Depot
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DepotTest {
    val depot = Depot

    @Test
    fun testItemAvailabilityInDepot() {
        Depot.inventory["PLUG"] = 1
        assertTrue(Depot.isAvailable("PLUG"))

        Depot.inventory["PLUG"] = 0
        assertFalse(Depot.isAvailable("PLUG"))
    }

}