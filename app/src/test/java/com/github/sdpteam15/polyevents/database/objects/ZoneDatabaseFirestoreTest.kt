package objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.*
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.objects.ZoneDatabaseFirestore
import com.github.sdpteam15.polyevents.database.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.Zone
import com.github.sdpteam15.polyevents.util.ZoneAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when` as When

private const val zoneID = "ZONEID"
private const val zoneName = "ZONENAME"
private const val zoneDesc = "ZONEDESC"
private const val zoneLoc = "ZONELOCATION"
private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

class ZoneDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface
    lateinit var zoneDocument: HashMap<String, Any?>
    lateinit var mockedZoneDatabase: ZoneDatabaseInterface

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest
        )

        zoneDocument = hashMapOf(
            ZONE_DOCUMENT_ID.value to zoneID,
            ZONE_DESCRIPTION.value to zoneDesc,
            ZONE_LOCATION.value to zoneLoc,
            ZONE_NAME.value to zoneName
        )


        //Mock the database and set it as the default database
        mockedDatabase = Mockito.mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore = mockedDatabase
        ZoneDatabaseFirestore.firestore = mockedDatabase
        mockedZoneDatabase = Mockito.mock(ZoneDatabaseInterface::class.java)


        FirestoreDatabaseProvider.lastQuerySuccessListener = null
        FirestoreDatabaseProvider.lastSetSuccessListener = null
        FirestoreDatabaseProvider.lastFailureListener = null
        FirestoreDatabaseProvider.lastGetSuccessListener = null
        FirestoreDatabaseProvider.lastAddSuccessListener = null
    }

    @After
    fun teardown() {
        FirestoreDatabaseProvider.firestore = null
        ZoneDatabaseFirestore.firestore = null
    }

    @Test
    fun createZoneWorksProperly() {
        //mock the required class
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val taskReferenceMock = Mockito.mock(Task::class.java) as Task<DocumentReference>

        val testZone = Zone(zoneID, zoneName, zoneLoc, zoneDesc)

        When(mockedDatabase.collection(ZONE_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.add(ZoneAdapter.toDocument(testZone))).thenReturn(
            taskReferenceMock
        )

        var zoneNameAdded = ""
        var zoneDescAdded = ""
        var zoneLocAdded = ""
        var zoneIDAdded = ""

        When(taskReferenceMock.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            FirestoreDatabaseProvider.lastAddSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            zoneNameAdded = testZone.zoneName!!
            zoneDescAdded = testZone.description!!
            zoneLocAdded = testZone.location!!
            zoneIDAdded = testZone.zoneId!!
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.zoneDatabase!!.createZone(testZone, user)
        assert(result.value!!)
        assert(zoneNameAdded == zoneName)
        assert(zoneDescAdded == zoneDesc)
        assert(zoneLocAdded == zoneLoc)
        assert(zoneIDAdded == zoneID)

    }

    @Test
    fun updateZoneWorksProperly() {
        //mock the required class
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<Void>
        val mockedDocumentReference = Mockito.mock(DocumentReference::class.java)
        val testZone = Zone(zoneID, zoneName, zoneLoc, zoneDesc)

        When(mockedDatabase.collection(ZONE_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.document(zoneID))
            .thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.update(ZoneAdapter.toDocument(testZone))).thenReturn(
            mockedTask
        )

        var zoneNameUpdated = ""
        var zoneDescUpdated = ""
        var zoneLocUpdated = ""
        var zoneIDUpdated = ""

        When(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            zoneNameUpdated = testZone.zoneName!!
            zoneDescUpdated = testZone.description!!
            zoneLocUpdated = testZone.location!!
            zoneIDUpdated = testZone.zoneId!!
            mockedTask
        }
        When(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        val result =
            FirestoreDatabaseProvider.zoneDatabase!!.updateZoneInformation(zoneID, testZone, user)
        assert(result.value!!)
        assert(zoneNameUpdated == zoneName)
        assert(zoneDescUpdated == zoneDesc)
        assert(zoneLocUpdated == zoneLoc)
        assert(zoneIDUpdated == zoneID)
    }

    @Test
    fun getZoneInformation() {
        //mock the required class
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<DocumentSnapshot>
        val mockedDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockedDocument = Mockito.mock(DocumentSnapshot::class.java)

        When(mockedDatabase.collection(ZONE_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.document(zoneID))
            .thenReturn(mockedDocumentReference)
        When(mockedDocumentReference.get()).thenReturn(mockedTask)
        When(mockedDocument.data).thenReturn(zoneDocument)
        When(mockedDocument.id)
            .thenReturn(zoneID)

        When(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            FirestoreDatabaseProvider.lastGetSuccessListener!!.onSuccess(mockedDocument)
            //set method in hard to see if the success listener is successfully called
            mockedTask
        }
        When(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }
        val obsZone = Observable<Zone>()

        val result =
            FirestoreDatabaseProvider.zoneDatabase!!.getZoneInformation(zoneID, obsZone, user)
        val value = obsZone.value!!
        assert(result.value!!)
        assert(value.zoneName == zoneName)
        assert(value.description == zoneDesc)
        assert(value.location == zoneLoc)
        assert(value.zoneId == zoneID)
    }

}