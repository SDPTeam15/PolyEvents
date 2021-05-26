package com.github.sdpteam15.polyevents.model.database.local.room

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import java.time.LocalDateTime
import java.util.*

object LogAdapter {
    const val LAST_UPDATE = "LAST_UPDATE"
    const val IS_VALID = "IS_VALID"
}

class LogAdapterToDocument<T>(private val adapter: AdapterToDocumentInterface<T>) :
    AdapterToDocumentInterface<T> {
    override fun toDocument(element: T?): Map<String, Any?> =
        toDocumentWithDate(element, null)

    fun toDocumentWithDate(element: T?, date: LocalDateTime?): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>(
            LogAdapter.LAST_UPDATE to HelperFunctions.localDateTimeToDate(
                date ?: LocalDateTime.now()
            ),
            LogAdapter.IS_VALID to (element != null),
        )
        if (element != null)
            result.putAll(adapter.toDocument(element)!!)
        return result
    }
}

class LogAdapterFromDocument<T>(private val adapter: AdapterFromDocumentInterface<T>) :
    AdapterFromDocumentInterface<T> {
    override fun fromDocument(document: Map<String, Any?>, id: String): T? =
        fromDocumentWithDate(document, id).first

    fun fromDocumentWithDate(document: Map<String, Any?>, id: String): Pair<T?, LocalDateTime?> {
        return if (document[LogAdapter.IS_VALID] as? Boolean != false)
            Pair(
                adapter.fromDocument(document, id),
                HelperFunctions.dateToLocalDateTime(document[LogAdapter.LAST_UPDATE] as? Date)
            )
        else Pair(null, null)
    }
}
