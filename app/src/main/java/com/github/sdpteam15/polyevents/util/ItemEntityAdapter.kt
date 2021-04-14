package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ITEM_COUNT
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ITEM_DOCUMENT_ID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ITEM_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ITEM_TYPE
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType

object ItemEntityAdapter {
    fun toItemEntity(documentData: MutableMap<String, Any?>, id: String): Pair<Item, Int> =
        Pair(
            Item(
                id,
                documentData[ITEM_NAME] as String,
                ItemType.valueOf(
                    documentData[ITEM_TYPE] as String
                )
            ), (documentData[ITEM_COUNT] as Long).toInt()
        )

    fun toItemDocument(item: Item, count: Int): HashMap<String, Any?> {
        val hash: HashMap<String, Any?> = hashMapOf(
            ITEM_NAME to item.itemName,
            ITEM_TYPE to item.itemType.name,
            ITEM_COUNT to count
        )
        if (item.itemId != null) {
            hash[ITEM_DOCUMENT_ID] = item.itemId
        }
        return hash
    }
}