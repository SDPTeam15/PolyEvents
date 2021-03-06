package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions.toInt
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.*
import com.github.sdpteam15.polyevents.model.entity.Item

/**
 * A class for converting between item entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 *
 * The query to the database returns a Triple<Item,Int,Int> :
 *      The Item, its total amount set by an admin, and the remaining amount
 */
object ItemEntityAdapter : AdapterInterface<Triple<Item, Int, Int>> {

    fun toItemDocument(item: Item, totalCount: Int, remainingCount: Int): HashMap<String, Any?> {
        return hashMapOf(
            ITEM_NAME.value to item.itemName,
            ITEM_TYPE.value to item.itemType,
            ITEM_TOTAL.value to totalCount,
            ITEM_REMAINING.value to remainingCount,
        )
    }

    override fun toDocumentWithoutNull(element: Triple<Item, Int, Int>): HashMap<String, Any?> =
        toItemDocument(element.first, element.second, element.third)

    override fun fromDocument(
        document: Map<String, Any?>,
        id: String
    ): Triple<Item, Int, Int> =
        Triple(
            Item(
                id,
                document[ITEM_NAME.value] as String?,
                document[ITEM_TYPE.value] as String
            ),
            document[ITEM_TOTAL.value].toInt()!!,
            document[ITEM_REMAINING.value].toInt()!!
        )
}