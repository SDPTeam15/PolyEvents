package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.FakeDatabase
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProfileTest {
    @Before
    fun setup() {
        currentDatabase = FakeDatabase
    }

    @Test
    fun defaultTest() {
        val defaultProfile = Profile.Default
        assertEquals(defaultProfile, Profile.Default)
        if (User.currentUser == null)
            assertEquals(Profile.CurrentProfile, Profile.Default)
        else
            assertNotEquals(Profile.CurrentProfile, Profile.Default)
    }
}