package com.github.sdpteam15.polyevents.model.database.remote.matcher

import com.google.android.gms.tasks.Task
import java.util.*

interface Query {
    fun get(): Task<QuerySnapshot>

    fun limit(limit: Long): Query
    fun whereEqualTo(value: String, eventId: String): Query
    fun whereArrayContains(value: String, uid: String): Query
    fun whereGreaterThan(value: String, localDateTimeToDate: Date): Query
    fun whereGreaterThan(value: String, long: Long): Query
    fun orderBy(value: String): Query
}