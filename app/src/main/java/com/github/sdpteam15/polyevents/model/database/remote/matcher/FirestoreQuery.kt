package com.github.sdpteam15.polyevents.model.database.remote.matcher

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.*


class FirestoreQuery(private val query: com.google.firebase.firestore.Query) : Query {
    override fun get() : Task<QuerySnapshot> = query.get()
        .onSuccessTask {
            Tasks.forResult(FirestoreQuerySnapshot(it))
    }

    override fun limit(limit: Long) = FirestoreQuery(query.limit(limit))
    override fun whereEqualTo(value: String, eventId: String) = FirestoreQuery(
        query.whereEqualTo(
            value,
            eventId
        )
    )
    override fun whereArrayContains(value: String, uid: String) = FirestoreQuery(
        query.whereArrayContains(
            value,
            uid
        )
    )
    override fun whereGreaterThan(value: String, localDateTimeToDate: Date) = FirestoreQuery(
        query.whereGreaterThan(
            value,
            localDateTimeToDate
        )
    )
    override fun whereGreaterThan(value: String, long: Long) = FirestoreQuery(
        query.whereGreaterThan(
            value,
            long
        )
    )
    override fun orderBy(value: String) = FirestoreQuery(query.orderBy(value))
}