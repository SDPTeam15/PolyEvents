package com.github.sdpteam15.polyevents.user

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProfileTest {
    @Test
    fun defautTest() {
        val defautProfile = Profile.Defaut
        assertEquals(defautProfile, Profile.Defaut)
        if(User.CurrentUser == null)
            assertEquals(Profile.CurrentProfile, Profile.Defaut)
        else
            assertNotEquals(Profile.CurrentProfile, Profile.Defaut)
    }
}