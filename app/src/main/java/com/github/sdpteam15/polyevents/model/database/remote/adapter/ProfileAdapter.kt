package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ProfileConstants.*
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 */
@Suppress("UNCHECKED_CAST")
object ProfileAdapter : AdapterInterface<UserProfile> {

    override fun toDocumentWithoutNull(element: UserProfile): HashMap<String, Any?> =
        hashMapOf(
            PROFILE_NAME.value to element.profileName,
            PROFILE_RANK.value to element.userRole.userRole,
            PROFILE_DEFAULT.value to element.defaultProfile,
            PROFILE_USERS.value to element.users
        )

    override fun fromDocument(document: Map<String, Any?>, id: String) = UserProfile(
        pid = id,
        profileName = document[PROFILE_NAME.value] as String?,
        userRole = if ((document[PROFILE_RANK.value] as String?) != null)
            UserRole.fromString(document[PROFILE_RANK.value] as String) ?: UserRole.PARTICIPANT
        else UserRole.PARTICIPANT,
        defaultProfile = (document[PROFILE_DEFAULT.value] as Boolean?)?:false,
        users = (document[PROFILE_USERS.value] as List<String>).toMutableList()
    )
}