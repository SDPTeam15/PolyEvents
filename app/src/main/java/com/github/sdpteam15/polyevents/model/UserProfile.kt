package com.github.sdpteam15.polyevents.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * @property profileName the name of the profile
 * @property userUid the username of the user this profile is associated to.
 * @property userRole the role associated with this user profile. Can be one of admin,
 * staff, event organizer or simply participant.
 */
@IgnoreExtraProperties
data class UserProfile(
        val userUid: String? = null,
        val profileName: String? = null,
        val userRole: UserRole = UserRole.PARTICIPANT
)