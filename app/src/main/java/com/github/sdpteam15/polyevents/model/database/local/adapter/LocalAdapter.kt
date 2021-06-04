package com.github.sdpteam15.polyevents.model.database.local.adapter

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/**
 * A class for converting between user entities in our code and GenericEntity in the database.
 */
@Suppress("UNCHECKED_CAST")
object LocalAdapter {
    @SuppressLint("SimpleDateFormat")
    val SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * Convert an entity to a GenericEntity
     * @param element the document we're converting
     * @param id id in remote database
     * @param collection collection of the element
     * @param date from last update
     * @return a GenericEntity fields to their values
     */
    fun toDocument(
        element: Map<String, Any?>,
        id: String,
        collection: String?,
        date: LocalDateTime? = null
    ) = GenericEntity(
        id,
        collection = collection ?: "",
        data = fromMap(element),
        update_time = fromDate(
            HelperFunctions.localDateTimeToDate(
                date ?: LocalDateTime.now()
            )!!
        )
    )

    /**
     * Convert document data to a user entity in our model.
     * @param document this is the data we retrieve from the document.
     * @return the corresponding userEntity.
     */
    fun fromDocument(document: GenericEntity) =
        Pair(toMap(document.data!!) as Map<String, Any?>, document.id)

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
    private fun fromDate(element: Date) = SimpleDateFormat.format(element).toString()

    private fun fromMap(element: Map<*, *>): String {
        val mapResult = mutableMapOf<String, String>()
        for (key in element.keys)
            mapResult[fromAny(key)] = fromAny(element[key])
        return (mapResult as Map<String, String>).toSerString()
    }

    private fun fromList(element: List<*>): String {
        val mapResult = mutableMapOf<String, String>()
        var i = 0
        for (e in element)
            mapResult[(i++).toString()] = fromAny(e)
        mapResult[":"] = i.toString()
        return (mapResult as Map<String, String>).toSerString()
    }

    private fun fromSet(element: Set<*>): String {
        val mapResult = mutableMapOf<String, String>()
        var i = 0
        for (e in element)
            mapResult[(i++).toString()] = fromAny(e)
        return (mapResult as Map<String, String>).toSerString()
    }

    private fun toAny(element: String): Any? {
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

    private fun toString(element: String) = element
    private fun toBoolean(element: String) = element == "T"
    private fun toInt(element: String) = element.toInt()
    private fun toLong(element: String) = element.toLong()
    private fun toFloat(element: String) = element.toFloat()
    private fun toDouble(element: String) = element.toDouble()
    private fun toDate(element: String) = SimpleDateFormat.parse(element)

    private fun toMap(element: String): Map<Any?, Any?> {
        val mapResult = mutableMapOf<Any?, Any?>()
        val map = element.toSerMap()
        for (key in map.keys)
            mapResult[toAny(key)] = toAny(map[key]!!)
        return mapResult
    }

    private fun toList(element: String): List<Any?> {
        val listResult = mutableListOf<Any?>()
        val map = element.toSerMap()
        for (key in 0 until map[":"]!!.toInt())
            listResult.add(toAny(map[key.toString()]!!))
        return listResult
    }

    private fun toSet(element: String): Set<Any?> {
        val setResult = mutableSetOf<Any?>()
        val map = element.toSerMap()
        for (key in map.keys)
            setResult.add(toAny(map[key]!!))
        return setResult
    }

    /**
     * serialize a Map<String, String> to a String
     * @param this the Map to serialize
     */
    fun Map<String, String>.toSerString(): String {
        if (this.isEmpty())
            return "{}"
        var s = ""
        for (key in this.keys)
            s += ", \"${key.replace("\"", "\\\"")}\":\"${this[key]!!.replace("\"", "\\\"")}\""
        return "{" + s.substring(2) + "}"
    }

    /**
     * deserialize a String to a Map<String, String>
     * @param this the String to deserialize
     */
    fun String.toSerMap(): Map<String, String> {
        if (this.length <= 2 || this[0] != '{' || this[this.length - 1] != '}')
            return mapOf()
        val map = mutableMapOf<String, String>()
        for (paire in this.substring(2, this.length - 2).split("\", \"")) {
            val splited = paire.split("\":\"")
            map[splited[0].replace("\\\"", "\"")] = splited[1].replace("\\\"", "\"")
        }
        return map
    }
}