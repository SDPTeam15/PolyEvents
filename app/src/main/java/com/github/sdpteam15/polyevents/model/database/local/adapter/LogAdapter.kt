package com.github.sdpteam15.polyevents.model.database.local.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import java.time.LocalDateTime
import java.util.*

object LogAdapter {
    const val LAST_UPDATE = "LAST_UPDATE"
    const val IS_VALID = "IS_VALID"
}

/**
 * Add Log to an entity on the database from a other adapter.
 * LAST_UPDATE : the last time that the entity was updated
 * IS_VALID : if the entity is valid (removed or not)
 * @param adapter the adapter
 */
class LogAdapterToDocument<T>(private val adapter: AdapterToDocumentInterface<T>) :
    AdapterToDocumentInterface<T> {
    override fun toDocumentWithoutNull(element: T): Map<String, Any?> =
        toDocumentWithDate(element)

    override fun toDocument(element: T?): Map<String, Any?> =
        toDocumentWithDate(element)

    fun toDocumentWithDate(element: T?, date: LocalDateTime? = LocalDateTime.now()): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>(
            LogAdapter.LAST_UPDATE to HelperFunctions.localDateTimeToDate(
                date ?: LocalDateTime.now()
            ),
            LogAdapter.IS_VALID to (element != null),
        )
        if (element != null)
            result.putAll(adapter.toDocumentWithoutNull(element))
        return result
    }
}

/**
 * Add Log to an entity on the database from a other adapter
 * LAST_UPDATE : the last time that the entity was updated
 * IS_VALID : if the entity is valid (removed or not)
 * @param adapter the adapter
 */
class LogAdapterFromDocument<T>(private val adapter: AdapterFromDocumentInterface<T>) :
    AdapterFromDocumentInterface<T> {
    override fun fromDocument(document: Map<String, Any?>, id: String): T? =
        fromDocumentWithDate(document, id).first

    /**
     * Convert document data to a entity in our model and the Date of last update.
     * Data retrieved from the database are always a mutable map that maps strings (names of the fields of our entity) to their values,
     * which can be of any type..
     * @param document this is the data we retrieve from the document.
     * @return the corresponding entity and the date.
     */
    fun fromDocumentWithDate(document: Map<String, Any?>, id: String): Pair<T?, LocalDateTime?> {
        return if (document[LogAdapter.IS_VALID] as? Boolean != false)
            Pair(
                adapter.fromDocument(document, id),
                HelperFunctions.dateToLocalDateTime(document[LogAdapter.LAST_UPDATE] as? Date)
            )
        else Pair(null, null)
    }
}
