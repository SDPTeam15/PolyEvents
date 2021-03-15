package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertTrue

private const val diplayNameTest = "Test displayName"
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
        mockedDatabaseUser = Mockito.mock(DatabaseUserInterface::class.java)
        Mockito.`when`(mockedDatabaseUser.email).thenReturn(emailTest)
        Mockito.`when`(mockedDatabaseUser.displayName).thenReturn(diplayNameTest)
        Mockito.`when`(mockedDatabaseUser.uid).thenReturn(uidTest)
        user = User.invoke(mockedDatabaseUser)

        endingRequest = MutableLiveData<Boolean>()
        database = FirestoreDatabaseProvider
    }

    @Test
    fun canAddAUser(){

    }
}