package objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.UserConstants.*
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.objects.UserDatabaseFirestore
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.time.LocalDate

val googleId = "googleId"
val usernameEntity = "JohnDoe"
val name = "John Doe"
val birthDate = LocalDate.of(1990, 12, 30)
val email = "John@email.com"

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private val listProfile = ArrayList<String>()
private const val uidTest2 = "Test uid2"
private const val emailTest2 = "Test email"
private const val displayNameTest2 = "Test uid2"
private const val username = "Test username"


class UserDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var profile: UserProfile
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface
    lateinit var userDocument: HashMap<String, Any?>

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest,
            profiles = listProfile
        )
        profile = UserProfile()

        userDocument = hashMapOf(
            USER_UID.value to uidTest,
            USER_NAME.value to displayNameTest,
            USER_EMAIL.value to emailTest,
            USER_PROFILES.value to listProfile
        )
        //Mock the database and set it as the default database
        mockedDatabase = Mockito.mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore = mockedDatabase
        //FirestoreDatabaseProvider.userDatabase =  mockedDatabaseUser
        UserDatabaseFirestore.firestore = mockedDatabase



        UserDatabaseFirestore.firstConnectionUser = UserEntity(uid = "DEFAULT")
        FirestoreDatabaseProvider.lastQuerySuccessListener = null
        FirestoreDatabaseProvider.lastSetSuccessListener = null
        FirestoreDatabaseProvider.lastFailureListener = null
        FirestoreDatabaseProvider.lastGetSuccessListener = null
        FirestoreDatabaseProvider.lastAddSuccessListener = null
    }

    @After
    fun teardown() {
        FirestoreDatabaseProvider.firestore = null
        UserDatabaseFirestore.firestore = null
        UserLogin.currentUserLogin = GoogleUserLogin
    }

    @Test
    fun variableCorrectlySet(){
        val mockedUserLogin = Mockito.mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        UserLogin.currentUserLogin = mockedUserLogin
        FirestoreDatabaseProvider.currentUser = user
        Mockito.`when`(mockedUserLogin.isConnected()).thenReturn(true)
        FirestoreDatabaseProvider.currentProfile = UserProfile()
        assert(UserDatabaseFirestore.currentUser==FirestoreDatabaseProvider.currentUser)
        assert(UserDatabaseFirestore.currentProfile==FirestoreDatabaseProvider.currentProfile)
        assert(UserDatabaseFirestore.firestore==mockedDatabase)
    }

    @Test
    fun inDatabaseCorrectlySetTheObservable() {
        //Mock all the necessary class to mock the methods
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedQuery = Mockito.mock(Query::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = Mockito.mock(QuerySnapshot::class.java)
        val mockedList = Mockito.mock(List::class.java) as List<DocumentSnapshot>

        //Mock all the needed method to perform the query correctly
        Mockito.`when`(mockedDatabase.collection(USER_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        Mockito.`when`(
            mockedCollectionReference.whereEqualTo(
                USER_UID.value,
                uidTest
            )
        ).thenReturn(mockedQuery)
        Mockito.`when`(mockedQuery.limit(1)).thenReturn(mockedQuery)
        Mockito.`when`(mockedQuery.get()).thenReturn(mockedTask)
        Mockito.`when`(mockedDocument.documents).thenReturn(mockedList)
        Mockito.`when`(mockedList.size).thenReturn(1)
        //mock sets the listerner
        Mockito.`when`(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            //Trigger the last used trigger that will do a callback according to the inDatabase method
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        Mockito.`when`(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        val isInDb = Observable<Boolean>()
        val result = FirestoreDatabaseProvider.userDatabase!!.inDatabase(
            isInDb,
            uidTest, profile
        )
        //Assert that the value are correctly set by the database
        assert(isInDb.value!!)
        //assert that the value is not in database
        assert(result.value!!)
    }

    @Test
    fun notInDatabaseCorrectlySetTheObservable() {
        //Mock the needed classes
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedQuery = Mockito.mock(Query::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = Mockito.mock(QuerySnapshot::class.java)
        val mockedList = Mockito.mock(List::class.java) as List<DocumentSnapshot>

        //mock the needed method
        Mockito.`when`(mockedDatabase.collection(USER_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        Mockito.`when`(
            mockedCollectionReference.whereEqualTo(
                USER_UID.value,
                uidTest
            )
        ).thenReturn(mockedQuery)
        Mockito.`when`(mockedQuery.limit(1)).thenReturn(mockedQuery)
        Mockito.`when`(mockedQuery.get()).thenReturn(mockedTask)
        Mockito.`when`(mockedDocument.documents).thenReturn(mockedList)
        Mockito.`when`(mockedList.size).thenReturn(0)
        Mockito.`when`(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            //Trigger the last used trigger that will do a callback according to the inDatabase method
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        Mockito.`when`(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        val isInDb = Observable<Boolean>()
        val result = FirestoreDatabaseProvider.userDatabase!!.inDatabase(
            isInDb,
            uidTest, profile
        )
        //Assert that the DB successfully performed the query
        assert(result.value!!)
        //assert that the value is not in database
        assert(!isInDb.value!!)
    }

    @Test
    fun getUserInformationReturnCorrectInformation() {
        //Mock the needed classes
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<DocumentSnapshot>
        val mockedDocument = Mockito.mock(DocumentSnapshot::class.java)

        //mock the needed method
        Mockito.`when`(mockedDatabase.collection(USER_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        Mockito.`when`(mockedCollectionReference.document(uidTest))
            .thenReturn(mockedDocumentReference)
        Mockito.`when`(mockedDocumentReference.get()).thenReturn(mockedTask)
        Mockito.`when`(mockedDocument.data).thenReturn(userDocument)
        Mockito.`when`(mockedDocument.id).thenReturn(uidTest)

        Mockito.`when`(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            //Trigger the last used trigger that will do a callback according to the getUserInformation method
            FirestoreDatabaseProvider.lastGetSuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        Mockito.`when`(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        val userObs = Observable<UserEntity>()
        val result = FirestoreDatabaseProvider.userDatabase!!.getUserInformation(
            userObs,
            uidTest, profile
        )
        //Assert that the DB correctly answer with true
        assert(result.value!!)
        //assert that the value of the observable was set by the DB
        assert(userObs.value != null)
        //Check that the value indeed corresponds to the correct user
        val userValue = userObs.value!!
        assert(userValue.email == emailTest)
        assert(userValue.name == displayNameTest)
        assert(userValue.uid == uidTest)
    }

    @Test
    fun updateUserInformationSetTheGoodInformation() {
        //mock the required class
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<Void>

        //Create a hashmap with values to update
        val map: HashMap<String, String> = HashMap()
        map[USER_UID.value] = uidTest2
        map[USER_USERNAME.value] = username
        map[USER_NAME.value] = displayNameTest2
        map[USER_EMAIL.value] = emailTest2

        var emailSet = ""
        var nameSet = ""
        var uidSet = ""
        var usernameSet = ""

        //mock the needed method
        Mockito.`when`(mockedDatabase.collection(USER_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        Mockito.`when`(mockedCollectionReference.document(uidTest))
            .thenReturn(mockedDocumentReference)
        Mockito.`when`(mockedDocumentReference.update(map as Map<String, Any>))
            .thenReturn(mockedTask)
        //TODO Mock the result from the database once the data class user is terminated

        Mockito.`when`(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            emailSet = emailTest2
            nameSet = displayNameTest2
            uidSet = uidTest2
            usernameSet = username
            mockedTask
        }

        Mockito.`when`(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        //Assert that the database correctly setted the value
        val result = FirestoreDatabaseProvider.userDatabase!!.updateUserInformation(
            map,
            uidTest, profile
        )
        assert(result.value!!)
        assert(emailSet.equals(emailTest2))
        assert(nameSet.equals(displayNameTest2))
        assert(uidSet.equals(uidTest2))
        assert(usernameSet.equals(username))
    }

    @Test
    fun firstConnectionSetTheGoodInformation() {
        //mock the required class
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<Void>

        var emailSet: String? = ""
        var nameSet: String? = ""
        var uidSet = ""

        Mockito.`when`(mockedDatabase.collection(USER_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        Mockito.`when`(mockedCollectionReference.document(uidTest))
            .thenReturn(mockedDocumentReference)
        Mockito.`when`(mockedDocumentReference.set(user)).thenReturn(
            mockedTask
        )

        Mockito.`when`(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            emailSet = user.email
            nameSet = user.name
            uidSet = user.uid
            mockedTask
        }

        Mockito.`when`(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        //Assert that the database correctly setted the value
        val result = FirestoreDatabaseProvider.userDatabase!!.firstConnexion(user, profile)
        assert(result.value!!)
        assert(emailSet.equals(user.email))
        assert(nameSet.equals(user.name))
        assert(uidSet.equals(user.uid))
    }

}