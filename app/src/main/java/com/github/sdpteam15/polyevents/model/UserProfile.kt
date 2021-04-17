package com.github.sdpteam15.polyevents.model

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.ObservableList

/**
 * @property pid id of the Profile
 * @property profileName the name of the profile
 * @property userRole the role associated with this user profile. Can be one of admin, staff, event organizer or simply participant.
 * @property users  the list of users the profile has
 */
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
    private lateinit var remove: () -> Boolean
    var userEntity: ObservableList<UserEntity> = ObservableList()
        get() {
            if (!loadSuccess)
                remove = Database.currentDatabase.userDatabase!!.getProfilesUserList(field, this)
                    .observe {
                        loadSuccess = it.value
                        remove()
                    }
            return field
        }
        set(value) {
            loadSuccess = true
            field = value
        }
}