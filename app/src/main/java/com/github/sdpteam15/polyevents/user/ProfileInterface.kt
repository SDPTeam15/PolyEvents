package com.github.sdpteam15.polyevents.user

/**
 * Interface of an application user profile
 */
interface ProfileInterface {
    /**
     * Profile name
     */
    var name: String

    /**
     * Associated user of the profile
     */
    val associatedUser: UserInterface?
}