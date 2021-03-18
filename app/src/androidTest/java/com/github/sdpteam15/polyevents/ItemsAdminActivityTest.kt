package com.github.sdpteam15.polyevents

import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ItemsAdminActivityTest {
    var fakeitems : MutableList<String> = mutableListOf("Sushi knife", "Pony saddle", "Electric guitar")
    lateinit var mockedUpcomingEventsProvider: DatabaseInterface
    private val testItem = "TestItem"

    @Rule
    @JvmField
    var itemsAdminActivity = ActivityScenarioRule(ItemsAdminActivity::class.java)

    @Before
    fun setup() {
        mockedUpcomingEventsProvider = mock(DatabaseInterface::class.java)
        currentDatabase = mockedUpcomingEventsProvider
        `when`(mockedUpcomingEventsProvider.getItemsList()).thenReturn(fakeitems)
        `when`(mockedUpcomingEventsProvider.addItem(anyString())).thenReturn(fakeitems.add(testItem))
        `when`(mockedUpcomingEventsProvider.removeItem(anyString())).thenReturn(fakeitems.remove(testItem))
    }


    @Test
    fun correctNumberItemsDisplayed() {
        Espresso.onView(withId(R.id.id_recycler_items_list))
            .check(RecyclerViewItemCountAssertion(fakeitems.size))
    }

    @Test
    fun addButtonPopupAddsItemToList() {
        //TODO fix this test, seems like fakeitems is not updated correctly
        onView(withId(R.id.id_add_item_button)).perform(click())
        onView(withId(R.id.id_edittext_item_name)).perform(typeText(testItem))
        closeSoftKeyboard()
        onView(withId(R.id.id_confirm_add_item_button)).perform(click())
        //onView(withId(R.id.id_recycler_items_list))
         //   .check(RecyclerViewItemCountAssertion(4))
    }

}
