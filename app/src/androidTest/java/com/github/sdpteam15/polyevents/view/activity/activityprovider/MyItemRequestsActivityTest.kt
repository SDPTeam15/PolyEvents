package com.github.sdpteam15.polyevents.view.activity.activityprovider

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
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.view.fragments.home.ProviderHomeFragment
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime

class MyItemRequestsActivityTest {

    lateinit var itemsAdminActivity: ActivityScenario<MyItemRequestsActivity>
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedMaterialRequestDB: MaterialRequestDatabaseInterface
    lateinit var mockedItemDB: ItemDatabaseInterface

    lateinit var availableItems: MutableMap<Item, Int>
    lateinit var allRequests: MutableList<MaterialRequest>
    val availableItemsList = ObservableList<Triple<Item, Int, Int>>()
    val availableRequestsList = ObservableList<MaterialRequest>()
    val fakeUserId = "FAKE USER ID"
    val fakeUsername = "POLO"
    var accepted = 0
    var refused = 0
    var pending = 0

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    fun setupItemRequests(){
        availableItems = mutableMapOf()
        availableItems[Item("1", "Bananas", "Fruit")] = 30
        availableItems[Item("2", "Kiwis", "Fruit")] = 10
        availableItems[Item("3", "230V Plugs", "Plug")] = 30
        availableItems[Item("4", "Fridge (large)", "Fridge")] = 5
        availableItems[Item("5", "Cord rewinder (15m)", "Plug")] = 30
        availableItems[Item("6", "Cord rewinder (50m)", "Plug")] = 10
        availableItems[Item("7", "Cord rewinder (25m)", "Plug")] = 20

        allRequests = mutableListOf(
            MaterialRequest(
                "r1",
                mutableMapOf(
                    Pair("1", 5)
                ),
                LocalDateTime.of(2021, 3, 24, 12, 23),
                Database.currentDatabase.currentUser!!.uid,
                MaterialRequest.Status.PENDING,
                null
            ),
            MaterialRequest(
                "r2",
                mutableMapOf(
                    Pair("3", 10),
                    Pair("1", 5),
                    Pair("6", 4)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid,
                MaterialRequest.Status.PENDING,
                null
            ),
            MaterialRequest(
                "r3",
                mutableMapOf(
                    Pair("3", 10)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid,
                MaterialRequest.Status.PENDING,
                null
            ),
            MaterialRequest(
            "r1",
            mutableMapOf(
                Pair("1", 5)
            ),
            LocalDateTime.of(2021, 3, 24, 12, 23),
            Database.currentDatabase.currentUser!!.uid,
            MaterialRequest.Status.ACCEPTED,
            null
        ),
        MaterialRequest(
            "r2",
            mutableMapOf(
                Pair("3", 10),
                Pair("1", 5),
                Pair("6", 4)
            ),
            LocalDateTime.of(2021, 3, 29, 1, 6),
            Database.currentDatabase.currentUser!!.uid,
            MaterialRequest.Status.ACCEPTED,
            null
        ),
        MaterialRequest(
            "r3",
            mutableMapOf(
                Pair("3", 10)
            ),
            LocalDateTime.of(2021, 3, 29, 1, 6),
            Database.currentDatabase.currentUser!!.uid,
            MaterialRequest.Status.REFUSED,
            null
        )
        )

        for(r in allRequests){
            when(r.status){
                MaterialRequest.Status.ACCEPTED -> accepted++
                MaterialRequest.Status.REFUSED -> refused++
                MaterialRequest.Status.PENDING -> pending++
            }
        }
    }

    @Before
    fun setup() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                MyItemRequestsActivity::class.java
            )
        intent.putExtra(ProviderHomeFragment.ID_USER, fakeUserId)
        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        mockedMaterialRequestDB = Mockito.mock(MaterialRequestDatabaseInterface::class.java)
        mockedItemDB = Mockito.mock(ItemDatabaseInterface::class.java)

        Database.currentDatabase = mockedDatabase

        val userEntity = UserEntity(fakeUserId, fakeUsername)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(userEntity)
        Mockito.`when`(mockedDatabase.materialRequestDatabase).thenReturn(mockedMaterialRequestDB)
        Mockito.`when`(mockedDatabase.itemDatabase).thenReturn(mockedItemDB)

        Mockito.`when`(mockedMaterialRequestDB.getMaterialRequestListByUser(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[0] as ObservableList<MaterialRequest>).addAll(allRequests)
            Observable(true, this)
        }

        Mockito.`when`(mockedMaterialRequestDB.deleteMaterialRequest(anyOrNull(), anyOrNull())).thenAnswer {
            allRequests.remove(it)
            pending--
            Observable(true, this)
        }

        Mockito.`when`(mockedItemDB.getItemsList(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[0] as ObservableList<Triple<Item, Int, Int>>).addAll(availableItemsList)
            Observable(true, this)
        }

        setupItemRequests()
        itemsAdminActivity = ActivityScenario.launch(intent)
        Thread.sleep(500)
    }

    @Test
    fun sizeOfListOk(){
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(pending))
        Espresso.onView(ViewMatchers.withId(R.id.id_change_request_status_left)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(refused))
        Espresso.onView(ViewMatchers.withId(R.id.id_change_request_status_left)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(accepted))
        Espresso.onView(ViewMatchers.withId(R.id.id_change_request_status_left)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(pending))
        Espresso.onView(ViewMatchers.withId(R.id.id_change_request_status_right)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(accepted))
        Espresso.onView(ViewMatchers.withId(R.id.id_change_request_status_right)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(refused))
        Espresso.onView(ViewMatchers.withId(R.id.id_change_request_status_right)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(pending))

        Espresso.onView(ViewMatchers.withId(R.id.id_title_item_request)).perform(ViewActions.click())
        Espresso.onData(anything()).atPosition(0).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(pending))

        Espresso.onView(ViewMatchers.withId(R.id.id_title_item_request)).perform(ViewActions.click())
        Espresso.onData(anything()).atPosition(1).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(accepted))

        Espresso.onView(ViewMatchers.withId(R.id.id_title_item_request)).perform(ViewActions.click())
        Espresso.onData(anything()).atPosition(2).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(refused))
    }

    @Test
    fun cancelTest(){
        Espresso.onView(ViewMatchers.withId(R.id.id_title_item_request)).perform(ViewActions.click())
        Espresso.onData(anything()).atPosition(0).perform(ViewActions.click())
        Thread.sleep(
            500
        )
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_delete_request)
            )
        )
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.id_recycler_my_item_requests))
            .check(RecyclerViewItemCountAssertion(pending))
    }
}