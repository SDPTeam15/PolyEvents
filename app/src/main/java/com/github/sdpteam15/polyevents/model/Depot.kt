package com.github.sdpteam15.polyevents.model

/**
 * Singleton class to denote a central depot, keeping track of available inventory
 */
object Depot {
    const val DEPOT_NAME: String = "mainDepot"
    val inventory: MutableMap<Item, Int> = mutableMapOf()

    fun isAvailable(itemType: Item): Boolean =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            inventory.getOrDefault(itemType, 0) > 0
        } else {
            val temp = inventory.get(itemType)
            val amount: Int = if (temp == null) 0 else temp
            amount > 0
        }
}