package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import com.github.sdpteam15.polyevents.user.User

/**
 * Database interface
 */
interface DatabaseInterface {
    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param user user for database access
     */
    fun getListProfile(uid : String = (User.CurrentUser as UserInterface).UID,
        user : UserInterface = User.CurrentUser as UserInterface) : List<ProfileInterface>

    /**
     * Add profile to a user
     * @param profile profile to add
     * @param uid uid
     * @param user user for database access
     */
    fun addProfile(profile: ProfileInterface, uid : String = (User.CurrentUser as UserInterface).UID,
                   user : UserInterface = User.CurrentUser as UserInterface) : Boolean

    /**
     * Remove profile from a user
     * @param profile profile to remove
     * @param uid uid
     * @param user user for database access
     */
    fun removeProfile(profile: ProfileInterface, uid : String = (User.CurrentUser as UserInterface).UID,
                   user : UserInterface = User.CurrentUser as UserInterface) : Boolean
}