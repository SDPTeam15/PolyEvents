package com.github.sdpteam15.polyevents


import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.item_request.ItemRequestAdapter
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ItemRequestActivityTest {
    lateinit var availableItems: MutableMap<String, Int>
    lateinit var availableItemsList: List<Pair<String, Int>>
    lateinit var mockedAvailableItemsProvider: DatabaseInterface

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    // Source : https://stackoverflow.com/questions/28476507/using-espresso-to-click-view-inside-recyclerview-item
    fun checkViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a checkbox view child with specified id."

        override fun perform(uiController: UiController, view: View) = click().perform(uiController, view.findViewById<View>(viewId))
    }

    fun setQuantityField(viewId: Int, value: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Set the value of the quantity field with specified id."

        override fun perform(uiController: UiController, view: View) = view.findViewById<EditText>(viewId).setText(value.toString())
    }

    // Source : https://stackoverflow.com/questions/38737127/espresso-how-to-get-current-activity-to-test-fragments/58684943#58684943
    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync { run { currentActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
            Stage.RESUMED).elementAtOrNull(0) } }
        return currentActivity
    }

    @Before
    fun setup() {
        availableItems = mutableMapOf<String, Int>()
        availableItems["Bananas"] = 30
        availableItems["Kiwis"] = 10
        availableItems["230V plugs"] = 30
        availableItems["Fridge (large)"] = 5
        availableItems["Cord rewinder (15m)"] = 30
        availableItems["Cord rewinder (50m)"] = 10
        availableItems["Cord rewinder (25m)"] = 20

        availableItemsList = availableItems.toList()


        // TODO : replace by the db interface call
        mockedAvailableItemsProvider = Mockito.mock(DatabaseInterface::class.java)
        Database.currentDatabase = mockedAvailableItemsProvider
        `when`(mockedAvailableItemsProvider.getAvailableItems()).thenReturn(availableItems)

        // go to activities more fragment
        mainActivity = ActivityScenarioRule(MainActivity::class.java)
        onView(withId(R.id.ic_more)).perform(click())

        // Go to items request activity
        onView(withId(R.id.id_request_button)).perform(click())
    }

    @Test
    fun correctNumberAvailableItemsDisplayed() {
        // TODO : replace the mock
        onView(withId(R.id.id_recycler_items_request))
            .check(RecyclerViewItemCountAssertion(availableItems.size))
    }

    @Test
    fun quantityAvailableItemsIsCorrect() {
        /*for(i in 0 until availableItems.size) {
            onData(allOf(`is`(instanceOf(String::class.java)), hasEntry(equalTo("STR"),
                `is`("item: 50"))))
                .onChildView(withId(R.id.id_item_quantity))
                .check(
                    matches(
                        withText(
                            CoreMatchers.containsString(
                                availableItemsList[i].second.toString()
                            )
                        )
                    )
                )

        }
         */
    }

    @Test
    fun itemsRequestListIsCorrect() {
        val itemsToSelect = arrayOf(0, 1, 3)
        val quantityToSelect = arrayOf(35, 3, 5, 2, 40, 4, 20)
        val correctQuantityAfterSelection = arrayOf(30, 3, 5, 2, 30, 4, 20)
        val itemsSelected = mutableMapOf<String, Int>()

        for (i in itemsToSelect) {
            onView(withId(R.id.id_recycler_items_request)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdapter.ItemViewHolder>(
                    i,
                    setQuantityField(R.id.id_item_quantity, quantityToSelect[i])
                )
            )

            onView(withId(R.id.id_recycler_items_request)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdapter.ItemViewHolder>(
                    i,
                    checkViewChild(R.id.id_item_requested)
                )
            )
            itemsSelected[availableItemsList[i].first] = correctQuantityAfterSelection[i]
        }

        assertThat((getCurrentActivity() as ItemRequestActivity).mapSelectedItems, `is`(itemsSelected))
    }
}