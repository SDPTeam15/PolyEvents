package com.github.sdpteam15.polyevents.view.activity

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseItem
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.view.activity.admin.ItemsAdminActivity
import com.github.sdpteam15.polyevents.view.adapter.EventItemAdapter
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test


class ItemsAdminActivityTest {

    lateinit var availableItems: MutableMap<Item, Int>
    lateinit var availableItemTypes: MutableList<String>

    private val testItem = Item(null, "test item", "TESTITEMTYPE")
    private val testQuantity = 3
    private val itemToTest = Item(null, "Chocolat", "Food")

    lateinit var itemsAdminActivity: ActivityScenario<ItemsAdminActivity>

    @Before
    fun setup() {
        availableItems = mutableMapOf()
        availableItems[itemToTest] = 30
        availableItems[Item(null, "Kiwis", "Food")] = 10
        availableItems[Item(null, "230V Plugs", "Plug")] = 30
        availableItems[Item(null, "Fridge (large)", "Fridge")] = 5
        availableItems[Item(null, "Cord rewinder (15m)", "Plug")] = 30
        availableItems[Item(null, "Cord rewinder (50m)", "Plug")] = 10
        availableItems[Item(null, "Cord rewinder (25m)", "Plug")] = 20

        availableItemTypes = mutableListOf("Food", "Plug", "Fridge")

        currentDatabase = FakeDatabase

        FakeDatabaseItem.items.clear()
        for ((item, count) in availableItems) {
            currentDatabase.itemDatabase!!.createItem(item, count)
        }

        FakeDatabaseItem.itemTypes.clear()
        for (itemType in availableItemTypes) {
            currentDatabase.itemDatabase!!.createItemType(itemType)
        }


        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ItemsAdminActivity::class.java)
        itemsAdminActivity = ActivityScenario.launch(intent)

        //itemsAdminActivity.scenario.recreate()
        Thread.sleep(1000)
    }

    @After
    fun tearDown() {
        currentDatabase = FirestoreDatabaseProvider
    }


    @Test
    fun correctNumberItemsDisplayed() {
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(availableItems.size))
    }

    @Test
    fun addButtonPopupAddsItemToList() {
        onView(withId(R.id.id_add_item_button)).perform(click())
        onView(withId(R.id.id_edittext_item_name)).perform(replaceText(testItem.itemName))
        closeSoftKeyboard()
        onView(withId(R.id.id_edittext_item_quantity)).perform(replaceText(testQuantity.toString()))
        closeSoftKeyboard()
        onView(withId(R.id.id_edittext_item_type)).perform(replaceText(testItem.itemType))
        closeSoftKeyboard()
        onView(withId(R.id.id_confirm_add_item_button)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(availableItems.size + 1))
        assert(FakeDatabaseItem.itemTypes.contains(testItem.itemType))
    }

    @Test
    fun removeButtonRemovesItemFromList() {
        onView(withId(R.id.id_recycler_items_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_remove_item)
            )
        )
        Thread.sleep(1000)
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(availableItems.size - 1))
    }

    @Test
    fun modifyItemCorrectlyDisplays() {
        onView(withId(R.id.id_recycler_items_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0,
                click()
            )
        )

        Thread.sleep(1000)
        onView(withId(R.id.id_edittext_item_name))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            itemToTest.itemName
                        )
                    )
                )
            )
        onView(withId(R.id.id_edittext_item_quantity))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            availableItems[itemToTest]!!.toString()
                        )
                    )
                )
            )
        onView(withId(R.id.id_edittext_item_type))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            itemToTest.itemType
                        )
                    )
                )
            )
    }

    @Test
    fun modifyItemTest() {
        val newName = "Choco"
        onView(withId(R.id.id_recycler_items_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.id_edittext_item_name)).perform(typeText(newName))
        closeSoftKeyboard()
        onView(withId(R.id.id_confirm_add_item_button)).perform(click())
        Thread.sleep(1000)

        onView(withId(R.id.id_recycler_items_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.id_edittext_item_name))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            itemToTest.itemName + newName
                        )
                    )
                )
            )
        onView(withId(R.id.id_edittext_item_quantity))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            availableItems[itemToTest]!!.toString()
                        )
                    )
                )
            )
        onView(withId(R.id.id_edittext_item_type))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            itemToTest.itemType
                        )
                    )
                )
            )
    }


}