package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.user.User.Companion.currentUser

/**
 * Application user profile
 * @property id Profile id
 * @property name Profile name
 * @property rank Profile rank
 * @property associatedUser Associated user of the profile
 */
class Profile(
    override var id: String,
    override var name: String,
    override var rank: Rank,
    override val associatedUser: UserInterface? = null) :
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
                default = default ?: Profile("default", "default", Rank.Visitor)
                return default as ProfileInterface
            }

        /**
         * Current profile of the application
         */
        val CurrentProfile: ProfileInterface get() = currentUser?.currentProfile ?: Default
    }
}