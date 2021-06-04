package com.github.sdpteam15.polyevents.model.entity

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Entity model for an item, used in inventory management
 * for events.
 *
 * @property itemId the uid of the item, generally given by the admin
 * @property itemType the type of the item
 * @property itemName The name of the item
 */
@IgnoreExtraProperties
data class Item(
    var itemId: String?,
    val itemName: String?,
    val itemType: String
)