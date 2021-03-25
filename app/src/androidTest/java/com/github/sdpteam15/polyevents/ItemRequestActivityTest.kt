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
import com.github.sdpteam15.polyevents.HelperTestFunction.getCurrentActivity
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.item_request.ItemRequestAdapter
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ItemRequestActivityTest {
    private lateinit var availableItems: MutableMap<String, Int>
    private lateinit var availableItemsList: List<Pair<String, Int>>
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
        Intents.init()
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
        val itemsSelected = mutableMapOf<String, Int>()

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
            getCurrentActivity<ItemRequestActivity>().mapSelectedItems, `is`(
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

        val correctMap = mutableMapOf<String, Int>()
        correctMap[availableItemsList[itemToSelect].first] = quantityToSelect

        assertThat(
            getCurrentActivity<ItemRequestActivity>().mapSelectedItems, `is`(
                correctMap
            )
        )
    }

    @Test
    fun settingTooHighQuantityMakeMaxRequest() {
        val quantityToSelect = 10000
        val itemToSelect = 1
        selectItemQuantity(itemToSelect, quantityToSelect.toString())

        val correctMap = mutableMapOf<String, Int>()
        correctMap[availableItemsList[itemToSelect].first] = availableItemsList[itemToSelect].second

        assertThat(
            getCurrentActivity<ItemRequestActivity>().mapSelectedItems, `is`(
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
            getCurrentActivity<ItemRequestActivity>().mapSelectedItems.size, `is`(
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