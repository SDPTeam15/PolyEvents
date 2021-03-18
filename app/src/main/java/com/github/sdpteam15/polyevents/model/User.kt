package com.github.sdpteam15.polyevents.model

import com.google.firebase.firestore.IgnoreExtraProperties

// TODO: builder pattern maybe?
// TODO: Make username unique, and use googleId to avoid duplicate keys

/**
 * Entity model for application user
 *
 * @property googleId the id of a user when he gets authenticated to google services
 * (this is unique to each user)
 * @property userType the type of a user (e.g. admin, staff..). Predefined types are
 * defined in the companion object
 * @property username username of a user to use and identify him in the application
 * @property name fullname of a user
 * @property age the user's age
 * @property displayName the name that will be displayed on a user's profile and other activites
 * @property email the email of the user (by default google's)
 */
@IgnoreExtraProperties
data class UserEntity (
    val googleId: String? = null,
    val userType: String? = null,
    val username: String? = null,
    val name: String? = null,
    val age: Int? = null,
    val displayName: String? = null,
    val email: String? = null) {

    fun isAdmin(): Boolean =
        this.userType == ADMIN

    companion object {
        const val ADMIN = "admin"
        const val ORGANIZER = "organizer"
        const val STAFF = "staff"
        const val PARTICIPANT = "participant"
    }
}