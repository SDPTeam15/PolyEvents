package com.github.sdpteam15.polyevents.view.activity.staff


import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.fakedatabase.*
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdminAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
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


    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Before
    fun setup() {
        Database.currentDatabase = FakeDatabase
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
            UserEntity("staff", "STAFF")
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
                MaterialRequest.Status.RETURN_REQUESTED,
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
                MaterialRequest.Status.PENDING,
                null,
                null
            ),
            MaterialRequest(
                "r3",
                mutableMapOf(
                    Pair("i3", 10)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid,
                "e1",
                MaterialRequest.Status.PENDING,
                null,
                null
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
            )
        staffItemActivity = ActivityScenario.launch(intent)

        Thread.sleep(500)
    }

    @Test
    fun requestsAreAllDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_item_requests))
            .check(RecyclerViewItemCountAssertion(availableRequests.size))

    }

    @Test
    fun acceptRequestChangeStatus() {
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_request_accept)
            )
        )
        assert(FakeDatabaseMaterialRequest.requests.values.any { it.status == MaterialRequest.Status.ACCEPTED })
    }

    @Test
    fun refusePopupAndChangeStatus() {
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdminAdapter.ItemViewHolder>(
                0,
                TestHelper.clickChildViewWithId(R.id.id_request_refuse)
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.id_txt_refusal_explanation))
            .perform(ViewActions.typeText(explanation))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.id_btn_confirm_refuse_request))
            .perform(ViewActions.click())


        assert(FakeDatabaseMaterialRequest.requests.values.any { it.status == MaterialRequest.Status.REFUSED && it.adminMessage == explanation })
    }

}
