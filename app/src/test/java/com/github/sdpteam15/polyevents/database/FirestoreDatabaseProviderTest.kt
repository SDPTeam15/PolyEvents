package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.User.Companion.currentUser
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
    fun toRemoveTest() {
        val mokedUserInterface = mock(UserInterface::class.java)
        val mokedProfileInterface = mock(ProfileInterface::class.java)
        val mokedEvent = mock(Event::class.java)

        assertNotNull(FirestoreDatabaseProvider.getListProfile("", mokedUserInterface))
        assertNotNull(FirestoreDatabaseProvider.addProfile(mokedProfileInterface, "", mokedUserInterface))
        assertNotNull(FirestoreDatabaseProvider.removeProfile(mokedProfileInterface, "", mokedUserInterface))
        assertNotNull(FirestoreDatabaseProvider.updateProfile(mokedProfileInterface, mokedUserInterface))
        assert(FirestoreDatabaseProvider.getListEvent("", 1, mokedProfileInterface).size <= 1)
        assert(FirestoreDatabaseProvider.getListEvent("", 100, mokedProfileInterface).size <= 100)
        assert(FirestoreDatabaseProvider.getUpcomingEvents(1, mokedProfileInterface).size <= 1)
        assert(FirestoreDatabaseProvider.getUpcomingEvents(100, mokedProfileInterface).size <= 100)
        assert(FirestoreDatabaseProvider.updateEvent(mokedEvent, mokedProfileInterface))
    }

    @Test
    fun inDatabaseCorrectlySetTheObservable() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedQuery = mock(Query::class.java)
        val mockedTask = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = mock(QuerySnapshot::class.java)
        val mockedList = mock(List::class.java) as List<DocumentSnapshot>

        When(mockedDatabase.collection(FirestoreDatabaseProvider.USER_DOCUMENT)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.whereEqualTo(FirestoreDatabaseProvider.USER_DOCUMENT_ID, uidTest)).thenReturn(mockedQuery)
        When(mockedQuery.limit(1)).thenReturn(mockedQuery)
        When(mockedQuery.get()).thenReturn(mockedTask)
        When(mockedDocument.documents).thenReturn(mockedList)
        When(mockedList.size).thenReturn(1)
        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastGetSuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }


        val isInDb = Observable<Boolean>()
        val result = FirestoreDatabaseProvider.inDatabase(isInDb, uidTest, user)

        assert(isInDb.value!!)
        assert(result.value!!)
    }

    @Test
    fun notInDatabaseCorrectlySetTheObservable() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedQuery = mock(Query::class.java)
        val mockedTask = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = mock(QuerySnapshot::class.java)
        val mockedList = mock(List::class.java) as List<DocumentSnapshot>

        When(mockedDatabase.collection(FirestoreDatabaseProvider.USER_DOCUMENT)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.whereEqualTo(FirestoreDatabaseProvider.USER_DOCUMENT_ID, uidTest)).thenReturn(mockedQuery)
        When(mockedQuery.limit(1)).thenReturn(mockedQuery)
        When(mockedQuery.get()).thenReturn(mockedTask)
        When(mockedDocument.documents).thenReturn(mockedList)
        When(mockedList.size).thenReturn(0)
        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastGetSuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val isInDb = Observable<Boolean>()
        val result = FirestoreDatabaseProvider.inDatabase(isInDb, uidTest, user)
        assert(result.value!!)
        assert(!isInDb.value!!)
    }

    @Test
    fun getUserInformationsReturnCorrectInformation() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<DocumentSnapshot>
        val mockedDocument = mock(DocumentSnapshot::class.java)

        When(mockedDatabase.collection(FirestoreDatabaseProvider.USER_DOCUMENT)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(uidTest)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.get()).thenReturn(mockedTask)
        //TODO Mock the result from the database once the data class user is terminated

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastMultGetSuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val userObs = Observable<UserInterface>()
        val result = FirestoreDatabaseProvider.getUserInformation(userObs, uidTest, user)
        assert(result.value!!)
        assert(userObs.value != null)
        val userValue = userObs.value!!
        assert(userValue.email== emailTest)
        assert(userValue.name== displayNameTest)
        assert(userValue.uid== uidTest)
    }
}
