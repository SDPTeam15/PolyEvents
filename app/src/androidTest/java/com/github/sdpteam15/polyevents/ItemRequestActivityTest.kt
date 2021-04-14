package com.github.sdpteam15.polyevents


import android.app.Activity
import android.view.View
import android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
import android.view.WindowManager.LayoutParams.TYPE_TOAST
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.adapter.ItemRequestAdapter
import com.github.sdpteam15.polyevents.database.FakeDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ItemRequestActivityTest {
    private lateinit var availableItems: MutableMap<Item, Int>
    private var availableItemsList = ObservableList<Pair<Item, Int>>()
    private lateinit var mockedAvailableItemsProvider: DatabaseInterface

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    private fun setQuantityField(viewId: Int, value: String) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Set the value of the quantity field with specified id."

        override fun perform(uiController: UiController, view: View) = view.findViewById<EditText>(
            viewId
        ).setText(value)
    }

    // Source : https://stackoverflow.com/questions/38737127/espresso-how-to-get-current-activity-to-test-fragments/58684943#58684943
    private fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync {
            run {
                currentActivity =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED
                    ).elementAtOrNull(0)
            }
        }
        return currentActivity
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
        Intents.release()
    }

    @Before
    fun setup() {
        availableItems = mutableMapOf()
        availableItems[Item(null,"Bananas", ItemType.OTHER)] = 30
        availableItems[Item(null,"Kiwis", ItemType.OTHER)] = 10
        availableItems[Item(null,"230V Plugs", ItemType.PLUG)] = 30
        availableItems[Item(null,"Fridge (large)", ItemType.OTHER)] = 5
        availableItems[Item(null,"Cord rewinder (15m)", ItemType.PLUG)] = 30
        availableItems[Item(null,"Cord rewinder (50m)",ItemType.PLUG)] = 10
        availableItems[Item(null,"Cord rewinder (25m)",ItemType.PLUG)] = 20



        // TODO : replace by the db interface call
        Database.currentDatabase = FakeDatabase
        FakeDatabase.items.clear()
        for ((item,count) in availableItems){
            Database.currentDatabase.createItem(item,count)
        }
        Database.currentDatabase.getItemsList(availableItemsList)

        // go to activities more fragment
        mainActivity = ActivityScenarioRule(MainActivity::class.java)
        onView(withId(R.id.ic_more)).perform(click())

        // Go to items request activity
        onView(withId(R.id.id_request_button)).perform(click())
        Intents.init()
        Thread.sleep(1000)
    }

    @Test
    fun correctNumberAvailableItemsDisplayed() {
        onView(withId(R.id.id_recycler_items_request))
            .check(RecyclerViewItemCountAssertion(availableItems.size))
    }

    @Test
    fun itemsRequestListIsCorrect() {
        val itemsToSelect = arrayOf(0, 1, 3)
        val quantityToSelect = arrayOf(30, 3, 5, 2, 40, 4, 20)
        val correctQuantityAfterSelection = arrayOf(30, 3, 5, 2, 30, 4, 20)
        val itemsSelected = mutableMapOf<Item, Int>()

        for (i in itemsToSelect) {
            // Set the quantity wanted for each item
            onView(withId(R.id.id_recycler_items_request)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemRequestAdapter.ItemViewHolder>(
                    i,
                    setQuantityField(R.id.id_item_quantity, quantityToSelect[i].toString())
                )
            )

            itemsSelected[availableItemsList[i].first] = correctQuantityAfterSelection[i]
        }

        assertThat(
            (getCurrentActivity() as ItemRequestActivity).mapSelectedItems, `is`(
                itemsSelected
            )
        )
    }


    @Test
    fun sendingNonEmptyItemRequestGoBackToMainActivity() {
        selectItemQuantity(0, "1")
        onView(withId(R.id.id_button_make_request)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    /*
    @Test
    fun sendingEmptyRequestDisplayToast() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()

        onView(withId(R.id.id_button_make_request)).perform(click())
        //onView(withText(containsString(context.getString(R.string.item_request_empty_text)))).inRoot(ToastMatcher()).check(matches(isDisplayed()))
        onView(withText(containsString(context.getString(R.string.item_request_empty_text)))).inRoot(withDecorView(not(
            getActivity(context)?.getWindow()?.getDecorView()
        ))).check(matches(isDisplayed()))
    }

     */

    @Test
    fun settingNegativeQuantityMakeEmptyRequest() {
        val quantityToSelect = 1
        val itemToSelect = 1
        selectItemQuantity(0, "-1")
        selectItemQuantity(itemToSelect, quantityToSelect.toString())

        val correctMap = mutableMapOf<Item, Int>()
        correctMap[availableItemsList[itemToSelect].first] = quantityToSelect

        assertThat(
            (getCurrentActivity() as ItemRequestActivity).mapSelectedItems, `is`(
                correctMap
            )
        )
    }

    @Test
    fun settingTooHighQuantityMakeMaxRequest() {
        val quantityToSelect = 10000
        val itemToSelect = 1
        selectItemQuantity(itemToSelect, quantityToSelect.toString())

        val correctMap = mutableMapOf<Item, Int>()
        correctMap[availableItemsList[itemToSelect].first] = availableItemsList[itemToSelect].second

        assertThat(
            (getCurrentActivity() as ItemRequestActivity).mapSelectedItems, `is`(
                correctMap
            )
        )
    }

    // Somewhat hijacked test
    @Test
    fun settingEmptyStringForQuantityHasNoEffect() {
        // Setting empty string as a quantity has no effect on anything
        selectItemQuantity(0, "")

        assertThat(
            (getCurrentActivity() as ItemRequestActivity).mapSelectedItems.size, `is`(
                0
            )
        )
    }
}

// Source : https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso
class ToastMatcher : TypeSafeMatcher<Root?>() {

    private var currentFailures: Int = 0

    override fun describeTo(description: Description) {
        description.appendText("no toast found after")
    }

    override fun matchesSafely(item: Root?): Boolean {
        val type: Int? = item?.windowLayoutParams?.get()?.type

        if (TYPE_TOAST == type || TYPE_APPLICATION_OVERLAY == type) {
            val windowToken = item.decorView.windowToken
            val appToken = item.decorView.applicationWindowToken

            if (windowToken == appToken) {
                return true
            }
        }
        return ++currentFailures >= 5
    }
}