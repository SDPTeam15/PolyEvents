package com.github.sdpteam15.polyevents


import android.content.Intent
import android.view.View
import android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
import android.view.WindowManager.LayoutParams.TYPE_TOAST
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sdpteam15.polyevents.HelperTestFunction.getCurrentActivity
import com.github.sdpteam15.polyevents.adapter.ItemRequestAdapter
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseItem
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseMaterialRequest
import com.github.sdpteam15.polyevents.model.Item
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.regex.Pattern.matches


@RunWith(MockitoJUnitRunner::class)
class ItemRequestActivityTest {
    private lateinit var availableItems: MutableMap<Item, Int>
    private var availableItemsList = ObservableList<Pair<Item, Int>>()
    private lateinit var mockedAvailableItemsProvider: DatabaseInterface
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
        availableItems = mutableMapOf()
        availableItems[Item(null, "Bananas", "Fruit")] = 30
        availableItems[Item(null, "Kiwis", "Fruit")] = 10
        availableItems[Item(null, "230V Plugs", "Plug")] = 30
        availableItems[Item(null, "Fridge (large)", "Fridge")] = 5
        availableItems[Item(null, "Cord rewinder (15m)", "Plug")] = 30
        availableItems[Item(null, "Cord rewinder (50m)", "Plug")] = 10
        availableItems[Item(null, "Cord rewinder (25m)", "Plug")] = 20

        val types = mutableSetOf<String>()
        nbItemTypes = availableItems.count { types.add(it.key.itemType) }
        // TODO : replace by the db interface call
        Database.currentDatabase = FakeDatabase
        FakeDatabaseItem.items.clear()
        for ((item, count) in availableItems) {
            Database.currentDatabase.itemDatabase!!.createItem(item, count)
        }
        Database.currentDatabase.itemDatabase!!.getItemsList(availableItemsList)

        // go to activities more fragment
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ItemRequestActivity::class.java)
        itemsAdminActivity = ActivityScenario.launch(intent)
        //onView(withId(R.id.ic_more)).perform(click())

        // Go to items request activity
        //onView(withId(R.id.id_request_button)).perform(click())
        //Intents.init()
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
        assert(FakeDatabaseMaterialRequest.requests.any { it.items.any { it2 -> it2.key == availableItemsList[0].first.itemId } })
    }

/*
    @Test
    fun sendingEmptyRequestDisplayToast() {
        val context = InstrumentationRegistry.getInstrumentation().getTargetContext()

        onView(withId(R.id.id_button_make_request)).perform(click())
        //onView(withText(containsString(context.getString(R.string.item_request_empty_text)))).inRoot(ToastMatcher()).check(matches(isDisplayed()))
        onView(withText(containsString(context.getString(R.string.item_request_empty_text)))).inRoot(
            withDecorView(
                not(
                    getActivity(context)?.getWindow()?.getDecorView()
                )
            )
        ).check(matches(isDisplayed()))
    }
*/

    @Test
    fun settingNegativeQuantityMakeEmptyRequest() {
        openAllCategories()
        val quantityToSelect = 1
        val itemToSelect = 1
        selectItemQuantity(1, "-1")
        selectItemQuantity(itemToSelect, quantityToSelect.toString())

        val correctMap = mutableMapOf<Item, Int>()
        correctMap[availableItemsList[itemToSelect-1].first] = quantityToSelect

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
        correctMap[availableItemsList[itemToSelect-1].first] = availableItemsList[itemToSelect].second

        for (k in correctMap.keys) {
            assert(
                getCurrentActivity<ItemRequestActivity>().mapSelectedItems.contains(k)
            )
        }
    }

    // Somewhat hijacked test
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