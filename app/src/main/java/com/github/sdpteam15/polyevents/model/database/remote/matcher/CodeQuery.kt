package com.github.sdpteam15.polyevents.model.database.remote.matcher

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

/**
 * Turn a code version of a query into a query
 */
class CodeQuery(private val getfun: () -> Task<QuerySnapshot>) : Query {
    companion object {
        fun <T> CodeQueryFromIterator(
            iterator: Iterator<T>,
            map: (T) -> QueryDocumentSnapshot
        ): Query = CodeQuery {
            Tasks.forResult(
                object : QuerySnapshot {
                    override fun iterator() = object : Iterator<QueryDocumentSnapshot> {
                        override fun hasNext() = iterator.hasNext()
                        override fun next() = map(iterator.next())
                    }
                }
            )
        }
    }

    override fun get(): Task<QuerySnapshot> = getfun()

    /**
     * add a filter to a query
     * @param filter the filter to apply
     */
    private fun filter(filter: (QueryDocumentSnapshot, Int) -> Boolean) = CodeQuery {
        get().onSuccessTask {
            Tasks.forResult(
                object : QuerySnapshot {
                    override fun iterator() =
                        object : Iterator<QueryDocumentSnapshot> {
                            var i = 0
                            var next: QueryDocumentSnapshot? = null
                            val iterator = it.iterator()
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
                }
            )
        }
    }

    override fun limit(limit: Long) =
        filter { _, it -> it < limit }

    override fun whereEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] == value }

    override fun whereNotEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] != value }

    override fun whereArrayContains(key: String, value: Any) =
        filter { it, _ -> (it.data[key] as? List<out Any>)?.contains(value) ?: false }

    override fun whereGreaterThan(key: String, value: Any) =
        filter { it, _ -> it.data[key] < value }

    override fun whereLessThan(key: String, value: Any) =
        filter { it, _ -> it.data[key] > value }

    override fun whereGreaterThanOrEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] >= value }

    override fun whereLessThanOrEqualTo(key: String, value: Any) =
        filter { it, _ -> it.data[key] <= value }

    override fun orderBy(key: String): Query {
        TODO("Not yet implemented")
    }
}

private operator fun Any?.compareTo(value: Any): Int {
    return (this as? Comparable<*>)?.compareTo(value) ?: 0
}
