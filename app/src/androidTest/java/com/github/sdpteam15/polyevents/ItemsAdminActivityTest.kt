package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.event.EventItemAdapter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ItemsAdminActivityTest {

    var fakeitems: MutableList<String> =
        mutableListOf("Sushi knife", "Pony saddle", "Electric guitar")
    lateinit var mockedUpcomingEventsProvider: DatabaseInterface

    private val testItem = "TestItem"

    @Rule
    @JvmField
    var itemsAdminActivity = ActivityScenarioRule(ItemsAdminActivity::class.java)

    @Before
    fun setup() {

        mockedUpcomingEventsProvider = mock(DatabaseInterface::class.java)

        `when`(mockedUpcomingEventsProvider.getItemsList()).thenReturn(fakeitems)
        `when`(mockedUpcomingEventsProvider.addItem(anyString())).thenAnswer{fakeitems.add(testItem)}
        `when`(mockedUpcomingEventsProvider.removeItem(anyString())).thenAnswer{it ->
            val v = it.arguments[0] as String
            println("NTM"+ v)
            fakeitems.remove(v)}
        currentDatabase = mockedUpcomingEventsProvider
        itemsAdminActivity.scenario.recreate()

    }


    @Test
    fun correctNumberItemsDisplayed() {
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(3))
    }

    @Test
    fun addButtonPopupAddsItemToList() {
        //TODO fix this test, seems like fakeitems is not updated correctly
        onView(withId(R.id.id_add_item_button)).perform(click())
        onView(withId(R.id.id_edittext_item_name)).perform(typeText(testItem))
        closeSoftKeyboard()
        onView(withId(R.id.id_confirm_add_item_button)).perform(click())
        onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(4))
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
            .check(RecyclerViewItemCountAssertion(2))
    }

}
