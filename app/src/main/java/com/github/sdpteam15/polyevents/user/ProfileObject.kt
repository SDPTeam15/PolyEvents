package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.user.UserObject.CurrentUser

/**
 * Application user profile constants
 */
object ProfileObject{
    private var default : ProfileInterface? = null

    /**
     * Defaut profile of the application
     */
    val Defaut : ProfileInterface
        get() {
            if(default == null)
                default = Profile("default")
        return default as ProfileInterface
    }

    /**
     * Current profile of the application
     */
    val CurrentProfile : ProfileInterface get() {
        if(CurrentUser == null)
            return Defaut
        return (CurrentUser as UserInterface).CurrentProfile
    }
}