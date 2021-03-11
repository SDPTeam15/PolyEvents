package com.github.sdpteam15.polyevents


import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.activity.ActivityItemAdapter
import com.github.sdpteam15.polyevents.fragments.EXTRA_ACTIVITY
import com.github.sdpteam15.polyevents.fragments.ListFragment
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelperInterface
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime


@RunWith(MockitoJUnitRunner::class)
class ActivityActivityTest {
    lateinit var activities: ArrayList<Activity>
    lateinit var mockedUpcomingActivitiesProvider: ActivitiesQueryHelperInterface

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        activities = ArrayList<Activity>()
        activities.add(
            Activity(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen",
                null, "1", mutableSetOf("cooking", "sushi", "miam")
            )
        )

        activities.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !",
                LocalDateTime.of(2021, 3, 7, 15, 0),
                1.5F,
                "The Aqua Poney team",
                "Swimming pool",
                null, "2", mutableSetOf("horse", "swimming", "pony")
            )
        )

        activities.add(
            Activity(
                "Concert",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 21, 15),
                2.75F,
                "AcademiC DeCibel",
                "Concert Hall",
                null, "3", mutableSetOf("music", "live", "pogo")
            )
        )

        mockedUpcomingActivitiesProvider = mock(ActivitiesQueryHelperInterface::class.java)
        `when`(mockedUpcomingActivitiesProvider.getUpcomingActivities()).thenReturn(activities)
        `when`(mockedUpcomingActivitiesProvider.getActivityFromId("1")).thenReturn(activities[0])
        `when`(mockedUpcomingActivitiesProvider.getActivityFromId("2")).thenReturn(activities[1])
        `when`(mockedUpcomingActivitiesProvider.getActivityFromId("3")).thenReturn(activities[2])
        // go to activities list fragment
        mainActivity = ActivityScenarioRule(MainActivity::class.java)
        Espresso.onView(withId(R.id.ic_list)).perform(click())
        // Set the activities query helper in list fragment
        val listFragment = MainActivity.fragments[R.id.ic_list] as ListFragment
        listFragment.currentQueryHelper = mockedUpcomingActivitiesProvider
    }


    @Test
    fun correctNumberUpcomingActivitiesDisplayed() {
        Espresso.onView(withId(R.id.recycler_activites_list))
            .check(RecyclerViewItemCountAssertion(mockedUpcomingActivitiesProvider.getUpcomingActivities().size));
    }

    @Test
    fun activityActivityOpensOnClick() {
        Intents.init()
        Espresso.onView(withId(R.id.recycler_activites_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ActivityItemAdapter.ItemViewHolder>(
                0,
                click()
            )
        )
        intended(hasComponent(ActivityActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun activityActivityShowsValuesFromGivenActivity() {
        val indexToTest: Int = 0
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ActivityActivity::class.java
        )
        intent.putExtra(EXTRA_ACTIVITY, "1")
        val activityToTest = mockedUpcomingActivitiesProvider.getActivityFromId("1")
        val scenario = ActivityScenario.launch<ActivityActivity>(intent)
        scenario.onActivity { activity ->
            activity.currentQueryHelper = mockedUpcomingActivitiesProvider
        }
        Thread.sleep(1000)
        Espresso.onView(withId(R.id.txt_activity_Name)).check(
            matches(
                withText(
                    containsString(
                        activityToTest.name
                    )
                )
            )
        )
        Espresso.onView(withId(R.id.txt_activity_description)).check(
            matches(
                withText(
                    containsString(
                        activityToTest.description
                    )
                )
            )
        )
        Espresso.onView(withId(R.id.txt_activity_organizer)).check(
            matches(
                withText(
                    containsString(
                        activityToTest.organizer
                    )
                )
            )
        )
        Espresso.onView(withId(R.id.txt_activity_zone)).check(
            matches(
                withText(
                    containsString(
                        activityToTest.zone
                    )
                )
            )
        )
        Espresso.onView(withId(R.id.txt_activity_date)).check(
            matches(
                withText(
                    containsString(
                        activityToTest.getTime()
                    )
                )
            )
        )
        Espresso.onView(withId(R.id.txt_activity_tags)).check(
            matches(
                withText(
                    containsString(
                        activityToTest.tags.joinToString { s -> s })
                )
            )
        )
        scenario.close()
        //TODO check image is correct

    }
}

class RecyclerViewItemCountAssertion(expectedCount: Int) : ViewAssertion {
    private val matcher: Matcher<Int> = `is`(expectedCount)

    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter!!.itemCount, matcher)
    }
}