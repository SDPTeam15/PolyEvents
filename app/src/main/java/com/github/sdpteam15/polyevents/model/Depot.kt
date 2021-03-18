package com.github.sdpteam15.polyevents.model

/**
 * Singleton class to denote a central depot, keeping track of available inventory
 */
object Depot {
    const val DEPOT_NAME: String = "mainDepot"
    val inventory: MutableMap<Item, Int> = mutableMapOf()

    fun isAvailable(itemType: Item): Boolean =
        inventory.getOrDefault(itemType, 0) > 0
}