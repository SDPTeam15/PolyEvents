package com.github.sdpteam15.polyevents.database.objects

import android.graphics.Bitmap
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.util.EventAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as When

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private val listProfile = ArrayList<String>()

class EventDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest,
            profiles = listProfile
        )

        //Mock the database and set it as the default database
        mockedDatabase = mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore = mockedDatabase
        EventDatabaseFirestore.firestore = mockedDatabase

        FirestoreDatabaseProvider.lastQuerySuccessListener = null
        FirestoreDatabaseProvider.lastSetSuccessListener = null
        FirestoreDatabaseProvider.lastFailureListener = null
        FirestoreDatabaseProvider.lastGetSuccessListener = null
        FirestoreDatabaseProvider.lastAddSuccessListener = null
    }

    @After
    fun teardown() {
        FirestoreDatabaseProvider.firestore = null
        EventDatabaseFirestore.firestore = null
    }

    @Test
    fun updateEventInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val documentReference = mock(DocumentReference::class.java) as DocumentReference
        val taskReferenceMock = mock(Task::class.java) as Task<Void>

        val testEvent = Event(
            eventId = "event1",
            eventName = "Sushi demo",
            organizer = "The fish band",
            zoneName = "Kitchen",
            description = "Super hungry activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
            endTime = LocalDateTime.of(2021, 3, 7, 12, 45),
            inventory = mutableListOf(),
            tags = mutableSetOf("sushi", "japan", "cooking")
        )

        When(mockedDatabase.collection(EVENT_COLLECTION)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(testEvent.eventId!!)).thenReturn(documentReference)
        When(documentReference.set(EventAdapter.toDocument(testEvent))).thenReturn(
            taskReferenceMock
        )

        var eventId: String? = null
        var eventName: String? = null
        var organizer: String? = null
        var zoneName: String? = null
        var description: String? = null
        var icon: Bitmap? = null
        var startTime: LocalDateTime? = null
        var endTime: LocalDateTime? = null
        var inventory: MutableList<Item> = mutableListOf()
        var tags: MutableSet<String> = mutableSetOf()

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            eventId = testEvent.eventId
            eventName = testEvent.eventName
            organizer = testEvent.organizer
            zoneName = testEvent.zoneName
            description = testEvent.description
            icon = testEvent.icon
            startTime = testEvent.startTime
            endTime = testEvent.endTime
            inventory = testEvent.inventory
            tags = testEvent.tags
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.eventDatabase!!.updateEvents(testEvent)
        assert(result.value!!)
        assert(eventId == testEvent.eventId)
        assert(eventName == testEvent.eventName)
        assert(organizer == testEvent.organizer)
        assert(zoneName == testEvent.zoneName)
        assert(description == testEvent.description)
        assert(icon == testEvent.icon)
        assert(startTime == testEvent.startTime)
        assert(endTime == testEvent.endTime)
        assert(inventory == testEvent.inventory)
        assert(tags == testEvent.tags)
    }

    @Test
    fun addEventInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<DocumentReference>

        val testEvent = Event(
            eventId = "event1",
            eventName = "Sushi demo",
            organizer = "The fish band",
            zoneName = "Kitchen",
            description = "Super hungry activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
            endTime = LocalDateTime.of(2021, 3, 7, 12, 45),
            inventory = mutableListOf(),
            tags = mutableSetOf("sushi", "japan", "cooking")
        )

        When(mockedDatabase.collection(EVENT_COLLECTION)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.add(EventAdapter.toDocument(testEvent))).thenReturn(
            taskReferenceMock
        )

        var eventId: String? = null
        var eventName: String? = null
        var organizer: String? = null
        var zoneName: String? = null
        var description: String? = null
        var icon: Bitmap? = null
        var startTime: LocalDateTime? = null
        var endTime: LocalDateTime? = null
        var inventory: MutableList<Item> = mutableListOf()
        var tags: MutableSet<String> = mutableSetOf()

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastAddSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            eventId = testEvent.eventId
            eventName = testEvent.eventName
            organizer = testEvent.organizer
            zoneName = testEvent.zoneName
            description = testEvent.description
            icon = testEvent.icon
            startTime = testEvent.startTime
            endTime = testEvent.endTime
            inventory = testEvent.inventory
            tags = testEvent.tags
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.eventDatabase!!.createEvent(testEvent)
        assert(result.value!!)
        assert(eventId == testEvent.eventId)
        assert(eventName == testEvent.eventName)
        assert(organizer == testEvent.organizer)
        assert(zoneName == testEvent.zoneName)
        assert(description == testEvent.description)
        assert(icon == testEvent.icon)
        assert(startTime == testEvent.startTime)
        assert(endTime == testEvent.endTime)
        assert(inventory == testEvent.inventory)
        assert(tags == testEvent.tags)
    }

}