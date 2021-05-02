package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.ITEM_TYPE

object ItemTypeAdapter : AdapterInterface<String> {
    override fun toDocument(element: String): HashMap<String, Any?> =
        hashMapOf(ITEM_TYPE.value to element)

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): String =
        document[ITEM_TYPE.value] as String

}