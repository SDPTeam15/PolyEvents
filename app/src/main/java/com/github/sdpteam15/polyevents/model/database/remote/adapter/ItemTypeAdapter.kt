package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.ITEM_TYPE

/**
 * A class for converting between item type entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
object ItemTypeAdapter : AdapterInterface<String> {
    override fun toDocument(element: String): HashMap<String, Any?> =
        hashMapOf(ITEM_TYPE.value to element)

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): String =
        document[ITEM_TYPE.value] as String

}