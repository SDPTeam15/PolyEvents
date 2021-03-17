package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When

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
    fun canAddAUser() {
        val mokedCollectionReference = mock(CollectionReference::class.java)
        val mokedQuery = mock(Query::class.java)
        val mokedTask = mock(Task::class.java) as Task<QuerySnapshot>
        val mekedDocument = mock(QuerySnapshot::class.java)
        val mekedList = mock(List::class.java) as List<DocumentSnapshot>

        When(mockedDatabase.collection(FirestoreDatabaseProvider.USER_DOCUMENT)).thenReturn(mokedCollectionReference)
        When(mokedCollectionReference.whereEqualTo(FirestoreDatabaseProvider.USER_DOCUMENT_ID, uidTest)).thenReturn(mokedQuery)
        When(mokedQuery.limit(1)).thenReturn(mokedQuery)
        When(mokedQuery.get()).thenReturn(mokedTask)
        When(mekedDocument.documents).thenReturn(mekedList)
        When(mekedList.size).thenReturn(1)
        When(mokedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastGetSuccessListener!!(mekedDocument)
            mokedTask
        }
        When(mokedTask.addOnFailureListener(any())).thenAnswer {
            mokedTask
        }

        val isInDb = Observable<Boolean>()
        val result = FirestoreDatabaseProvider.inDatabase(isInDb, uidTest, user)

        assert(isInDb.value!!)
        assert(result.value!!)
    }
}