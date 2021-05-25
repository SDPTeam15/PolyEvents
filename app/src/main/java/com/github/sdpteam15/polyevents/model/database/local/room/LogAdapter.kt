package com.github.sdpteam15.polyevents.model.database.local.room

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import java.time.LocalDateTime

class LogAdapter<T>(adapter: AdapterInterface<T>) : AdapterInterface<T> {
    companion object {
        const val LAST_UPDATE = "LAST_UPDATE"
        const val IS_VALID = "IS_VALID"
    }

    private var toDocument: AdapterToDocumentInterface<T> =
        LogAdapterToDocument(adapter)
    private var fromDocument: AdapterFromDocumentInterface<T> =
        LogAdapterFromDocument(adapter)

    override fun toDocument(element: T?): Map<String, Any?>? =
        toDocument.toDocument(element)

    override fun fromDocument(document: Map<String, Any?>, id: String): T? =
        fromDocument.fromDocument(document, id)
}

class LogAdapterToDocument<T>(private val adapter: AdapterToDocumentInterface<T>) :
    AdapterToDocumentInterface<T> {
    override fun toDocument(element: T?): Map<String, Any?>? {
        val result = mutableMapOf<String, Any?>(
            LogAdapter.LAST_UPDATE to HelperFunctions.localDateTimeToDate(LocalDateTime.now()),
            LogAdapter.IS_VALID to (element != null),
        )
        if(element != null)
            result.putAll(adapter.toDocument(element)!!)
        return result
    }
}

class LogAdapterFromDocument<T>(private val adapter: AdapterFromDocumentInterface<T>) :
    AdapterFromDocumentInterface<T> {
    override fun fromDocument(document: Map<String, Any?>, id: String): T? {
        return if(document[LogAdapter.IS_VALID] as? Boolean == true)
            adapter.fromDocument(document, id)
        else null
    }
}
