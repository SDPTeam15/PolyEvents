package com.github.sdpteam15.polyevents.database

/**
 * Database
 */
object Database {
    private var mutableCurrentDatabase: DatabaseInterface? = null
    var currentDatabase: DatabaseInterface
        get() {
            mutableCurrentDatabase = mutableCurrentDatabase ?: FakeDatabase
            return mutableCurrentDatabase!!
        }
        set(value) {
            mutableCurrentDatabase = value
        }
}