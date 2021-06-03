package com.github.sdpteam15.polyevents.model.database.remote.matcher

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.google.type.LatLng
import java.util.*

/**
 * Turn a code version of a query into a query
 */
class CodeQuery(private val getfun: () -> Observable<Pair<QuerySnapshot?, Exception?>>) : Query {
    companion object {
        fun <T> CodeQueryFromIterator(
            iterator: Iterator<T>,
            map: (T) -> QueryDocumentSnapshot?
        ): Query = CodeQuery {
            val observable = Observable<Pair<QuerySnapshot?, Exception?>>()
            HelperFunctions.run(Runnable {
                observable.postValue(
                    Pair(
                        object : QuerySnapshot {
                            override fun iterator() = object : Iterator<QueryDocumentSnapshot> {
                                var nextVal: QueryDocumentSnapshot? = null

                                override fun hasNext(): Boolean {
                                    while (nextVal == null && iterator.hasNext())
                                        nextVal = map(iterator.next())
                                    return nextVal != null
                                }

                                override fun next(): QueryDocumentSnapshot {
                                    if (hasNext()) {
                                        val result = nextVal!!
                                        nextVal = null
                                        return result
                                    }
                                    return (map(iterator.next())!!)
                                }
                            }
                        }, null
                    ), this
                )
            })
            observable
        }
    }

    override fun get(): Observable<Pair<QuerySnapshot?, Exception?>> = getfun()

    /**
     * add a filter to a query
     * @param filter the filter to apply
     */
    private fun filter(filter: (QueryDocumentSnapshot, Int) -> Boolean) = CodeQuery {
        val observable = Observable<Pair<QuerySnapshot?, Exception?>>()
        get().observeOnce {
            observable.postValue(
                it.value.first.apply(it.value) { qs ->
                    Pair(
                        object : QuerySnapshot {
                            override fun iterator() =
                                object : Iterator<QueryDocumentSnapshot> {
                                    var i = 0
                                    var next: QueryDocumentSnapshot? = null
                                    val iterator = qs.iterator()
                                    override fun hasNext(): Boolean {
                                        if (next != null)
                                            return true
                                        if (iterator.hasNext()) {
                                            while (iterator.hasNext()) {
                                                val result = iterator.next()
                                                if (filter(result, i)) {
                                                    next = result
                                                    return true
                                                }
                                            }
                                        }
                                        return false
                                    }

                                    override fun next(): QueryDocumentSnapshot {
                                        val result = next ?: iterator.next()
                                        ++i
                                        next = null
                                        return result
                                    }
                                }
                        }, null
                    )
                }, it.sender
            )
        }
        observable
    }

    override fun limit(limit: Long) =
        filter { _, it -> it < limit }

    override fun whereEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] == value }

    override fun whereNotEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] != value }

    @Suppress("UNCHECKED_CAST")
    override fun whereArrayContains(key: String, value: Any) =
        filter { it, _ -> (it.data[key] as? List<out Any>)?.contains(value) ?: false }

    override fun whereGreaterThan(key: String, value: Any) =
        filter { it, _ -> it.data[key] > value }

    override fun whereLessThan(key: String, value: Any) =
        filter { it, _ -> it.data[key] < value }

    override fun whereGreaterThanOrEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] >= value }

    override fun whereLessThanOrEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] <= value }
}

private operator fun Any?.compareTo(value: Any): Int =
    when (this) {
        is String -> this.compareTo(value as String)
        is Boolean -> this.compareTo(value as Boolean)
        is Int -> this.compareTo(value as Int)
        is Long -> this.compareTo(value as Long)
        is Float -> this.compareTo(value as Float)
        is Double -> this.compareTo(value as Double)
        is Date -> this.compareTo(value as Date)
        is LatLng -> this.compareTo(value as LatLng)
        else -> 0
    }
