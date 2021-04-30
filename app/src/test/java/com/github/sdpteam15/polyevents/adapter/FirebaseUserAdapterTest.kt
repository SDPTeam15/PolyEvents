package com.github.sdpteam15.polyevents.adapter

import com.github.sdpteam15.polyevents.model.database.remote.login.FirebaseUserAdapter
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as When

class FirebaseUserAdapterTest {
    val displayName = "John"
    val uid = "googleId"
    val email = "that email"

    lateinit var mockedFirebaseUser: FirebaseUser

    @Before
    fun setup() {
        mockedFirebaseUser = mock(FirebaseUser::class.java)
        When(mockedFirebaseUser.displayName).thenReturn(displayName)
        When(mockedFirebaseUser.uid).thenReturn(uid)
        When(mockedFirebaseUser.email).thenReturn(email)
    }

    @Test
    fun conversionFromFirebaseUserToUserEntityReturnsCorrectInfo() {
        val userEntity = FirebaseUserAdapter.toUser(mockedFirebaseUser)

        assertEquals(userEntity.name, displayName)
        assertEquals(userEntity.uid, uid)
        assertEquals(userEntity.email, email)
        assertNull(userEntity.telephone)
    }
}