package com.github.sdpteam15.polyevents.model

import org.junit.Assert.*
import org.junit.Test

class DepotTest {
    val depot = Depot

    @Test
    fun testItemAvailabilityInDepot() {
        depot.inventory[Item.COCA] = 1
        assertTrue(depot.isAvailable(Item.COCA))

        depot.inventory[Item.COCA] = 0
        assertFalse(depot.isAvailable(Item.COCA))
    }

}