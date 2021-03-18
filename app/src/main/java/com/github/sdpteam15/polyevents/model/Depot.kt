package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.exceptions.InsufficientAmountException
import java.lang.IllegalArgumentException

/**
 * Singleton class to denote a central depot, keeping track of available inventory
 *
 * @property inventory the current inventory of the depot, keeping track of each
 * item and its availabilities
 */
object Depot {
    const val DEPOT_NAME: String = "mainDepot"
    val inventory: MutableMap<Item, Int> = mutableMapOf()

    // TODO: refactor Inventory management maybe between Depot and Event
    /**
     * Set the amount of a certain item in the inventory of the depot.
     * Same functionality as the inventory of Events.
     * @param item: the item to add
     * @param amount the amount of the item to add.
     * @return the old previous amount of that item in the depot, or null if the item was not found
     */
    fun setItemAmount(item: Item, amount: Int): Int? {
        if (amount < 0) {
            throw IllegalArgumentException("Amount of items must >= 0")
        } else {
            return inventory.put(item, amount)
        }
    }

    /**
     * Add an amount of an item to the inventory of the depot
     * Same functionality as inventory of events.
     * @param item: the item to add
     * @param amount the amount of the item to add. By default is 1
     * @return the old previous amount of that item in the depot, or null if the item was not found
     */
    fun addItemToDepot(item: Item, amount: Int = 1): Int? {
        val currentAmount = inventory.getOrDefault(item, 0)
        return inventory.put(item, currentAmount + amount)
    }

    /**
     * Take an amount of some item from the inventory of the depot.
     *
     * @param item the item to take
     * @param amount amount of the item to take. By default is 1
     * @return the old previous amount of that item, or null if the item was not found
     */
    fun takeItemFromDepot(item: Item, amount: Int = 1): Int? {
        val currentAmount = inventory.getOrDefault(item, 0)
        if (amount > currentAmount) {
            throw InsufficientAmountException("There are only $currentAmount of $item available" +
                    "in the depot.")
        } else {
            return inventory.put(item, currentAmount - amount)
        }
    }

    /**
     * Check if a certain item is available in the depot
     * @param itemType the item to check
     * @return true if the item's amount is bigger than 0
     */
    fun isAvailable(itemType: Item): Boolean =
        inventory.getOrDefault(itemType, 0) > 0
}