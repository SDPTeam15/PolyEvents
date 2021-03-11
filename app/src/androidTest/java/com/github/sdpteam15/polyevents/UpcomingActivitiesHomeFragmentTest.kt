package com.github.sdpteam15.polyevents


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.fragments.HomeFragment
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelperInterface
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime


@RunWith(MockitoJUnitRunner::class)
class UpcomingActivitiesHomeFragmentTest {

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
                "Kitchen", null, "1"
            )
        )

        activities.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !",
                LocalDateTime.of(2021, 3, 7, 15, 0),
                1.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "2"
            )
        )

        activities.add(
            Activity(
                "Concert",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                2.75F,
                "AcademiC DeCibel",
                "Concert Hall", null, "3"
            )
        )

        activities.add(
            Activity(
                "Cricket",
                "Outdoor activity !",
                LocalDateTime.of(2021, 3, 7, 18, 15),
                1.75F,
                "Cricket club",
                "Field", null, "4"
            )
        )

        mockedUpcomingActivitiesProvider = mock(ActivitiesQueryHelperInterface::class.java)
        `when`(mockedUpcomingActivitiesProvider.getUpcomingActivities()).thenReturn(activities)

        // Set the activities query helper in home fragment
        val homeFragment = MainActivity.fragments[R.id.ic_home] as HomeFragment
        homeFragment.activitiesQueryHelper = mockedUpcomingActivitiesProvider

        // Update the content to use the mock activities query helper
        runOnUiThread {
            // Stuff that updates the UI
            homeFragment.updateContent()
        }

        // Initially should be on home fragment but click on it if it gets modified
        Espresso.onView(withId(R.id.ic_home)).perform(click())
    }

    @Test
    fun correctNumberUpcomingActivitiesDisplayed() {
        Espresso.onView(withId(R.id.id_upcoming_activities_list)).check(
            matches(
                hasChildCount(
                    activities.size
                )
            )
        )
    }
}