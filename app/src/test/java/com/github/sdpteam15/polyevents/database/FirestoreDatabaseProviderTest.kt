package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.DatabaseConstant.LOCATIONS_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.LOCATIONS_POINT
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_EMAIL
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_UID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_DESCRIPTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_DOCUMENT_ID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_LOCATION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_NAME
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.*
import com.github.sdpteam15.polyevents.util.ZoneAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import java.time.LocalDate
import kotlin.test.assertNotNull
import org.hamcrest.CoreMatchers.`is` as Is
import org.mockito.Mockito.`when` as When

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private const val uidTest2 = "Test uid2"
private const val emailTest2 = "Test email"
private const val displayNameTest2 = "Test uid2"
private const val username = "Test username"
private const val zoneID = "ZONEID"
private const val zoneName = "ZONENAME"
private const val zoneDesc = "ZONEDESC"
private const val zoneLoc = "ZONELOCATION"

val googleId = "googleId"
val usernameEntity = "JohnDoe"
val name = "John Doe"
val birthDate = LocalDate.of(1990, 12, 30)
val email = "John@email.com"

class FirestoreDatabaseProviderTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface

    lateinit var userDocument: HashMap<String, Any?>
    lateinit var zoneDocument: HashMap<String, Any?>

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest
        )

        userDocument = hashMapOf(
            USER_UID to uidTest,
            USER_NAME to displayNameTest,
            USER_EMAIL to emailTest
        )
        zoneDocument = hashMapOf(
            ZONE_DOCUMENT_ID to zoneID,
            ZONE_DESCRIPTION to zoneDesc,
            ZONE_LOCATION to zoneLoc,
            ZONE_NAME to zoneName
        )


        //Mock the database and set it as the default database
        mockedDatabase = mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore = mockedDatabase

        FirestoreDatabaseProvider.firstConnectionUser=UserEntity(uid = "DEFAULT")
        FirestoreDatabaseProvider.lastQuerySuccessListener= null
        FirestoreDatabaseProvider.lastSetSuccessListener= null
        FirestoreDatabaseProvider.lastFailureListener= null
        FirestoreDatabaseProvider.lastGetSuccessListener= null
        FirestoreDatabaseProvider.lastAddSuccessListener= null
    }

    @Test
    fun toRemoveTest() {
        val testProfile = UserProfile(
            userUid = user.uid,
            profileName = "mockProfile"
        )

        val testEvent = Event(
            eventId = "eventA",
            eventName = "Event A"
        )

        assertNotNull(FirestoreDatabaseProvider.getProfilesList("", user))
        assertNotNull(
            FirestoreDatabaseProvider.addProfile(
                testProfile,
                "",
                user
            )
        )
        assertNotNull(
            FirestoreDatabaseProvider.removeProfile(
                testProfile,
                "",
                user
            )
        )
        assertNotNull(
            FirestoreDatabaseProvider.updateProfile(
                testProfile,
                user
            )
        )
        val item = Item(itemId = "TestId",itemType = ItemType.MICROPHONE)

        assert(FirestoreDatabaseProvider.getListEvent("", 1, testProfile).size <= 1)
        assert(FirestoreDatabaseProvider.getListEvent("", 100, testProfile).size <= 100)
        assert(FirestoreDatabaseProvider.getUpcomingEvents(1, testProfile).size <= 1)
        assert(FirestoreDatabaseProvider.getUpcomingEvents(100, testProfile).size <= 100)
        assert(FirestoreDatabaseProvider.updateEvent(testEvent, testProfile))
        assert(FirestoreDatabaseProvider.addItem(item))
        assert(FirestoreDatabaseProvider.removeItem(item))
        assert(FirestoreDatabaseProvider.getAvailableItems()!=null)
        assert(FirestoreDatabaseProvider.getItemsList()!=null)

        assert(FirestoreDatabaseProvider.getEventFromId("",UserProfile(userUid="DEFAULT"))==null)
    }

    @Test
    fun inDatabaseCorrectlySetTheObservable() {
        //Mock all the necessary class to mock the methods
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedQuery = mock(Query::class.java)
        val mockedTask = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = mock(QuerySnapshot::class.java)
        val mockedList = mock(List::class.java) as List<DocumentSnapshot>

        //Mock all the needed method to perform the query correctly
        When(mockedDatabase.collection(USER_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        When(
            mockedCollectionReference.whereEqualTo(
                USER_UID,
                uidTest
            )
        ).thenReturn(mockedQuery)
        When(mockedQuery.limit(1)).thenReturn(mockedQuery)
        When(mockedQuery.get()).thenReturn(mockedTask)
        When(mockedDocument.documents).thenReturn(mockedList)
        When(mockedList.size).thenReturn(1)
        //mock sets the listerner
        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            //Trigger the last used trigger that will do a callback according to the inDatabase method
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val isInDb = Observable<Boolean>()
        val result = FirestoreDatabaseProvider.inDatabase(isInDb, uidTest, user)
        //Assert that the value are correctly set by the database
        assert(isInDb.value!!)
        //assert that the value is not in database
        assert(result.value!!)
    }

    @Test
    fun notInDatabaseCorrectlySetTheObservable() {
        //Mock the needed classes
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedQuery = mock(Query::class.java)
        val mockedTask = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = mock(QuerySnapshot::class.java)
        val mockedList = mock(List::class.java) as List<DocumentSnapshot>

        //mock the needed method
        When(mockedDatabase.collection(USER_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        When(
            mockedCollectionReference.whereEqualTo(
                USER_UID,
                uidTest
            )
        ).thenReturn(mockedQuery)
        When(mockedQuery.limit(1)).thenReturn(mockedQuery)
        When(mockedQuery.get()).thenReturn(mockedTask)
        When(mockedDocument.documents).thenReturn(mockedList)
        When(mockedList.size).thenReturn(0)
        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            //Trigger the last used trigger that will do a callback according to the inDatabase method
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val isInDb = Observable<Boolean>()
        val result = FirestoreDatabaseProvider.inDatabase(isInDb, uidTest, user)
        //Assert that the DB successfully performed the query
        assert(result.value!!)
        //assert that the value is not in database
        assert(!isInDb.value!!)
    }

    @Test
    fun getUserInformationReturnCorrectInformation() {
        //Mock the needed classes
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<DocumentSnapshot>
        val mockedDocument = mock(DocumentSnapshot::class.java)

        //mock the needed method
        When(mockedDatabase.collection(USER_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.document(uidTest)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.get()).thenReturn(mockedTask)
        When(mockedDocument.data).thenReturn(userDocument)

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            //Trigger the last used trigger that will do a callback according to the getUserInformation method
            FirestoreDatabaseProvider.lastGetSuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val userObs = Observable<UserEntity>()
        val result = FirestoreDatabaseProvider.getUserInformation(userObs, uidTest, user)
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
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<Void>

        //Create a hashmap with values to update
        val map: HashMap<String, String> = HashMap()
        map[USER_UID] = uidTest2
        map[USER_USERNAME] = username
        map[USER_NAME] = displayNameTest2
        map[USER_EMAIL] = emailTest2

        var emailSet = ""
        var nameSet = ""
        var uidSet = ""
        var usernameSet = ""

        //mock the needed method
        When(mockedDatabase.collection(USER_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.document(uidTest)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.update(map as Map<String, Any>)).thenReturn(mockedTask)
        //TODO Mock the result from the database once the data class user is terminated

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            emailSet = emailTest2
            nameSet = displayNameTest2
            uidSet = uidTest2
            usernameSet = username
            mockedTask
        }

        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        //Assert that the database correctly setted the value
        val result = FirestoreDatabaseProvider.updateUserInformation(map, uidTest, user)
        assert(result.value!!)
        assert(emailSet.equals(emailTest2))
        assert(nameSet.equals(displayNameTest2))
        assert(uidSet.equals(uidTest2))
        assert(usernameSet.equals(username))
    }

    @Test
    fun firstConnectionSetTheGoodInformation() {
        //mock the required class
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<Void>

        var emailSet: String? = ""
        var nameSet: String? = ""
        var uidSet = ""

        When(mockedDatabase.collection(USER_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.document(uidTest)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.set(user)).thenReturn(
            mockedTask
        )

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            emailSet = user.email
            nameSet = user.name
            uidSet = user.uid
            mockedTask
        }

        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        //Assert that the database correctly setted the value
        val result = FirestoreDatabaseProvider.firstConnexion(user, user)
        assert(result.value!!)
        assert(emailSet.equals(user.email))
        assert(nameSet.equals(user.name))
        assert(uidSet.equals(user.uid))
    }

    @Test
    fun setUserLocationCorrectlySet() {
        //mock the required class
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<Void>

        val lat = 46.548823
        val lng = 7.017012
        val pointToAdd = LatLng(lat, lng)

        var latSet = 0.0
        var lngSet = 0.0

        When(mockedDatabase.collection(LOCATIONS_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.document(uidTest)).thenReturn(mockedDocumentReference)
        When(
            mockedDocumentReference.set(
                hashMapOf(
                    LOCATIONS_POINT to GeoPoint(
                        pointToAdd.latitude,
                        pointToAdd.longitude
                    )
                ),
                SetOptions.merge()
            )
        ).thenReturn(mockedTask)

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)

            latSet = lat
            lngSet = lng
            mockedTask
        }

        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val result = FirestoreDatabaseProvider.setUserLocation(pointToAdd, user)
        assertThat(result.value, Is(true))
        assertThat(latSet, Is(lat))
        assertThat(lngSet, Is(lng))
    }

    @Test
    fun variablesAreCorrectlySet(){
        val user = UserEntity(
            uid = googleId,
            username = usernameEntity,
            birthDate = birthDate,
            name = name,
            email = email)
        FirestoreDatabaseProvider.firstConnectionUser = user
        assertThat(FirestoreDatabaseProvider.firstConnectionUser,Is(user))

        val lastQuerySuccessListener=OnSuccessListener<QuerySnapshot> { }
        val lastSetSuccessListener=OnSuccessListener<Void> { }
        val lastFailureListener= OnFailureListener {  }
        val lastGetSuccessListener=OnSuccessListener<DocumentSnapshot> { }
        val lastAddSuccessListener=OnSuccessListener<DocumentReference> { }

        FirestoreDatabaseProvider.lastQuerySuccessListener= lastQuerySuccessListener
        FirestoreDatabaseProvider.lastSetSuccessListener= lastSetSuccessListener
        FirestoreDatabaseProvider.lastFailureListener= lastFailureListener
        FirestoreDatabaseProvider.lastGetSuccessListener= lastGetSuccessListener
        FirestoreDatabaseProvider.lastAddSuccessListener= lastAddSuccessListener

        assertThat(FirestoreDatabaseProvider.lastQuerySuccessListener,Is(lastQuerySuccessListener))
        assertThat(FirestoreDatabaseProvider.lastSetSuccessListener,Is(lastSetSuccessListener))
        assertThat(FirestoreDatabaseProvider.lastFailureListener,Is(lastFailureListener))
        assertThat(FirestoreDatabaseProvider.lastGetSuccessListener,Is(lastGetSuccessListener))
        assertThat(FirestoreDatabaseProvider.lastAddSuccessListener,Is(lastAddSuccessListener))

    }

    @Test
    fun getUsersLocationsReturnCorrectNumberOfLocations() {
        //Mock the needed classes
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = mock(QuerySnapshot::class.java)

        val mockedDoc1 = mock(DocumentSnapshot::class.java)
        val mockedDoc2 = mock(DocumentSnapshot::class.java)
        val listMockedDocs = listOf(mockedDoc1, mockedDoc2)

        val lat1 = 46.548823
        val lng1 = 7.017012
        val lat2 = 46.548343
        val lng2 = 7.017892

        val locations = listOf(
            GeoPoint(lat1, lng1),
            GeoPoint(lat2, lng2)
        )
        val locationsLatLng = listOf(
            LatLng(lat1, lng1),
            LatLng(lat2, lng2)
        )

        val mapDoc1 = hashMapOf(
            LOCATIONS_POINT to locations[0],
            USER_UID to "1"
        )
        val mapDoc2 = hashMapOf(
            LOCATIONS_POINT to locations[1],
            USER_UID to "2"
        )
        When(mockedDoc1.data).thenReturn(mapDoc1 as Map<String, Any>?)
        When(mockedDoc2.data).thenReturn(mapDoc2 as Map<String, Any>?)

        //mock the needed method
        When(mockedDatabase.collection(LOCATIONS_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.get()).thenReturn(mockedTask)

        When(mockedDocument.documents).thenReturn(listMockedDocs)

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val locationsObs = Observable<List<LatLng>>()
        val result = FirestoreDatabaseProvider.getUsersLocations(locationsObs, user)

        // Assert that the DB successfully performed the query
        assertThat(result.value, Is(true))

        assertThat(locationsObs.value, Is(locationsLatLng))
    }

    @Test
    fun createZoneWorksProperly() {
        //mock the required class
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<DocumentReference>

        val testZone = Zone(zoneID, zoneName, zoneLoc, zoneDesc)

        When(mockedDatabase.collection(ZONE_COLLECTION)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.add(ZoneAdapter.toZoneDocument(testZone))).thenReturn(
            taskReferenceMock
        )

        var zoneNameAdded = ""
        var zoneDescAdded = ""
        var zoneLocAdded = ""
        var zoneIDAdded = ""

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastAddSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            zoneNameAdded = testZone.zoneName!!
            zoneDescAdded = testZone.description!!
            zoneLocAdded = testZone.location!!
            zoneIDAdded = testZone.zoneId!!
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.createZone(testZone, user)
        assert(result.value!!)
        assert(zoneNameAdded == zoneName)
        assert(zoneDescAdded == zoneDesc)
        assert(zoneLocAdded == zoneLoc)
        assert(zoneIDAdded == zoneID)

    }

    @Test
    fun updateZoneWorksProperly() {
        //mock the required class
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<Void>
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val testZone = Zone(zoneID, zoneName, zoneLoc, zoneDesc)

        When(mockedDatabase.collection(ZONE_COLLECTION)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(zoneID)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.update(ZoneAdapter.toZoneDocument(testZone))).thenReturn(
            mockedTask
        )

        var zoneNameUpdated = ""
        var zoneDescUpdated = ""
        var zoneLocUpdated = ""
        var zoneIDUpdated = ""

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            zoneNameUpdated = testZone.zoneName!!
            zoneDescUpdated = testZone.description!!
            zoneLocUpdated = testZone.location!!
            zoneIDUpdated = testZone.zoneId!!
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }

        val result = FirestoreDatabaseProvider.updateZoneInformation(zoneID, testZone, user)
        assert(result.value!!)
        assert(zoneNameUpdated == zoneName)
        assert(zoneDescUpdated == zoneDesc)
        assert(zoneLocUpdated == zoneLoc)
        assert(zoneIDUpdated == zoneID)
    }

    @Test
    fun getZoneInformation() {
        //mock the required class
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedTask = mock(Task::class.java) as Task<DocumentSnapshot>
        val mockedDocumentReference = mock(DocumentReference::class.java)
        val mockedDocument = mock(DocumentSnapshot::class.java)

        When(mockedDatabase.collection(ZONE_COLLECTION)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(zoneID)).thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.get()).thenReturn(mockedTask)
        When(mockedDocument.data).thenReturn(zoneDocument)
        When(mockedDocument.id).thenReturn(zoneID)

        When(mockedTask.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastGetSuccessListener!!.onSuccess(mockedDocument)
            //set method in hard to see if the success listener is successfully called
            mockedTask
        }
        When(mockedTask.addOnFailureListener(any())).thenAnswer {
            mockedTask
        }
        val obsZone = Observable<Zone>()

        val result = FirestoreDatabaseProvider.getZoneInformation(zoneID, obsZone, user)
        val value = obsZone.value!!
        assert(result.value!!)
        assert(value.zoneName == zoneName)
        assert(value.description == zoneDesc)
        assert(value.location == zoneLoc)
        assert(value.zoneId == zoneID)
    }

}