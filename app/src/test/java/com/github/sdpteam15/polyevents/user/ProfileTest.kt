package com.github.sdpteam15.polyevents.user

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProfileTest {
    @Test
    fun defautTest() {
        val defautProfile = Profile.Default
        assertEquals(defautProfile, Profile.Default)
        if(User.CurrentUser == null)
            assertEquals(Profile.CurrentProfile, Profile.Default)
        else
            assertNotEquals(Profile.CurrentProfile, Profile.Default)
    }
}