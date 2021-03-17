package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`as When
import org.mockito.Mockito.mock

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

class FirestoreDatabaseProviderTest {
    lateinit var user: UserInterface
    lateinit var mockedDatabaseUser: DatabaseUserInterface
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var endingRequest: MutableLiveData<Boolean>
    val testingID = "TESTINGUSER"
    lateinit var database: DatabaseInterface

    @Before
    fun setup() {
        mockedDatabaseUser = mock(DatabaseUserInterface::class.java)
        When(mockedDatabaseUser.email).thenReturn(emailTest)
        When(mockedDatabaseUser.displayName).thenReturn(displayNameTest)
        When(mockedDatabaseUser.uid).thenReturn(uidTest)
        user = User.invoke(mockedDatabaseUser)
        
        mockedDatabase = mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore = mockedDatabase
    }

    @Test
    fun canAddAUser(){
        When(
            mockedDatabase
                .collection(FirestoreDatabaseProvider.USER_DOCUMENT)
            .whereEqualTo(FirestoreDatabaseProvider.USER_DOCUMENT_ID, uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                any()
            }
            .addOnFailureListener {
                ended.postValue(false)
            })
    }
}