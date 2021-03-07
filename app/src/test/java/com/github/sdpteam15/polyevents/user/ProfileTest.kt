package com.github.sdpteam15.polyevents.user

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProfileTest {
    @Test
    fun defautTest() {
        val defautProfile = ProfileObject.Defaut
        assertEquals(defautProfile, ProfileObject.Defaut)
        if(UserObject.CurrentUser == null)
            assertEquals(ProfileObject.CurrentProfile, ProfileObject.Defaut)
        else
            assertNotEquals(ProfileObject.CurrentProfile, ProfileObject.Defaut)
    }
}