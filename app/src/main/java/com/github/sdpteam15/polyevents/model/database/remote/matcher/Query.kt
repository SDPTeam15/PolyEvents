package com.github.sdpteam15.polyevents.model.database.remote.matcher

import com.google.android.gms.tasks.Task
import java.util.*

interface Query {
    fun get(): Task<QuerySnapshot>

    fun limit(limit: Long): Query
    fun whereEqualTo(key: String, value: Any): Query
    fun whereArrayContains(key: String, value: Any): Query
    fun whereGreaterThan(key: String, value: Any): Query
}