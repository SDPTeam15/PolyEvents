package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.model.database.remote.login.GoogleUserLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as When

const val email = "TESTMAIL"
const val displayName = "TESTNAME"
const val uid = "UID"

class GoogleUserLoginTest {
    lateinit var mockFirebaseAuth: FirebaseAuth
    lateinit var mockFirebaseUser: FirebaseUser

    @Before
    fun setup() {
        mockFirebaseUser = mock(FirebaseUser::class.java)
        mockFirebaseAuth = mock(FirebaseAuth::class.java)

        When(mockFirebaseUser.email).thenReturn(email)
        When(mockFirebaseUser.displayName).thenReturn(displayName)
        When(mockFirebaseUser.uid).thenReturn(uid)

        GoogleUserLogin.firebaseAuth = mockFirebaseAuth
    }

    @After
    fun teardown() {
        GoogleUserLogin.firebaseAuth = null
    }

    @Test
    fun isConnectedWorks() {
        When(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        assert(GoogleUserLogin.isConnected())
        When(mockFirebaseAuth.currentUser).thenReturn(null)
        assert(!GoogleUserLogin.isConnected())
    }

    @Test
    fun returnCorrectUser() {
        When(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        val user = GoogleUserLogin.getCurrentUser()
        assert(user!!.name == displayName)
        assert(user.email == email)
        assert(user.uid == uid)
        When(mockFirebaseAuth.currentUser).thenReturn(null)
        val user2 = GoogleUserLogin.getCurrentUser()
        assert(user2 == null)
    }

    @Test
    fun SignOutIsProperlyCalled() {
        var called = false
        When(mockFirebaseAuth.signOut()).thenAnswer {
            called = true
            Unit
        }
        GoogleUserLogin.signOut()

        assert(called)
    }


}