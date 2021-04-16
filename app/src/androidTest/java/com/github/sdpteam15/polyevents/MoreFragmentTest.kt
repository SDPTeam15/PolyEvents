package com.github.sdpteam15.polyevents


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseItem
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(AndroidJUnit4::class)
class MoreFragmentTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }


    @Test
    fun itemRequestActivityOpensOnClick() {
        onView(withId(R.id.ic_more)).perform(click())

        onView(withId(R.id.id_request_button)).perform(click())
        intended(hasComponent(ItemRequestActivity::class.java.name))
    }

    @Test
    fun itemsAdminActivity() {
        var availableItems: MutableMap<Item, Int> = mutableMapOf()
        availableItems[Item(null, "Chocolat", ItemType.OTHER)] = 30
        availableItems[Item(null, "Kiwis", ItemType.OTHER)] = 10
        availableItems[Item(null, "230V Plugs", ItemType.PLUG)] = 30
        availableItems[Item(null, "Fridge (large)", ItemType.OTHER)] = 5
        availableItems[Item(null, "Cord rewinder (15m)", ItemType.PLUG)] = 30
        availableItems[Item(null, "Cord rewinder (50m)", ItemType.PLUG)] = 10
        availableItems[Item(null, "Cord rewinder (25m)", ItemType.PLUG)] = 20

        Database.currentDatabase = FakeDatabase

        FakeDatabaseItem.items.clear()
        for ((item, count) in availableItems) {
            Database.currentDatabase.itemDatabase!!.createItem(item, count)
        }

        onView(withId(R.id.ic_more)).perform(click())
        onView(withId(R.id.btn_admin_items_list)).perform(click())
        intended(hasComponent(ItemsAdminActivity::class.java.name))
    }

}