package com.github.sdpteam15.polyevents.model

import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalArgumentException

class DepotTest {
    val depot = Depot

    @Test
    fun testAddingItemsToDepot() {
        depot.addItemToDepot(Item.COCA)
        assertEquals(depot.inventory[Item.COCA], 1)

        depot.addItemToDepot(Item.COCA, 2)
        assertEquals(depot.inventory[Item.COCA], 3)
    }

    @Test
    fun testSettingItemInDepotInventory() {
        depot.setItemAmount(Item.COCA, 2)
        assertEquals(depot.inventory[Item.COCA], 2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSettingItemWithNegativeAmountValueThrowsException() {
        depot.setItemAmount(Item.COCA, -2)
    }

    @Test
    fun testRemovingItemFromDepotInventory() {
        depot.setItemAmount(Item.COCA, 3)
        depot.takeItemFromDepot(Item.COCA)
        assertEquals(depot.inventory[Item.COCA], 2)

        depot.takeItemFromDepot(Item.COCA, 2)
        assertEquals(depot.inventory[Item.COCA], 0)
    }

    @Test
    fun testItemAvailabilityInDepot() {
        depot.setItemAmount(Item.COCA, 1)
        assertTrue(depot.isAvailable(Item.COCA))

        depot.takeItemFromDepot(Item.COCA)
        assertFalse(depot.isAvailable(Item.COCA))
    }

}