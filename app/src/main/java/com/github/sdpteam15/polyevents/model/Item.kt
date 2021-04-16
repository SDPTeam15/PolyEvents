package com.github.sdpteam15.polyevents.model

/**
 * Entity model for an item, used in inventory management
 * for events.
 *
 * TODO: We attribute an id to each item, to keep track. Need to see that id will be formed.
 * TODO: Queries on Item: - Available items of certain itemType
 * TODO: Queries on Item: - Find location of certain item
 * TODO: Queries on Item: - Add item to event's inventory
 *
 * @property itemId the uid of the item, generally given by the admin
 * @property itemType the type of the item
 */
data class Item(
        val itemId: String?,
        val itemName: String,
        val itemType: ItemType
)