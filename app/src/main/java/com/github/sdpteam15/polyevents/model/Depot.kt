package com.github.sdpteam15.polyevents.model

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
     * Check if a certain item is available in the depot
     * @param itemType the item to check
     * @return true if the item's amount is bigger than 0
     */
    fun isAvailable(itemType: Item): Boolean =
        inventory.getOrDefault(itemType, 0) > 0
}