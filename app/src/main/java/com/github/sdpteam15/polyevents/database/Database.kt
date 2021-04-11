package com.github.sdpteam15.polyevents.database

import android.util.Log

/**
 * Database
 */
object Database {
    var mutableCurrentDatabase: DatabaseInterface? = null
    var currentDatabase: DatabaseInterface
        get() {
            mutableCurrentDatabase = mutableCurrentDatabase ?: FakeDatabase
            return mutableCurrentDatabase!!
        }
        set(value) {
            mutableCurrentDatabase = value
        }
}