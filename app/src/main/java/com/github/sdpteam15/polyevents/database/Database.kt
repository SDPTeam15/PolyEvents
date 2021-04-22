package com.github.sdpteam15.polyevents.database

/**
 * Database
 */
object Database {
    private var mutableCurrentDatabase: DatabaseInterface? = null
    var currentDatabase: DatabaseInterface
        get() {
            mutableCurrentDatabase = mutableCurrentDatabase ?: FirestoreDatabaseProvider
            return mutableCurrentDatabase!!
        }
        set(value) {
            mutableCurrentDatabase = value
        }
}