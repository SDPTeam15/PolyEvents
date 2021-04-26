package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ProfileConstants.*
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 */
object ProfileAdapter : AdapterInterface<UserProfile> {

    override fun toDocument(element: UserProfile): HashMap<String, Any?> = hashMapOf(
        PROFILE_ID.value to element.pid,
        PROFILE_NAME.value to element.profileName,
        PROFILE_RANK.value to element.userRole.userRole,
        PROFILE_USERS.value to element.users
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String) = UserProfile(
        pid = id as String?,
        profileName = document[PROFILE_NAME.value] as String?,
        userRole = if ((document[PROFILE_RANK.value] as String?) != null)
            UserRole.fromString(document[PROFILE_RANK.value] as String)!!
        else UserRole.PARTICIPANT,
        users = (document[PROFILE_USERS.value] as List<String>).toMutableList()
    )
}