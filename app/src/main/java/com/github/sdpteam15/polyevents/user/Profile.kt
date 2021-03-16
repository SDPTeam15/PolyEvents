package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.user.User.Companion.currentUser

/**
 * Application user profile
 * @property name Profile name
 * @property associatedUser Associated user of the profile
 */
class Profile(override var name: String, override val associatedUser: UserInterface?) :
    ProfileInterface {

    /**
     * Application user profile constants
     */
    companion object {
        private var default: ProfileInterface? = null

        /**
         * Default profile of the application
         */
        val Default: ProfileInterface
            get() {
                default = default ?: Profile("default", null)
                return default as ProfileInterface
            }

        /**
         * Current profile of the application
         */
        val CurrentProfile: ProfileInterface get() = currentUser?.currentProfile ?: Default
    }
}