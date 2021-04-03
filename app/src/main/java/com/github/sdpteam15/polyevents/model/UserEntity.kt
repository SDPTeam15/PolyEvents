package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDate

// TODO: builder pattern maybe?
// TODO: Make username unique, and use googleId to avoid duplicate keys

/**
 * Entity model for application user
 *
 * @property uid the id of a user when he gets authenticated to google services
 * (this is unique to each user)
 * @property username username of a user to use and identify him in the application
 * @property name fullname of a user
 * @property age the user's age
 * @property email the email of the user (by default google's)
 * @property profiles the list of profiles the user has
 */
@IgnoreExtraProperties
data class UserEntity (
    val uid: String,
    var username: String? = null,
    var name: String? = null,
    var birthDate: LocalDate? = null,
    var email: String? = null,
    var telephone: String? = null,
    val profiles: MutableList<UserProfile> = mutableListOf()) {

    val age: Int?
        get() =
            birthDate?.let { HelperFunctions.calculateAge(it, LocalDate.now()) }

    /**
     * Check if current user has an admin profile
     * @return if one of the profiles has admin role
     */
    fun isAdmin(): Boolean {
        for (profile in profiles) {
            if (profile.userRole == UserRole.ADMIN) return true
        }
        return false
    }

    /**
     * Create a new Profile with the given to the associated user
     * @param name the name of the profile
     * @return true if new profile with given was successfully added
     */
    fun addNewProfile(name: String): Boolean =
        profiles.add(UserProfile(
                userUid = uid,
                profileName = name
        ))

    /**
     * Add a new profile to the user's list of profiles
     * @param userProfile the new Profile to add
     * @return true if newProfile was successfully added
     */
    fun addNewProfile(userProfile: UserProfile): Boolean =
            profiles.add(
                    userProfile
            )


    /**
     * Remove a Profile with the given name
     * @param name the name of the profile to remove
     * @return true if a profile with the given name was found
     */
    fun removeProfile(name: String): Boolean {
        for (prof in profiles) {
            if (prof.profileName == name)
                // TODO: do we allow several profiles with the same name? (if so need to remove them all)
                return profiles.remove(prof)
        }
        return false
    }

    // TODO
    fun switchRoles(userRole: UserRole) {

    }
}