package com.github.sdpteam15.polyevents.database.objects

import android.graphics.Bitmap
import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.database.remote.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private val listProfile = ArrayList<String>()

@Suppress("UNCHECKED_CAST")
class EventDatabaseTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var mockedEventdatabase: EventDatabase

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

        FirestoreDatabaseProvider.lastQuerySuccessListener = null
        FirestoreDatabaseProvider.lastSetSuccessListener = null
        FirestoreDatabaseProvider.lastFailureListener = null
        FirestoreDatabaseProvider.lastGetSuccessListener = null
        FirestoreDatabaseProvider.lastAddSuccessListener = null


    }
    @Before
    fun setup2() {
        val mockDatabaseInterface = HelperTestFunction.mockFor()
        mockedEventdatabase = EventDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @After
    fun teardown() {
        FirestoreDatabaseProvider.firestore = null
        UserLogin.currentUserLogin = GoogleUserLogin
    }

    @Test
    fun variableCorrectlySet(){
        val mockedUserLogin = mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        UserLogin.currentUserLogin = mockedUserLogin
        FirestoreDatabaseProvider.currentUser = user
        Mockito.`when`(mockedUserLogin.isConnected()).thenReturn(true)
        FirestoreDatabaseProvider.currentProfile = UserProfile()
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

        When(mockedDatabase.collection(EVENT_COLLECTION.value)).thenReturn(mockedCollectionReference)
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

        When(mockedDatabase.collection(EVENT_COLLECTION.value)).thenReturn(mockedCollectionReference)
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

    @Test
    fun getEventListInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedQuerySnapshot = mock(QuerySnapshot::class.java) as QuerySnapshot

        val testEvents = ObservableList<Event>()

        val eventsToBeAdded = mutableListOf<Event>()
        eventsToBeAdded.add(
            Event(
            eventId = "event1",
            eventName = "Sushi demo",
            description = "Super hungry activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
            organizer = "The fish band",
            zoneName = "Kitchen",
            tags = mutableSetOf("sushi", "japan", "cooking")
        )
        )
        eventsToBeAdded.add(
            Event(
            eventId = "event2",
            eventName = "Saxophone demo",
            description = "Super noisy activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
            organizer = "The music band",
            zoneName = "Concert Hall"
        )
        )
        eventsToBeAdded.add(
            Event(
            eventId = "event3",
            eventName = "Aqua Poney",
            description = "Super cool activity !" +
                    " With a super long description that essentially describes and explains" +
                    " the content of the activity we are speaking of.",
            startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
            organizer = "The Aqua Poney team",
            zoneName = "Swimming pool"
        )
        )


        When(mockedDatabase.collection(EVENT_COLLECTION.value)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.get()).thenReturn(
            taskReferenceMock
        )

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedQuerySnapshot)
            //set method in hard to see if the success listener is successfully called
            testEvents.addAll(eventsToBeAdded)
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.eventDatabase!!.getEvents(eventList = testEvents)
        assert(result.value!!)
        for (event in eventsToBeAdded){
            assert(event in testEvents)
        }
    }
    @Test
    fun getEventFromIdInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<DocumentSnapshot>
        val mockedDocumentSnapshot = mock(DocumentSnapshot::class.java) as DocumentSnapshot
        val mockedDocumentReference = mock(DocumentReference::class.java) as DocumentReference

        val testEvents = Observable<Event>()

        val eventTotest = Event(
            eventId = "event1",
            eventName = "Sushi demo",
            description = "Super hungry activity !",
            organizer = "The fish band",
            zoneName = "Kitchen",
            tags = mutableSetOf("sushi", "japan", "cooking")
        )
        val eventDoc = EventAdapter.toDocument(eventTotest)


        When(mockedDatabase.collection(EVENT_COLLECTION.value)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(anyOrNull())).thenReturn(
            mockedDocumentReference
        )
        When(mockedDocumentReference.get()).thenReturn(taskReferenceMock)
        When(mockedDocumentSnapshot.data).thenReturn(eventDoc)
        When(mockedDocumentSnapshot.id).thenReturn(eventTotest.eventId)
        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastGetSuccessListener!!.onSuccess(mockedDocumentSnapshot)
            //set method in hard to see if the success listener is successfully called
            testEvents.postValue(eventTotest)
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.eventDatabase!!.getEventFromId("event1",testEvents)
        assert(result.value!!)
        assert(eventTotest == testEvents.value)

    }
}