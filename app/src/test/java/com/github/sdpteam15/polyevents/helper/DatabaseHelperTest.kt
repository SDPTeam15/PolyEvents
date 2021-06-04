package com.github.sdpteam15.polyevents.helper

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Query
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.Mockito.`when` as When

class DatabaseHelperTest {
    lateinit var event: Event
    lateinit var zone: Zone

    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedEventDb: EventDatabaseInterface
    lateinit var mockedZoneDb: ZoneDatabaseInterface
    lateinit var mockedItemDb: ItemDatabaseInterface
    lateinit var mockedRouteDb : RouteDatabaseInterface
    lateinit var mockedMaterialRequestDb: MaterialRequestDatabaseInterface

    lateinit var cancelMaterialRequest: MutableList<MaterialRequest>
    lateinit var updatedItems: MutableList<Triple<Item, Int, Int>>
    lateinit var cancelEventEdits: MutableList<Event>
    lateinit var cancelEvents: MutableList<Event>
    lateinit var updatedZone: MutableList<Zone>

    lateinit var allMaterialRequest: MutableList<MaterialRequest>
    lateinit var allItems: MutableList<Triple<Item, Int, Int>>
    lateinit var allEventEdits: MutableList<Event>
    lateinit var allEvents: MutableList<Event>

    private fun createContent() {
        cancelMaterialRequest = mutableListOf()
        updatedItems = mutableListOf()
        cancelEventEdits = mutableListOf()
        cancelEvents = mutableListOf()
        updatedZone = mutableListOf()

        event = Event(
            eventName = "Test 1",
            eventId = "Id1",
            zoneId = " zid1",
            zoneName = "zoneName1",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            organizer = "idA"
        )
        zone = Zone(zoneId = "zid1", zoneName = "zoneName1")

        allMaterialRequest = mutableListOf(
            MaterialRequest(
                "r1",
                mutableMapOf(
                    Pair("1", 5)
                ),
                LocalDateTime.of(2021, 3, 24, 12, 23),
                "Id1",
                "Id1",
                MaterialRequest.Status.ACCEPTED,
                null,
                null
            ),
            MaterialRequest(
                "r2",
                mutableMapOf(),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                "userId",
                "Id1",
                MaterialRequest.Status.PENDING,
                null,
                null
            ),
            MaterialRequest(
                "r2",
                mutableMapOf(),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                "userId",
                "Id1",
                MaterialRequest.Status.DELIVERED,
                null,
                null
            ),
            MaterialRequest(
                "r2",
                mutableMapOf(
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                "userId",
                "Id1",
                MaterialRequest.Status.DELIVERING,
                null,
                null
            ),
            MaterialRequest(
                "r2",
                mutableMapOf(
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                "userId",
                "Id1",
                MaterialRequest.Status.RETURNING,
                null,
                null
            )
        )

        allEvents = mutableListOf(event)
        allEventEdits = mutableListOf(
            Event(
                eventName = "Test 4",
                eventId = "Id1",
                zoneId = " zid1",
                zoneName = "zoneName1",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                organizer = "idAs"
            ), Event(
                eventName = "Test 3",
                eventId = "Id1",
                zoneId = " zid1",
                zoneName = "zoneName1",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                organizer = "idBs"
            )
        )

        allItems = mutableListOf(
            Triple(Item(itemId = "1", itemName = "testItem", itemType = "Webcam"), 6, 1)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun mockMethods() {

        val mockQuery1 = mock(Query::class.java)
        When(mockQuery1.whereEqualTo(anyOrNull(), anyOrNull())).thenAnswer {
            assertEquals(DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value, it.arguments[0])
            mockQuery1
        }
        val mockQuery2 = mock(Query::class.java)
        When(mockQuery2.whereEqualTo(anyOrNull(), anyOrNull())).thenAnswer {
            assertEquals(DatabaseConstant.EventConstant.EVENT_ZONE_ID.value, it.arguments[0])
            mockQuery2
        }
        val mockQuery3 = mock(Query::class.java)
        When(mockQuery3.whereEqualTo(anyOrNull(), anyOrNull())).thenAnswer {
            assertEquals(
                DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_EVENT_ID.value,
                it.arguments[0]
            )
            mockQuery3
        }

        When(mockedMaterialRequestDb.updateMaterialRequest(anyOrNull(), anyOrNull())).thenAnswer {
            cancelMaterialRequest.add(it.arguments[1] as MaterialRequest)
            Observable(true)
        }

        When(mockedMaterialRequestDb.getMaterialRequestList(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[0] as ObservableList<MaterialRequest>).addAll(allMaterialRequest)
            (it.arguments[1] as Matcher).match(mockQuery3)
            assertNotNull(it.arguments[1])
            Observable(true)
        }

        When(mockedEventDb.getEvents(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[2] as ObservableList<Event>).addAll(allEvents)
            (it.arguments[0] as Matcher).match(mockQuery2)
            assertNotNull(it.arguments[0])
            Observable(true)
        }

        When(mockedEventDb.getEventEdits(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[1] as ObservableList<Event>).addAll(allEventEdits)

            (it.arguments[0] as Matcher).match(mockQuery1)
            assertNotNull(it.arguments[0])
            Observable(true)
        }

        When(mockedItemDb.getItemsList(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[0] as ObservableList<Triple<Item, Int, Int>>).addAll(allItems)
            Observable(true)
        }

        When(mockedItemDb.updateItem(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            updatedItems.add(
                Triple(
                    it.arguments[0] as Item,
                    it.arguments[1] as Int,
                    it.arguments[2] as Int
                )
            )
            Observable(true)
        }

        When(mockedEventDb.updateEventEdit(anyOrNull())).thenAnswer {
            cancelEventEdits.add(it.arguments[0] as Event)
            Observable(true)
        }

        When(mockedZoneDb.updateZoneInformation(anyOrNull(),anyOrNull())).thenAnswer {
            updatedZone.add(it.arguments[1] as Zone)
            Observable(true)
        }

        When(mockedEventDb.removeEvent(anyOrNull())).thenAnswer {
            cancelEvents.add(Event(eventId = it.arguments[0] as String))
            Observable(true)
        }

    }

    @Before
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        PolyEventsApplication.inTest = true

        UserLogin.currentUserLogin =
            mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        createContent()
        mockedDatabase = HelperTestFunction.mockDatabaseInterface()
        mockedEventDb = mock(EventDatabaseInterface::class.java)
        mockedZoneDb = mock(ZoneDatabaseInterface::class.java)
        mockedItemDb = mock(ItemDatabaseInterface::class.java)
        mockedRouteDb = mock(RouteDatabaseInterface::class.java)
        mockedMaterialRequestDb = mock(MaterialRequestDatabaseInterface::class.java)
        When(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDb)
        When(mockedDatabase.itemDatabase).thenReturn(mockedItemDb)
        When(mockedDatabase.materialRequestDatabase).thenReturn(mockedMaterialRequestDb)
        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDb)
        When(mockedDatabase.routeDatabase).thenReturn(mockedRouteDb)
        Database.currentDatabase = mockedDatabase
        mockMethods()

    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
        UserLogin.currentUserLogin = GoogleUserLogin
    }

    @Test
    fun removeEventWorksProperly() {
        DatabaseHelper.deleteEvent(event)
        Thread.sleep(2500)
        assertEquals(allMaterialRequest.size - 1, cancelMaterialRequest.size)
        assertEquals(allItems.size, updatedItems.size)
        assertEquals(allEventEdits.size, cancelEventEdits.size)
        assertEquals(allEvents.size, cancelEvents.size)

        assertEquals(cancelMaterialRequest[0].status, MaterialRequest.Status.CANCELED)
        assertEquals(cancelMaterialRequest[1].status, MaterialRequest.Status.CANCELED)
        assertEquals(cancelMaterialRequest[2].status, MaterialRequest.Status.RETURN_REQUESTED)
        assertEquals(cancelMaterialRequest[3].status, MaterialRequest.Status.RETURNING)
        assertEquals(
            updatedItems[0],
            Triple(Item(itemId = "1", itemName = "testItem", itemType = "Webcam"), 6, 6)
        )

        for (e in cancelEventEdits) {
            assertEquals(e.status, Event.EventStatus.CANCELED)
        }
    }

    @Test
    fun removeZoneWorksProperly() {
        DatabaseHelper.deleteZone(zone)
        Thread.sleep(2500)
        assertEquals(allMaterialRequest.size - 1, cancelMaterialRequest.size)
        assertEquals(allItems.size, updatedItems.size)
        assertEquals(allEventEdits.size, cancelEventEdits.size)
        assertEquals(allEvents.size, cancelEvents.size)

        assertEquals(updatedZone[0].status, Zone.Status.DELETED)
        assertEquals(cancelMaterialRequest[0].status, MaterialRequest.Status.CANCELED)
        assertEquals(cancelMaterialRequest[1].status, MaterialRequest.Status.CANCELED)
        assertEquals(cancelMaterialRequest[2].status, MaterialRequest.Status.RETURN_REQUESTED)
        assertEquals(cancelMaterialRequest[3].status, MaterialRequest.Status.RETURNING)
        assertEquals(
            updatedItems[0],
            Triple(Item(itemId = "1", itemName = "testItem", itemType = "Webcam"), 6, 6)
        )

        for (e in cancelEventEdits) {
            assertEquals(e.status, Event.EventStatus.CANCELED)
        }
    }
}