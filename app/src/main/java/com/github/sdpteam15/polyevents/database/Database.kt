package com.github.sdpteam15.polyevents.database

import android.util.Log

/**
 * Database
 */
object Database {
    var mutableCurrentDatabase: DatabaseInterface? = null
    var currentDatabase: DatabaseInterface
        get() {
            mutableCurrentDatabase = mutableCurrentDatabase ?: FirestoreDatabaseProvider
            return mutableCurrentDatabase!!
        }
        set(value) {
            Log.d("Database", "New DB " + value.toString())
            mutableCurrentDatabase = value
        }
}