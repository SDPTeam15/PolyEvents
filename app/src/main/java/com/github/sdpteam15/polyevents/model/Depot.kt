package com.github.sdpteam15.polyevents.model

/**
 * Singleton class to denote a central depot, keeping track of available inventory
 *
 * @property inventory the current inventory of the depot, keeping track of each
 * item and its availabilities
 */
// TODO: refactor to database
object Depot {
    const val DEPOT_NAME: String = "mainDepot"

    // TODO: consider storing the list of items instead of just the number
    val inventory: MutableMap<ItemType, Int> = mutableMapOf()

    /**
     * Check if a certain item is available in the depot
     * @param itemType the item to check
     * @return true if the item's amount is bigger than 0
     */
    fun isAvailable(itemType: ItemType): Boolean =
        inventory.getOrDefault(itemType, 0) > 0
}