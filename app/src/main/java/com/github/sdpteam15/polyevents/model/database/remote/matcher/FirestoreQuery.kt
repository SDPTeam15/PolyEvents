package com.github.sdpteam15.polyevents.model.database.remote.matcher

import com.github.sdpteam15.polyevents.model.observable.Observable

/**
 * Transform a firestore.Query to a Query
 */
class FirestoreQuery(private val query: com.google.firebase.firestore.Query) : Query {
    override fun get(): Observable<Pair<QuerySnapshot?, Exception?>> {
        val observable = Observable<Pair<QuerySnapshot?, Exception?>>()
        query
            .get()
            .addOnSuccessListener {
                observable.postValue(
                    Pair(FirestoreQuerySnapshot(it), null),
                    this
                )
            }
            .addOnFailureListener {
                observable.postValue(
                    Pair(null, it),
                    this
                )
            }
        return observable
    }

    override fun limit(limit: Long) = FirestoreQuery(query.limit(limit))
    override fun whereEqualTo(key: String, value: Any) = FirestoreQuery(
        query.whereEqualTo(key, value)
    )

    override fun whereNotEqualTo(key: String, value: Any) = FirestoreQuery(
        query.whereNotEqualTo(key, value)
    )

    override fun whereArrayContains(key: String, value: Any) = FirestoreQuery(
        query.whereArrayContains(key, value)
    )

    override fun whereGreaterThan(key: String, value: Any) = FirestoreQuery(
        query.whereGreaterThan(key, value)
    )

    override fun whereLessThan(key: String, value: Any) = FirestoreQuery(
        query.whereLessThan(key, value)
    )

    override fun whereGreaterThanOrEqualTo(key: String, value: Any) = FirestoreQuery(
        query.whereGreaterThanOrEqualTo(key, value)
    )

    override fun whereLessThanOrEqualTo(key: String, value: Any) = FirestoreQuery(
        query.whereLessThanOrEqualTo(key, value)
    )
}