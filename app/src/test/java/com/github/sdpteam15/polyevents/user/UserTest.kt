package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.Database
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(MockitoJUnitRunner::class)
class UserTest {

    @Before
    fun setup() {
        Database.currentDatabase = FakeDatabase
    }

    @Test
    fun invokeTest() {
        val mockedFirebaseUser = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(mockedFirebaseUser.uid).thenReturn("Test UID")

        val user = User.invoke(mockedFirebaseUser)

        assertEquals(user, User.invoke(mockedFirebaseUser))
        assertEquals(user, User.invoke(mockedFirebaseUser.uid))
    }

    @Test
    fun profileListTest() {
        val mockedFirebaseUser = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(mockedFirebaseUser.uid).thenReturn("Test UID")
        Mockito.`when`(mockedFirebaseUser.displayName).thenReturn("Test name")

        val user = User.invoke(mockedFirebaseUser)

        assertEquals(1, user.profileList.size)
        assertEquals(mockedFirebaseUser.displayName, user.profileList[0].name)
        assertFailsWith<IndexOutOfBoundsException> { user.currentProfileId = -1 }
        assertFailsWith<IndexOutOfBoundsException> { user.currentProfileId = 1 }

        user.newProfile("New Name")
        assertEquals(2, user.profileList.size)
        assertEquals(mockedFirebaseUser.displayName, user.profileList[0].name)
        assertEquals("New Name", user.profileList[1].name)
        assertEquals(user.profileList[0], user.currentProfile)

        assertEquals(user.profileList[user.currentProfileId], user.currentProfile)

        user.currentProfileId = 1
        assertEquals(user.profileList[1], user.currentProfile)

        user.currentProfile = user.profileList[0]
        assertEquals(user.profileList[0], user.currentProfile)
        user.currentProfile = user.profileList[1]
        assertEquals(user.profileList[1], user.currentProfile)

        user.removeProfile(user.currentProfile)
        assertEquals(0, user.currentProfileId)

        user.removeProfile(user.currentProfile)
        user.newProfile("New Name")
        user.removeProfile(user.profileList[1])

        val fakeProfile = Mockito.mock(ProfileInterface::class.java)
        user.removeProfile(fakeProfile)

        user.removeCache()
        assertEquals(user.profileList[user.currentProfileId], user.currentProfile)

        user.removeCache()
        user.newProfile("New Name")
        assertEquals(2, user.profileList.size)

        user.removeProfile(fakeProfile)
        user.removeCache()
    }
}