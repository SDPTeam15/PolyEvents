package com.github.sdpteam15.polyevents.entity

import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// TODO: to continue
class UserProfileTest {
    lateinit var userProfile: UserProfile

    val pid = "myId"
    val profileName = "myProfile"
    val userRole = UserRole.ADMIN

    @Before
    fun setup() {
        userProfile = UserProfile(
            pid = pid,
            profileName = profileName,
            userRole = userRole
        )
    }

    @Test
    fun testUserProfileCorrectlyConstructed() {
        assertEquals(userProfile.pid, pid)
        assertEquals(userProfile.profileName, profileName)
        assertEquals(userProfile.userRole, userRole)
    }

}