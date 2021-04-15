package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 */
object ProfileAdapter : AdapterInterface<UserProfile> {

    override fun toDocument(element: UserProfile): HashMap<String, Any?> = hashMapOf(
        DatabaseConstant.PROFILE_ID to element.pid,
        DatabaseConstant.PROFILE_NAME to element.profileName,
        DatabaseConstant.PROFILE_RANK to element.userRole,
        DatabaseConstant.PROFILE_USERS to element.users
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String) = UserProfile(
        pid = id as String?,
        profileName = document[DatabaseConstant.PROFILE_NAME] as String,
        userRole = document[DatabaseConstant.PROFILE_RANK] as UserRole,
        users = document[DatabaseConstant.PROFILE_USERS] as MutableList<String>
    )
}