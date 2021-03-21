package com.github.sdpteam15.polyevents.user

/**
 * Interface of an application user profile
 */
interface ProfileInterface {

    /**
     *  Profile id
     */
    var id: String

    /**
     * Profile name
     */
    var name: String


    /**
     *  Profile rank
     */
    var rank: Rank

    /**
     * Associated user of the profile
     */
    val associatedUser: UserInterface?
}