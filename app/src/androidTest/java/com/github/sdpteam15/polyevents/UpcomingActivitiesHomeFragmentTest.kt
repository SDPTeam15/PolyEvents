package com.github.sdpteam15.polyevents


import android.widget.LinearLayout
import androidx.core.view.children
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelperInterface
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.*
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import androidx.cardview.widget.CardView as CardView

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
                "Kitchen", null)
        )

        activities.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !",
                LocalDateTime.of(2021, 3, 7, 15, 0),
                1.5F,
                "The Aqua Poney team",
                "Swimming pool", null)
        )

        activities.add(
            Activity(
                "Concert",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 21, 15),
                2.75F,
                "AcademiC DeCibel",
                "Concert Hall", null))

        mockedUpcomingActivitiesProvider = mock(ActivitiesQueryHelperInterface::class.java)
        `when`(mockedUpcomingActivitiesProvider.getUpcomingActivities()).thenReturn(activities)

        // Initially, we are on the home page, click on it to be sure
        Espresso.onView(withId(R.id.ic_home)).perform(click())
    }

    @Test
    fun correctNumberUpcomingActivitiesDisplayed() {
        Espresso.onView(withId(R.id.id_upcoming_activities_list)).check(matches(hasChildCount(activities.size)))
    }
}