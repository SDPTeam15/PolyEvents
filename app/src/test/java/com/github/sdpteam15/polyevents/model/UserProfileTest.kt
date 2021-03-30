package com.github.sdpteam15.polyevents.model

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// TODO: to continue
class UserProfileTest {
    lateinit var userProfile: UserProfile

    val user_uid = "myId"
    val profileName = "myProfile"
    val userRole = UserRole.ADMIN

    @Before
    fun setup() {
        userProfile = UserProfile(
                userUid = user_uid,
                profileName = profileName,
                userRole = userRole
        )
    }

    @Test
    fun testUserProfileCorrectlyConstructed() {
        assertEquals(userProfile.userUid, user_uid)
        assertEquals(userProfile.profileName, profileName)
        assertEquals(userProfile.userRole, userRole)
    }

}