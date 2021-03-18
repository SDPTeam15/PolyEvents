package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.DatabaseUserInterface
import com.github.sdpteam15.polyevents.database.FakeDatabase
import com.github.sdpteam15.polyevents.database.FirebaseUserAdapter
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(MockitoJUnitRunner::class)
class UserTest {

    lateinit var mockedDatabaseUser : DatabaseUserInterface
    lateinit var mockedDatabaseInterface : DatabaseInterface

    @Before
    fun setup() {
        mockedDatabaseUser = mock(DatabaseUserInterface::class.java)
        mockedDatabaseInterface = mock(DatabaseInterface::class.java)
        currentDatabase = mockedDatabaseInterface
    }

    @Test
    fun invokeTest() {
        Mockito.`when`(mockedDatabaseUser.uid).thenReturn("Test UID")

        val user = User.invoke(mockedDatabaseUser)

        assertEquals(user, User.invoke(mockedDatabaseUser))
        assertEquals(user, User.invoke(mockedDatabaseUser.uid))
    }

    @Test
    fun profileListTest() {
        Mockito.`when`(mockedDatabaseUser.uid).thenReturn("Test UID")
        Mockito.`when`(mockedDatabaseUser.displayName).thenReturn("Test name")

        val user = User.invoke(mockedDatabaseUser)

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

    @Test
    fun currentUserTest(){
        val mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        val mockedCurrentUser1 = Mockito.mock(DatabaseUserInterface ::class.java)
        val mockedCurrentUser2 = Mockito.mock(DatabaseUserInterface ::class.java)
        currentDatabase = mockedDatabase;

        Mockito.`when`(mockedDatabase.currentUser).thenReturn(mockedCurrentUser1)
        Mockito.`when`(mockedCurrentUser1.uid).thenReturn("0")
        assertEquals("0", User.currentUser!!.uid)
        assertEquals("0", User.currentUser!!.uid)

        Mockito.`when`(mockedDatabase.currentUser).thenReturn(mockedCurrentUser2)
        Mockito.`when`(mockedCurrentUser2.uid).thenReturn("1")
        assertEquals("1", User.currentUser!!.uid)
    }
}