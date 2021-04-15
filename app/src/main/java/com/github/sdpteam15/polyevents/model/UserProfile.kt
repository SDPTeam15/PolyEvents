package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.ObservableList

/**
 * @property id id of the Profile
 * @property profileName the name of the profile
 * @property userRole the role associated with this user profile. Can be one of admin,
 * staff, event organizer or simply participant.
 */
data class UserProfile(
    val id: String? = null,
    val profileName: String? = null,
    val userRole: UserRole = UserRole.PARTICIPANT,
    val users: MutableList<String> = mutableListOf()
) {
    val userEntity: ObservableList<UserEntity>
        get() {
            val res = ObservableList<UserEntity>()
            Database.currentDatabase.getProfilesUserList(res, this)
            return res
        }
}