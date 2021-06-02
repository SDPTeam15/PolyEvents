package com.github.sdpteam15.polyevents.view.activity


import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.sdpteam15.polyevents.HelperTestFunction.getCurrentActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseItem
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseMaterialRequest
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.activityprovider.EXTRA_ITEM_REQUEST_ID
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@Suppress("UNCHECKED_CAST")
class ItemRequestActivityUpdateTest {
    private lateinit var availableItems: MutableMap<Item, Pair<Int, Int>>
    private var availableItemsList = ObservableList<Triple<Item, Int, Int>>()
    private var nbItemTypes: Int = 0
    private val requestId = "requestId"

    lateinit var itemsAdminActivity: ActivityScenario<ItemRequestActivity>

    private fun setQuantityField(viewId: Int, value: String) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Set the value of the quantity field with specified id."

        override fun perform(uiController: UiController, view: View) = view.findViewById<EditText>(
            viewId
        ).setText(value)
    }

    private fun selectItemQuantity(item: Int, quantity: String) {
        onView(withId(R.id.id_recycler_items_request)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdapter.ItemViewHolder>(
                item,
                setQuantityField(R.id.id_item_quantity, quantity)
            )
        )
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Before
    fun setup() {
        Database.currentDatabase = FakeDatabase

        availableItems = mutableMapOf()
        availableItems[Item(null, "Bananas", "Fruit")] = Pair(30, 20)
        availableItems[Item(null, "Kiwis", "Fruit")] = Pair(10, 5)
        availableItems[Item(null, "230V Plugs", "Plug")] = Pair(30, 29)
        availableItems[Item(null, "Fridge (large)", "Fridge")] = Pair(5, 0)
        availableItems[Item(null, "Cord rewinder (15m)", "Plug")] = Pair(30, 1)
        availableItems[Item(null, "Cord rewinder (50m)", "Plug")] = Pair(10, 2)
        availableItems[Item(null, "Cord rewinder (25m)", "Plug")] = Pair(20, 2)

        val types = mutableSetOf<String>()
        nbItemTypes = availableItems.count { types.add(it.key.itemType) }
        // TODO : replace by the db interface call

        FakeDatabaseItem.items.clear()
        for ((item, count) in availableItems) {
            Database.currentDatabase.itemDatabase!!.createItem(item, count.first)
        }
        Database.currentDatabase.itemDatabase!!.getItemsList(availableItemsList)
        FakeDatabaseMaterialRequest.requests.clear()

        FakeDatabaseMaterialRequest.requests[requestId] = MaterialRequest(
            requestId,
            mutableMapOf(
                Pair("1", 5)
            ),
            LocalDateTime.of(2021, 3, 24, 12, 23),
            Database.currentDatabase.currentUser!!.uid,
            "1",
            MaterialRequest.Status.PENDING,
            null,
            null

        )


        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ItemRequestActivity::class.java
            ).apply {
                putExtra(
                    EXTRA_ITEM_REQUEST_ID, requestId
                )
            }
        itemsAdminActivity = ActivityScenario.launch(intent)
        Thread.sleep(500)
    }



    private fun openAllCategories() {
        for (position in nbItemTypes - 1 downTo 0) {
            onView(withId(R.id.id_recycler_items_request)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdapter.ItemViewHolder>(
                    position,
                    click()
                )
            )
        }
    }


    @Test
    fun materialRequestUpdateIsSent() {
        openAllCategories()
        selectItemQuantity(1, "2")
        onView(withId(R.id.id_button_make_request)).perform(click())
        assert(FakeDatabaseMaterialRequest.requests.any { it.value.items.any { it2 -> it2.key == availableItemsList[0].first.itemId && it2.value == 2 } })

    }


}


