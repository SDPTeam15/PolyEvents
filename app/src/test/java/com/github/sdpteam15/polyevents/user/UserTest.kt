package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.FakeDatabase
import com.github.sdpteam15.polyevents.database.FakeFirebaseUser
import com.github.sdpteam15.polyevents.database.FirebaseUserInterface
import com.github.sdpteam15.polyevents.user.UserObject.CurrentUser
import com.google.firebase.auth.FirebaseAuth
import org.junit.Test
import kotlin.test.*

class UserTest {
    @Test
    fun invokeTest() {
        val fakeFirebaseUser = FakeFirebaseUser()
        val user = User.invoke(fakeFirebaseUser)
        assertEquals(user, User.invoke(fakeFirebaseUser))
        assertFailsWith<IllegalArgumentException> { User.invoke(null as FirebaseUserInterface?) }
        assertEquals(user, User.invoke(fakeFirebaseUser.uid))
        assertEquals(null, User.invoke(null as String?))
    }

    @Test
    fun profileListTest() {
        val fakeFirebaseUser = FakeFirebaseUser()
        val user = User.invoke(fakeFirebaseUser)
        user.database = FakeDatabase()

        assertEquals(1, user.ProfileList.size)
        assertEquals(fakeFirebaseUser.displayName, user.ProfileList[0].Name)
        assertFailsWith<IndexOutOfBoundsException> { user.CurrentProfileId = -1 }
        assertFailsWith<IndexOutOfBoundsException> { user.CurrentProfileId = 1 }

        user.newProfile("New Name")
        assertEquals(2, user.ProfileList.size)
        assertEquals(fakeFirebaseUser.displayName, user.ProfileList[0].Name)
        assertEquals("New Name", user.ProfileList[1].Name)
        assertEquals(user.ProfileList[0], user.CurrentProfile)

        assertEquals(user.ProfileList[user.CurrentProfileId], user.CurrentProfile)

        user.CurrentProfileId = 1;
        assertEquals(user.ProfileList[1] ,user.CurrentProfile)

        user.CurrentProfile = user.ProfileList[0];
        assertEquals(user.ProfileList[0], user.CurrentProfile)
        user.CurrentProfile = user.ProfileList[1];
        assertEquals(user.ProfileList[1], user.CurrentProfile)

        user.removeProfile(user.CurrentProfile)
        assertEquals(0, user.CurrentProfileId)

        user.removeProfile(user.CurrentProfile)
        user.newProfile("New Name")
        user.removeProfile(user.ProfileList[1])

        user.removeProfile(FakeProfile("null"))

        user.removeCache()
        assertEquals(user.ProfileList[user.CurrentProfileId], user.CurrentProfile)

        user.removeCache()
        user.newProfile("New Name")
        assertEquals(2, user.ProfileList.size)

        user.removeCache()
        user.removeProfile(FakeProfile("null"))
    }
}