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
import java.lang.Exception
import org.mockito.Mockito.`when` as When

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private const val uidTest2 = "Test uid2"
private const val emailTest2 = "Test email"
private const val displayNameTest2 = "Test uid2"
private const val username = "Test username"

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

    @Test
    fun updateUserInformationSetTheGoodInformations() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<Void>

        val map :HashMap<String, String> = HashMap()
        map["uid"] = uidTest2
        map["username"] = username
        map["displayName"] = displayNameTest2
        map["email"] = emailTest2

        var emailSet=""
        var nameSet=""
        var uidSet=""
        var usernameSet =""

        When(mockedDatabase.collection(FirestoreDatabaseProvider.USER_DOCUMENT)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(uidTest)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.update(map as Map<String, Any>)).thenReturn(mockedTask)
        //TODO Mock the result from the database once the data class user is terminated

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            emailSet = emailTest2
            nameSet = displayNameTest2
            uidSet = uidTest2
            usernameSet= username
            mockedTask
        }

        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val result = FirestoreDatabaseProvider.updateUserInformation(map, uidTest, user)
        assert(result.value!!)
        assert(emailSet.equals(emailTest2))
        assert(nameSet.equals(displayNameTest2))
        assert(uidSet.equals(uidTest2))
        assert(usernameSet.equals(username))
    }

    @Test
    fun firstConnectionSetTheGoodInformations() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<Void>

        var emailSet=""
        var nameSet=""
        var uidSet=""

        When(mockedDatabase.collection(FirestoreDatabaseProvider.USER_DOCUMENT)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(uidTest)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.set(FirestoreDatabaseProvider.firstConnectionMap)).thenReturn(mockedTask)

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            emailSet = user.email
            nameSet = user.name
            uidSet = user.uid
            mockedTask
        }

        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val result = FirestoreDatabaseProvider.firstConnexion(user, user)
        assert(result.value!!)
        assert(emailSet.equals(user.email))
        assert(nameSet.equals(user.name))
        assert(uidSet.equals(user.uid))
    }
}