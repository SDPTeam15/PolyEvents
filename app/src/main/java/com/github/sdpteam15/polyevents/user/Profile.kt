package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.user.User.Companion.CurrentUser

/**
 * Application user profile
 * @param Name profile name
 */
class Profile(override var Name : String) : ProfileInterface{
    /**
     * Application user profile constants
     */
    companion object {
        private var default : ProfileInterface? = null

        /**
         * Defaut profile of the application
         */
        val Default : ProfileInterface
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
                return Default
            return (CurrentUser as UserInterface).CurrentProfile
        }
    }

}