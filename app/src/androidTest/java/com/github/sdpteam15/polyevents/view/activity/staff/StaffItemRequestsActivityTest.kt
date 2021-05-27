package com.github.sdpteam15.polyevents.view.activity.staff


import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.fakedatabase.*
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest.Status.*
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdminAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.time.LocalDateTime

class StaffItemRequestsActivityTest {

    lateinit var staffItemActivity: ActivityScenario<StaffRequestsActivity>
    lateinit var availableItems: MutableMap<Item, Int>
    lateinit var availableRequests: MutableList<MaterialRequest>
    lateinit var availableUsers: MutableList<UserEntity>
    lateinit var availableZones: MutableList<Zone>
    lateinit var availableEvents: MutableList<Event>
    val availableItemsList = ObservableList<Triple<Item, Int, Int>>()
    val availableRequestsList = ObservableList<MaterialRequest>()
    val availableZoneList = ObservableList<Zone>()
    val availableUsersList = ObservableList<UserEntity>()
    val availableEventList = ObservableList<Event>()
    val explanation = "TESTREFUSE"
    val staffId = "staff"


    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Before
    fun setup() {
        Database.currentDatabase = FakeDatabase
        FakeDatabase.CURRENT_USER = UserEntity(staffId, "STAFF")
        availableItems = mutableMapOf()
        availableItems[Item("i1", "Bananas", "Fruit")] = 30
        availableItems[Item("i2", "Kiwis", "Fruit")] = 10
        availableItems[Item("i3", "230V Plugs", "Plug")] = 30
        availableItems[Item("i4", "Fridge (large)", "Fridge")] = 5
        availableItems[Item("i5", "Cord rewinder (15m)", "Plug")] = 30
        availableItems[Item("i6", "Cord rewinder (50m)", "Plug")] = 10
        availableItems[Item("i7", "Cord rewinder (25m)", "Plug")] = 20

        availableUsers = mutableListOf(
            UserEntity("u1", "U1"),
            UserEntity("u2", "U2"),
            UserEntity(staffId, "STAFF")
        )

        availableEvents = mutableListOf(
            Event("e1", "EVENT1", "u1")
        )

        availableZones = mutableListOf(
            Zone("z1", "ZONE1")
        )

        availableRequests = mutableListOf(
            MaterialRequest(
                "r1",
                mutableMapOf(
                    Pair("i1", 5)
                ),
                LocalDateTime.of(2021, 3, 24, 12, 23),
                Database.currentDatabase.currentUser!!.uid,
                "e1",
                ACCEPTED,
                null,
                null
            ),
            MaterialRequest(
                "r2",
                mutableMapOf(
                    Pair("i3", 10),
                    Pair("i1", 5),
                    Pair("i6", 4)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid,
                "e1",
                ACCEPTED,
                null,
                null,
            ),
            MaterialRequest(
                "r3",
                mutableMapOf(
                    Pair("i3", 10)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid,
                "e1",
                DELIVERED,
                null,
                staffId
            ),
            MaterialRequest(
                "r4",
                mutableMapOf(
                    Pair("i3", 10)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid,
                "e1",
                RETURN_REQUESTED,
                null,
                null
            ),
            MaterialRequest(
                "r5",
                mutableMapOf(
                    Pair("i3", 10)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid,
                "e1",
                RETURNED,
                null,
                staffId
            )
        )

        FakeDatabaseMaterialRequest.requests.clear()
        FakeDatabaseItem.items.clear()
        FakeDatabaseEvent.events.clear()
        FakeDatabaseZone.zones.clear()
        FakeDatabaseUser.allUsers.clear()

        for ((item, count) in availableItems) {
            Database.currentDatabase.itemDatabase!!.updateItem(item, count, count)
        }
        Database.currentDatabase.itemDatabase!!.getItemsList(availableItemsList)
        for (request in availableRequests) {
            Database.currentDatabase.materialRequestDatabase!!.updateMaterialRequest(
                request.requestId!!,
                request
            )
        }
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestList(
            availableRequestsList
        )
        for (zone in availableZones) {
            Database.currentDatabase.zoneDatabase!!.createZone(zone)
        }
        Database.currentDatabase.zoneDatabase!!.getAllZones(null, null, availableZoneList)

        for (event in availableEvents) {
            Database.currentDatabase.eventDatabase!!.createEvent(event)
        }
        Database.currentDatabase.eventDatabase!!.getEvents(null, null, availableEventList)

        FakeDatabaseUser.allUsers.addAll(availableUsers)
        Database.currentDatabase.userDatabase!!.getListAllUsers(availableUsersList)

        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                StaffRequestsActivity::class.java
            ).also { it.putExtra(EXTRA_ID_USER_STAFF, staffId) }
        staffItemActivity = ActivityScenario.launch(intent)

        Thread.sleep(200)
    }

    @Test
    fun acceptedRequestsAreAllDisplayed() {
        onView(withId(R.id.id_recycler_staff_item_requests))
            .check(RecyclerViewItemCountAssertion(availableRequests.filter { it.status in listOf(ACCEPTED,DELIVERING,DELIVERED) }.size))
        onView(withId(R.id.id_change_request_status_right))
            .perform(click())

        onView(withId(R.id.id_recycler_staff_item_requests))
            .check(RecyclerViewItemCountAssertion(availableRequests.filter { it.status in listOf(RETURN_REQUESTED,RETURNING,RETURNED) }.size))
    }

    @Test
    fun statusIsCorrectlyUpdated(){
        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests.values.any { it.status == DELIVERING })
        var id = FakeDatabaseMaterialRequest.requests.entries.first{it.value.status == DELIVERING}.key

        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_delete_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests[id]!!.status == ACCEPTED)
        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests[id]!!.status == DELIVERING)
        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests[id]!!.status == DELIVERED)

//----------------------------------------

        onView(withId(R.id.id_change_request_status_right)).perform(click())

        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)


        assert(FakeDatabaseMaterialRequest.requests.values.any { it.status == RETURNING })

        id = FakeDatabaseMaterialRequest.requests.entries.first{it.value.status == RETURNING}.key


        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_delete_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests[id]!!.status == RETURN_REQUESTED)
        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests[id]!!.status == RETURNING)
        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests[id]!!.status == RETURNED)


    }

    @Test
    fun returnAddItemsToDataBase() {
        onView(withId(R.id.id_change_request_status_right)).perform(click())

        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests.values.any { it.status == RETURNING })

        val request = FakeDatabaseMaterialRequest.requests.values.first{it.status == RETURNING}

        val oldItems = FakeDatabaseItem.items.toMutableMap()

        onView(withId(R.id.id_recycler_staff_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Thread.sleep(100)
        assert(FakeDatabaseMaterialRequest.requests[request.requestId]!!.status == RETURNED)
        for (itemId in request.items.entries){
            val itemTriple = FakeDatabaseItem.items[itemId.key]
            assert(oldItems[itemId.key]!!.third + itemId.value == itemTriple!!.third )
        }
    }

}
