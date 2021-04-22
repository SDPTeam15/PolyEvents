package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ItemConstants.*
import com.github.sdpteam15.polyevents.model.Item

object ItemEntityAdapter {
    fun toItemEntity(documentData: MutableMap<String, Any?>, id: String): Pair<Item, Int> =
        Pair(
            Item(
                id,
                documentData[ITEM_NAME.value] as String?,
                documentData[ITEM_TYPE.value] as String),
                (documentData[ITEM_COUNT.value] as Long).toInt()
        )

    fun toItemDocument(item: Item, count: Int): HashMap<String, Any?> {
        val hash: HashMap<String, Any?> = hashMapOf(
            ITEM_NAME.value to item.itemName,
            ITEM_TYPE.value to item.itemType,
            ITEM_COUNT.value to count
        )
        if (item.itemId != null) {
            hash[ITEM_DOCUMENT_ID.value] = item.itemId
        }
        return hash
    }
}