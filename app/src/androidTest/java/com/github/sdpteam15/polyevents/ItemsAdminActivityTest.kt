package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FakeDatabase
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ItemsAdminActivityTest {

    lateinit var availableItems: MutableMap<Item, Int>
    var items = ObservableList<Pair<Item, Int>>()
    lateinit var mockedUpcomingEventsProvider: DatabaseInterface

    private val testItem = Item(null, "test item", ItemType.OTHER)
    private val testQuantity = 3

    @Rule
    @JvmField
    var itemsAdminActivity = ActivityScenarioRule(ItemsAdminActivity::class.java)

    @Before
    fun setup() {
        availableItems = mutableMapOf()
        availableItems[Item(null, "Bananas", ItemType.OTHER)] = 30
        availableItems[Item(null, "Kiwis", ItemType.OTHER)] = 10
        availableItems[Item(null, "230 Plugs", ItemType.PLUG)] = 30
        availableItems[Item(null, "Fridge (large)", ItemType.OTHER)] = 5
        availableItems[Item(null, "Cord rewinder (15m)", ItemType.PLUG)] = 30
        availableItems[Item(null, "Cord rewinder (50m)", ItemType.PLUG)] = 10
        availableItems[Item(null, "Cord rewinder (25m)", ItemType.PLUG)] = 20


        // TODO : replace by the db interface call
        currentDatabase = FakeDatabase
        FakeDatabase.items.clear()
        for ((item, count) in availableItems) {
            currentDatabase.createItem(item, count)
        }
        currentDatabase.getItemsList(items)

        itemsAdminActivity.scenario.recreate()

    }

    @After
    fun tearDown(){
        currentDatabase = FirestoreDatabaseProvider
    }


    @Test
    fun correctNumberItemsDisplayed() {
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(availableItems.size))
    }

    @Test
    fun addButtonPopupAddsItemToList() {
        //TODO fix this test, seems like fakeitems is not updated correctly
        onView(withId(R.id.id_add_item_button)).perform(click())
        onView(withId(R.id.id_edittext_item_name)).perform(typeText(testItem.itemName))
        closeSoftKeyboard()
        onView(withId(R.id.id_edittext_item_quantity)).perform(typeText(testQuantity.toString()))
        closeSoftKeyboard()
        onView(withId(R.id.id_confirm_add_item_button)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(availableItems.size+1))
    }

    //Test passes manually but not with gradle
    @Test
    fun removeButtonRemovesItemFromList() {
        onView(withId(R.id.id_recycler_items_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_remove_item)
            )
        )
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(availableItems.size-1))
    }
}