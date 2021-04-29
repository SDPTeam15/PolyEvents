package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseItem
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseMaterialRequest
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.MaterialRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ItemRequestManagementTest {

    lateinit var itemsAdminActivity: ActivityScenario<ItemRequestManagementActivity>
    lateinit var availableItems: MutableMap<Item, Int>
    lateinit var availableRequests: MutableList<MaterialRequest>
    val availableItemsList = ObservableList<Pair<Item, Int>>()
    val availableRequestsList = ObservableList<MaterialRequest>()


    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Before
    fun setup() {
        availableItems = mutableMapOf()
        availableItems[Item("1", "Bananas", "Fruit")] = 30
        availableItems[Item("2", "Kiwis", "Fruit")] = 10
        availableItems[Item("3", "230V Plugs", "Plug")] = 30
        availableItems[Item("4", "Fridge (large)", "Fridge")] = 5
        availableItems[Item("5", "Cord rewinder (15m)", "Plug")] = 30
        availableItems[Item("6", "Cord rewinder (50m)", "Plug")] = 10
        availableItems[Item("7", "Cord rewinder (25m)", "Plug")] = 20

        availableRequests = mutableListOf(
            MaterialRequest(
                "r1",
                mutableMapOf(
                    Pair("1", 5)
                ),
                LocalDateTime.of(2021, 3, 24, 12, 23),
                Database.currentDatabase.currentUser!!.uid
            ),
            MaterialRequest(
                "r2",
                mutableMapOf(
                    Pair("3", 10),
                    Pair("1", 5),
                    Pair("6", 4)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid
            ),
            MaterialRequest(
                "r3",
                mutableMapOf(
                    Pair("3", 10)
                ),
                LocalDateTime.of(2021, 3, 29, 1, 6),
                Database.currentDatabase.currentUser!!.uid
            )
        )
        Database.currentDatabase = FakeDatabase
        FakeDatabaseMaterialRequest.requests.clear()
        FakeDatabaseItem.items.clear()

        for ((item, count) in availableItems) {
            Database.currentDatabase.itemDatabase!!.createItem(item, count)
        }
        Database.currentDatabase.itemDatabase!!.getItemsList(availableItemsList)

        for (request in availableRequests) {
            Database.currentDatabase.materialRequestDatabase!!.createMaterialRequest(request)
        }
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestList(
            availableRequestsList
        )


        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ItemRequestManagementActivity::class.java
            )
        itemsAdminActivity = ActivityScenario.launch(intent)

        Thread.sleep(500)
    }

    @Test
    fun requestsAreAllDisplayed() {
        Espresso.onView(withId(R.id.id_recycler_item_requests))
            .check(RecyclerViewItemCountAssertion(availableRequests.size))
    }

}