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
import com.github.sdpteam15.polyevents.model.Event
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
                "The fish band",
                "Kitchen",
                "Super hungry activity !",
                null,
                LocalDateTime.of(2021, 3, 7, 12, 15),
                LocalDateTime.of(2021, 3, 7, 13, 15),
                mutableListOf(),
                mutableSetOf(),
                 "1"
            )
        )

        events.add(
            Event(
                "Aqua Poney",
                "The Aqua Poney team",
                "Swimming pool",
                "Super cool activity !",
                null,
                LocalDateTime.of(2021, 3, 7, 15, 0),
                LocalDateTime.of(2021, 3, 7, 16, 30),
                mutableListOf(),
                mutableSetOf(),
                "2"
            )
        )

        events.add(
            Event(
                "Concert",
                "Super noisy activity !",
                "AcademiC DeCibel",
                "Concert Hall",
                null,
                LocalDateTime.of(2021, 3, 7, 17, 15),
                LocalDateTime.of(2021, 3, 7, 19, 0),
                mutableListOf(),
                mutableSetOf(),
                 "3"
            )
        )

        events.add(
            Event(
                "Cricket",
                "Outdoor activity !",
                "Cricket club",
                "Field",
                null,
                LocalDateTime.of(2021, 3, 7, 18, 15),
                LocalDateTime.of(2021, 3, 7, 20, 0),
                mutableListOf(),
                mutableSetOf(),
                "4"
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