package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.DatabaseUserInterface

/**
 *  Application user interface
 */
interface UserInterface {
    /**
     * Associates FirebaseUser
     */
    val databaseUser: DatabaseUserInterface

    /***
     * Application user profile List
     */
    val profileList: List<ProfileInterface>

    /**
     * Application user name
     */
    val name: String

    /**
     * User mail
     */
    val email: String

    /**
     * Application user uid
     */
    val uid: String

    /**
     * Application user rank
     */
    val rank: Rank

    /**
     * Current application user profile
     */
    val currentProfile: ProfileInterface

    /**
     * Delete all cache
     */
    fun removeCache()

    /**
     * Create a new Profile
     */
    fun newProfile(name: String)

    /**
     * Remove a Profile
     */
    fun removeProfile(profile: ProfileInterface): Boolean
}