package com.github.sdpteam15.polyevents.model.database.local.room

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/**
 * A class for converting between user entities in our code and GenericEntity in the database.
 */
class LocalAdapter<T>(adapter: AdapterInterface<T>) {
    private var toDocument: LocalAdapterToDocument<T> =
        LocalAdapterToDocument(adapter)
    private var fromDocument: LocalAdapterFromDocument<T> =
        LocalAdapterFromDocument(adapter)

    /**
     * Convert an entity to a GenericEntity
     * @param element the entity we're converting
     * @param id id in remote database
     * @param date from last update
     * @return a GenericEntity fields to their values
     */
    fun toDocument(element: T?, id: String, date: LocalDateTime?): GenericEntity? =
        toDocument.toDocument(element, id, date)

    /**
     * Convert document data to a user entity in our model.
     * @param document this is the data we retrieve from the document.
     * @return the corresponding userEntity.
     */
    fun fromDocument(document: GenericEntity) =
        fromDocument.fromDocument(document)

    companion object {
        @SuppressLint("SimpleDateFormat")
        val SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }
}

/**
 * A class for converting between user entities in our code and GenericEntity in the database.
 */
class LocalAdapterToDocument<T>(private val adapter: AdapterToDocumentInterface<T>) {

    /**
     * Convert an entity to a GenericEntity
     * @param element the entity we're converting
     * @param id id in remote database
     * @param date from last update
     * @return a GenericEntity fields to their values
     */
    fun toDocument(element: T?, id: String, date: LocalDateTime? = null): GenericEntity? {
        val result = adapter.toDocument(element)
        return if (result == null) null
        else GenericEntity(
            id,
            data = fromMap(result),
            update_time = fromDate(
                HelperFunctions.localDateTimeToDate(
                    date ?: LocalDateTime.now()
                )!!
            )
        )
    }

    private fun fromAny(element: Any?) = when (element) {
        null -> "n"
        is String -> "s${fromString(element)}"
        is Boolean -> "b${fromBoolean(element)}"
        is Int -> "i${fromInt(element)}"
        is Long -> "l${fromLong(element)}"
        is Float -> "f${fromFloat(element)}"
        is Double -> "d${fromDouble(element)}"
        is Date -> "a${fromDate(element)}"
        is Map<*, *> -> "M${fromMap(element)}"
        is List<*> -> "L${fromList(element)}"
        is Set<*> -> "S${fromSet(element)}"
        else -> "n"
    }

    private fun fromString(element: String) = element
    private fun fromBoolean(element: Boolean) = if (element) "T" else "F"
    private fun fromInt(element: Int) = element.toString()
    private fun fromLong(element: Long) = element.toString()
    private fun fromFloat(element: Float) = element.toString()
    private fun fromDouble(element: Double) = element.toString()
    private fun fromDate(element: Date) = LocalAdapter.SimpleDateFormat.format(element).toString()

    private fun fromMap(element: Map<*, *>): String {
        val mapResult = mutableMapOf<String, String>()
        for (key in element.keys)
            mapResult[fromAny(key)] = fromAny(element[key])
        return JSONObject(mapResult as Map<String, String>).toString()
    }

    private fun fromList(element: List<*>): String {
        val mapResult = mutableMapOf<String, String>()
        var i = 0
        for (e in element)
            mapResult[(i++).toString()] = fromAny(e)
        return JSONObject(mapResult as Map<String, String>).accumulate(":", element.size).toString()
    }

    private fun fromSet(element: Set<*>): String {
        val mapResult = mutableMapOf<String, String>()
        var i = 0
        for (e in element)
            mapResult[(i++).toString()] = fromAny(e)
        return JSONObject(mapResult as Map<String, String>).toString()
    }
}

/**
 * A class for converting between user entities in our code and GenericEntity in the database.
 */
class LocalAdapterFromDocument<T>(private val adapter: AdapterFromDocumentInterface<T>) {
    /**
     * Convert document data to a user entity in our model.
     * @param document this is the data we retrieve from the document.
     * @return the corresponding userEntity.
     */
    fun fromDocument(document: GenericEntity): T? =
        adapter.fromDocument(toMap(document.data!!) as Map<String, Any?>, document.id)

    companion object {
        fun toAny(element: String): Any? {
            return when (element[0]) {
                'n' -> null

                's' -> toString(element.drop(1))
                'b' -> toBoolean(element.drop(1))
                'i' -> toInt(element.drop(1))
                'l' -> toLong(element.drop(1))
                'f' -> toFloat(element.drop(1))
                'd' -> toDouble(element.drop(1))
                'a' -> toDate(element.drop(1))

                'M' -> toMap(element.drop(1))
                'L' -> toList(element.drop(1))
                'S' -> toSet(element.drop(1))

                else -> null
            }
        }

        fun toString(element: String) = element
        fun toBoolean(element: String) = element == "T"
        fun toInt(element: String) = element.toInt()
        fun toLong(element: String) = element.toLong()
        fun toFloat(element: String) = element.toFloat()
        fun toDouble(element: String) = element.toDouble()
        fun toDate(element: String) = LocalAdapter.SimpleDateFormat.parse(element)

        fun toMap(element: String): Map<Any?, Any?> {
            val mapResult = mutableMapOf<Any?, Any?>()
            val jsonObject = JSONObject(element)
            for (key in jsonObject.keys())
                mapResult[toAny(key)] = toAny(jsonObject.getString(key))
            return mapResult
        }

        fun toList(element: String): List<Any?> {
            val listResult = mutableListOf<Any?>()
            val jsonObject = JSONObject(element)
            for (key in 0 until jsonObject.getInt(":"))
                listResult.add(toAny(jsonObject.getString(key.toString())))
            return listResult
        }

        fun toSet(element: String): Set<Any?> {
            val setResult = mutableSetOf<Any?>()
            val jsonObject = JSONObject(element)
            for (key in jsonObject.keys())
                setResult.add(toAny(jsonObject.getString(key)))
            return setResult
        }
    }
}