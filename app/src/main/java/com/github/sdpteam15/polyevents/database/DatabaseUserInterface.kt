package com.github.sdpteam15.polyevents.database

/**
 * DatabaseUser adapter interface
 */
interface DatabaseUserInterface {
    val displayName: String?
    val uid: String
    val email: String?
}