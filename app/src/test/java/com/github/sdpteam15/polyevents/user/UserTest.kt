package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.DatabaseUserInterface
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.mockito.Mockito.`when` as When

@RunWith(MockitoJUnitRunner::class)
class UserTest {

    lateinit var mockedDatabaseUser: DatabaseUserInterface
    lateinit var mockedDatabaseInterface: DatabaseInterface

    @Before
    fun setup() {
        mockedDatabaseUser = mock(DatabaseUserInterface::class.java)
        mockedDatabaseInterface = mock(DatabaseInterface::class.java)
        currentDatabase = mockedDatabaseInterface
    }

    @Test
    fun invokeTest() {
        When(mockedDatabaseUser.uid).thenReturn("Test UID")

        val user = User.invoke(mockedDatabaseUser)

        assertEquals(user, User.invoke(mockedDatabaseUser))
        assertEquals(user, User.invoke(mockedDatabaseUser.uid))
    }

    @Test
    fun profileListTest() {
        When(mockedDatabaseUser.uid).thenReturn("Test UID")
        When(mockedDatabaseUser.displayName).thenReturn("Test name")

        val user = User.invoke(mockedDatabaseUser)

        When(mockedDatabaseInterface.getListProfile(user.uid, user)).thenReturn(listOf())

        assertEquals(1, user.profileList.size)
        assertEquals(mockedDatabaseUser.displayName, user.profileList[0].name)
        assertFailsWith<IndexOutOfBoundsException> { user.currentProfileId = -1 }
        assertFailsWith<IndexOutOfBoundsException> { user.currentProfileId = 1 }

        user.newProfile("New Name")
        assertEquals(2, user.profileList.size)
        assertEquals(mockedDatabaseUser.displayName, user.profileList[0].name)
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

        val fakeProfile = mock(ProfileInterface::class.java)
        user.removeProfile(fakeProfile)

        user.removeCache()
        assertEquals(user.profileList[user.currentProfileId], user.currentProfile)

        user.removeCache()
        user.newProfile("New Name")
        assertEquals(2, user.profileList.size)

        user.removeProfile(fakeProfile)
        user.removeCache()
    }

    @Test
    fun currentUserTest() {
        val mockedDatabase = mock(DatabaseInterface::class.java)
        val mockedCurrentUser1 = mock(DatabaseUserInterface::class.java)
        val mockedCurrentUser2 = mock(DatabaseUserInterface::class.java)
        currentDatabase = mockedDatabase;

        When(mockedDatabase.currentUser).thenReturn(mockedCurrentUser1)
        When(mockedCurrentUser1.uid).thenReturn("0")
        assertEquals("0", User.currentUser!!.uid)
        assertEquals("0", User.currentUser!!.uid)

        When(mockedDatabase.currentUser).thenReturn(mockedCurrentUser2)
        When(mockedCurrentUser2.uid).thenReturn("1")
        assertEquals("1", User.currentUser!!.uid)
    }
}