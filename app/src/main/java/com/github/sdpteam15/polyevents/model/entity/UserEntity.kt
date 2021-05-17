package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.observable.ObservableList
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
data class UserEntity(
    var uid: String,
    var username: String? = null,
    var name: String? = null,
    var birthDate: LocalDate? = null,
    var email: String? = null,
    var telephone: String? = null,
    val profiles: MutableList<String> = mutableListOf()
) {

    val age: Int?
        get() =
            birthDate?.let { HelperFunctions.calculateAge(it, LocalDate.now()) }

    var loadSuccess = false
        set(value) {
            field = value
            if (!value)
                userProfiles
        }
    var isLoading = false
    var userProfiles: ObservableList<UserProfile> = ObservableList()
        get() {
            synchronized(this) {
                if (!loadSuccess && !isLoading) {
                    isLoading = true
                    Database.currentDatabase.userDatabase!!.getUserProfilesList(field, this)
                        .observeOnce {
                            synchronized(this) {
                                isLoading = false
                                if (it.value && !loadSuccess)
                                    loadSuccess = true
                            }
                        }
                    field.observe {
                        roles.clear(this)
                        val res = mutableSetOf(UserRole.PARTICIPANT)
                        for (e in it.value)
                            res.add(e.userRole)
                        roles.addAll(res, this)
                    }
                }
            }
            return field
        }
        set(value) {
            loadSuccess = true
            field = value
        }

    val roles: ObservableList<UserRole> = ObservableList()
        get() {
            synchronized(this) {
                if (!loadSuccess)
                    userProfiles
            }
            return field
        }

    /**
     * Check if current user has an admin profile
     * @return if one of the profiles has admin role
     */
    fun isAdmin(): Boolean {
        //TODO roles.contains(UserRole.ADMIN)
        return true
    }

    /**
     * Create a new Profile with the given to the associated user
     * @param profile the new Profile to add
     * @return true if new profile with given was successfully added
     */
    fun addNewProfile(profile: UserProfile): Boolean {
        if (profile.pid != null) {
            profiles.add(profile.pid!!)
            profile.users.add(this.uid)
            return true
        }
        return false
    }

    /**
     * Remove a Profile with the given name
     * @param name the name of the profile to remove
     * @return true if a profile with the given name was found
     */
    fun removeProfile(id: String?): Boolean {
        if (id != null)
            return profiles.remove(id)
        return false
    }
}