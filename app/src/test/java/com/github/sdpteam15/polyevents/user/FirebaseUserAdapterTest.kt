package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.FirebaseUserAdapter
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

@RunWith(MockitoJUnitRunner::class)
class FirebaseUserAdapterTest {
    lateinit var mockedFirebaseUser: FirebaseUser
    lateinit var firebaseUser: FirebaseUserAdapter


    @Before
    fun setup() {
        mockedFirebaseUser = Mockito.mock(FirebaseUser::class.java)
        firebaseUser = FirebaseUserAdapter(mockedFirebaseUser)
        Mockito.`when`(mockedFirebaseUser.email).thenReturn(emailTest)
        Mockito.`when`(mockedFirebaseUser.displayName).thenReturn(displayNameTest)
        Mockito.`when`(mockedFirebaseUser.uid).thenReturn(uidTest)
    }

    @Test
    fun firebaseAdapterReturnCorrectValues() {
        assertEquals(emailTest, firebaseUser.email)
        assertEquals(uidTest, firebaseUser.uid)
        assertEquals(displayNameTest, firebaseUser.displayName)
    }

}