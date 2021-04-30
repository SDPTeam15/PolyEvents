package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.observable.ObservableList

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * @property pid id of the Profile
 * @property profileName the name of the profile
 * @property userRole the role associated with this user profile. Can be one of admin, staff, event organizer or simply participant.
 * @property users  the list of users the profile has
 */
@IgnoreExtraProperties
data class UserProfile(
    var pid: String? = null,
    var profileName: String? = null,
    var userRole: UserRole = UserRole.PARTICIPANT,
    val users: MutableList<String> = mutableListOf()
) {

    var loadSuccess = false
        set(value){
            field = value
            if(!value)
                userEntity
        }
    var userEntity: ObservableList<UserEntity> = ObservableList()
        get() {
            if (!loadSuccess)
                Database.currentDatabase.userDatabase!!.getProfilesUserList(field, this)
                    .observeOnce {
                        loadSuccess = it.value
                    }
            return field
        }
        set(value) {
            loadSuccess = true
            field = value
        }
}