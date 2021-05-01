package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.*
import com.github.sdpteam15.polyevents.model.entity.Item

object ItemEntityAdapter : AdapterInterface<Pair<Item, Int>> {

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

    override fun toDocument(element: Pair<Item, Int>): HashMap<String, Any?> =
        toItemDocument(element.first, element.second)

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Pair<Item, Int> =
        Pair(
            Item(
                id,
                document[ITEM_NAME.value] as String?,
                document[ITEM_TYPE.value] as String
            ),
            (document[ITEM_COUNT.value] as Long).toInt()
        )
}