package com.github.sdpteam15.polyevents.database

/**
 * Database constants
 */
object DatabaseObject {
    /**
     * Singleton of the DatabaseInterface
     */
    val Singleton : DatabaseInterface
        get() {
            return FakeDatabase()
        }
}