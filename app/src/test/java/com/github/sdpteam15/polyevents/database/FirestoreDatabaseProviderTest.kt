package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.test.assertTrue

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

class FirestoreDatabaseProviderTest {
    lateinit var user: UserInterface
    lateinit var mockedDatabaseUser: DatabaseUserInterface
    lateinit var endingRequest: MutableLiveData<Boolean>
    val testingID = "TESTINGUSER"
    lateinit var database: DatabaseInterface

    @Before
    fun setup() {
        mockedDatabaseUser = mock(DatabaseUserInterface::class.java)
        Mockito.`when`(mockedDatabaseUser.email).thenReturn(emailTest)
        Mockito.`when`(mockedDatabaseUser.displayName).thenReturn(displayNameTest)
        Mockito.`when`(mockedDatabaseUser.uid).thenReturn(uidTest)
        user = User.invoke(mockedDatabaseUser)
/*
        endingRequest = MutableLiveData<Boolean>()
        FirestoreDatabaseProvider.firestore = mock(FirebaseFirestore::class.java)
        database = FirestoreDatabaseProvider*/
    }

    @Test
    fun canAddAUser(){


    }
}