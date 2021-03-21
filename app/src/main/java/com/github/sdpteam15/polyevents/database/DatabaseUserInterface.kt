package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.user.Rank

/**
 * DatabaseUser adapter interface
 */
interface DatabaseUserInterface {
    val displayName: String?
    val uid: String
    val email: String?
    val rank: Rank
}