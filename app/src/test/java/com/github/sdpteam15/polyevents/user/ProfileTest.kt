package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.Database
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProfileTest {
    @Before
    fun setup() {
        Database.currentDatabase = FakeDatabase
    }

    @Test
    fun defaultTest() {
        val defaultProfile = Profile.Default
        assertEquals(defaultProfile, Profile.Default)
        if(User.CurrentUser == null)
            assertEquals(Profile.CurrentProfile, Profile.Default)
        else
            assertNotEquals(Profile.CurrentProfile, Profile.Default)
    }
}