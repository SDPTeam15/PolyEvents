package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType

object ItemEntityAdapter {
    fun toItemEntity(documentData: MutableMap<String, Any?>): Pair<Item,Int> =
        Pair(Item(
            documentData[DatabaseConstant.ITEM_DOCUMENT_ID] as String,
            ItemType.valueOf(
                documentData[DatabaseConstant.ITEM_TYPE] as String
            )
        ),documentData[DatabaseConstant.ITEM_COUNT] as Int)

    fun toItemDocument(item: Item, count: Int): HashMap<String, Any?> =
        hashMapOf(
            DatabaseConstant.ITEM_DOCUMENT_ID to item.itemId,
            DatabaseConstant.ITEM_TYPE to item.itemType.name,
            DatabaseConstant.ITEM_COUNT to count
        )
}