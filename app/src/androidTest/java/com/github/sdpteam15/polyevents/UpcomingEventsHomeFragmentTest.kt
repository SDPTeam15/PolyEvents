package com.github.sdpteam15.polyevents


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.fragments.HomeFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime


@RunWith(MockitoJUnitRunner::class)
class UpcomingEventsHomeFragmentTest {

    lateinit var events: ArrayList<Event>
    lateinit var mockedUpcomingActivitiesProvider: DatabaseInterface

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        events = ArrayList<Event>()

        events.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", "1"
            )
        )

        events.add(
            Event(
                "Aqua Poney",
                "Super cool activity !",
                LocalDateTime.of(2021, 3, 7, 15, 0),
                1.5F,
                "The Aqua Poney team",
                "Swimming pool", "2"
            )
        )

        events.add(
            Event(
                "Concert",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                2.75F,
                "AcademiC DeCibel",
                "Concert Hall", "3"
            )
        )

        events.add(
            Event(
                "Cricket",
                "Outdoor activity !",
                LocalDateTime.of(2021, 3, 7, 18, 15),
                1.75F,
                "Cricket club",
                "Field", "4"
            )
        )

        mockedUpcomingActivitiesProvider = mock(DatabaseInterface::class.java)
        `when`(mockedUpcomingActivitiesProvider.getUpcomingEvents()).thenReturn(events)

        // Set the activities query helper in home fragment
        val homeFragment = MainActivity.fragments[R.id.ic_home] as HomeFragment
        currentDatabase = mockedUpcomingActivitiesProvider

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
        Espresso.onView(withId(R.id.id_upcoming_events_list)).check(
            matches(
                hasChildCount(
                    events.size
                )
            )
        )
    }
}