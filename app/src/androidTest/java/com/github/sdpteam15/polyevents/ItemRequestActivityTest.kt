package com.github.sdpteam15.polyevents


import android.app.Activity
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.github.sdpteam15.polyevents.activity.ActivityItemAdapter
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelperInterface
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ItemRequestActivityTest {
    lateinit var availableItems: ArrayList<String>
    lateinit var mockedUpcomingActivitiesProvider: ActivitiesQueryHelperInterface

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    // Source : https://stackoverflow.com/questions/28476507/using-espresso-to-click-view-inside-recyclerview-item
    fun checkViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a checkbox view child with specified id."

        override fun perform(uiController: UiController, view: View) = click().perform(uiController, view.findViewById<View>(viewId))
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
        availableItems = arrayListOf(
            "Bananas",
            "Kiwis",
            "Boiler plates",
            "230V plugs",
            "Cord rewinder (100m)",
            "Cord rewinder (20m)",
            "Cookware"
            )

        // TODO : replace by the db interface call
        /*
        mockedUpcomingActivitiesProvider = mock(ActivitiesQueryHelperInterface::class.java)
        `when`(mockedUpcomingActivitiesProvider.getUpcomingActivities()).thenReturn(activities)
        `when`(mockedUpcomingActivitiesProvider.getActivityFromId("1")).thenReturn(activities[0])
        `when`(mockedUpcomingActivitiesProvider.getActivityFromId("2")).thenReturn(activities[1])
        `when`(mockedUpcomingActivitiesProvider.getActivityFromId("3")).thenReturn(activities[2])
         */

        // go to activities more fragment
        mainActivity = ActivityScenarioRule(MainActivity::class.java)
        onView(withId(R.id.ic_more)).perform(click())

        /*
        // Set the activities query helper in list fragment
        val listFragment = MainActivity.fragments[R.id.ic_list] as ListFragment
        listFragment.currentQueryHelper = mockedUpcomingActivitiesProvider
        */

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
    fun itemsRequestListIsCorrect() {
        val itemsToSelect = arrayOf(0, 1, 3)
        val itemsSelected = arrayListOf<String>()

        for (i in itemsToSelect) {
            onView(withId(R.id.id_recycler_items_request)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ActivityItemAdapter.ItemViewHolder>(
                    i,
                    checkViewChild(R.id.id_item_requested)
                )
            )

            itemsSelected.add(availableItems[i])
        }

        // TODO : fails because mock is not set in ItemRequestActivity
        assertThat(itemsSelected, equalTo((getCurrentActivity() as ItemRequestActivity).mapSelectedItems))
    }
}