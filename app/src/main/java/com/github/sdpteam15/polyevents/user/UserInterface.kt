package com.github.sdpteam15.polyevents.user

import com.google.firebase.auth.FirebaseUser

/**
 *  Application user interface
 */
interface UserInterface {
    /**
     * Associates FirebaseUser
     */
    val FirebaseUser : FirebaseUser

    /***
     * Application user profile List
     */
    val ProfileList : List<ProfileInterface>

    /**
     * Application user name
     */
    val Name : String

    /**
     * Application user uid
     */
    val UID : String

    /**
     * Current application user profile
     */
    val CurrentProfile : ProfileInterface

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
    fun removeProfile(profile: ProfileInterface)
}