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
import androidx.test.espresso.matcher.ViewMatchers.assertThat
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
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.activityprovider.ItemRequestActivity
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdapter
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class ItemRequestActivityTest {
    private lateinit var availableItems: MutableMap<Item, Pair<Int, Int>>
    private var availableItemsList = ObservableList<Triple<Item, Int, Int>>()
    private var nbItemTypes: Int = 0

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
            Database.currentDatabase.itemDatabase.createItem(item, count.first)
        }
        Database.currentDatabase.itemDatabase.getItemsList(availableItemsList)

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ItemRequestActivity::class.java)
        itemsAdminActivity = ActivityScenario.launch(intent)
        Thread.sleep(500)
    }

    @Test
    fun correctNumberAvailableItemsTypesDisplayed() {

        onView(withId(R.id.id_recycler_items_request))
            //counts different item types
            .check(RecyclerViewItemCountAssertion(nbItemTypes))
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
    fun itemsRequestListIsCorrect() {
        val itemsToSelect = arrayOf(1, 2)
        val quantityToSelect = arrayOf(30, 3)
        val correctQuantityAfterSelection = arrayOf(30, 3)
        val itemsSelected = mutableMapOf<Item, Int>()
        openAllCategories()

        for (i in itemsToSelect) {
            // Set the quantity wanted for each item
            onView(withId(R.id.id_recycler_items_request)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdapter.ItemViewHolder>(
                    i,
                    setQuantityField(R.id.id_item_quantity, quantityToSelect[i - 1].toString())
                )
            )

            itemsSelected[availableItemsList[i - 1].first] = correctQuantityAfterSelection[i - 1]
        }
        for (k in itemsSelected.keys) {
            assert(
                getCurrentActivity<ItemRequestActivity>().mapSelectedItems.contains(k)
            )
        }
    }


    @Test
    fun materialRequestIsSent() {
        openAllCategories()
        selectItemQuantity(1, "1")
        onView(withId(R.id.id_button_make_request)).perform(click())
        assert(FakeDatabaseMaterialRequest.requests.any { it.value.items.any { it2 -> it2.key == availableItemsList[0].first.itemId } })
    }


    @Test
    fun settingNegativeQuantityMakeEmptyRequest() {
        openAllCategories()
        val quantityToSelect = 1
        val itemToSelect = 1
        selectItemQuantity(1, "-1")
        selectItemQuantity(itemToSelect, quantityToSelect.toString())

        val correctMap = mutableMapOf<Item, Int>()
        correctMap[availableItemsList[itemToSelect - 1].first] = quantityToSelect

        for (k in correctMap.keys) {
            assert(
                getCurrentActivity<ItemRequestActivity>().mapSelectedItems.contains(k)
            )
        }
    }

    @Test
    fun settingTooHighQuantityMakeMaxRequest() {
        openAllCategories()
        val quantityToSelect = 10000
        val itemToSelect = 1
        selectItemQuantity(itemToSelect, quantityToSelect.toString())

        val correctMap = mutableMapOf<Item, Int>()
        correctMap[availableItemsList[itemToSelect - 1].first] =
            availableItemsList[itemToSelect].second

        for (k in correctMap.keys) {
            assert(
                getCurrentActivity<ItemRequestActivity>().mapSelectedItems.contains(k)
            )
        }
    }

    @Test
    fun settingEmptyStringForQuantityHasNoEffect() {
        openAllCategories()
        // Setting empty string as a quantity has no effect on anything
        selectItemQuantity(1, "")

        assertThat(
            getCurrentActivity<ItemRequestActivity>().mapSelectedItems.size, `is`(
                0
            )
        )
    }

}


