package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.ObservableList

/**
 * @property pid id of the Profile
 * @property profileName the name of the profile
 * @property userRole the role associated with this user profile. Can be one of admin,
 * staff, event organizer or simply participant.
 */
data class UserProfile(
    var pid: String? = null,
    var profileName: String? = null,
    var userRole: UserRole = UserRole.PARTICIPANT,
    val users: MutableList<String> = mutableListOf()
) {
    val userEntity: ObservableList<UserEntity>
        get() {
            val res = ObservableList<UserEntity>()
            Database.currentDatabase.userDatabase!!.getProfilesUserList(res, this)
            return res
        }
}