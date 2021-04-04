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
import com.github.sdpteam15.polyevents.database.FakeDatabase
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
                    eventId = "event1",
                    eventName = "Sushi demo",
                    description = "Super hungry activity !",
                    startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                    organizer = "The fish band",
                    zoneName = "Kitchen",
                    tags = mutableSetOf("sushi", "japan", "cooking")
                )
        )

        events.add(
                Event(
                    eventId = "event2",
                    eventName = "Aqua Poney",
                    description = "Super cool activity !" +
                            " With a super long description that essentially describes and explains" +
                            " the content of the activity we are speaking of.",
                    startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
                    organizer = "The Aqua Poney team",
                    zoneName = "Swimming pool"
                )
        )

        events.add(
            Event(
                eventId = "event3",
                eventName = "Concert",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
                organizer = "AcademiC DeCibel",
                zoneName = "Concert Hall"
            )
        )

        events.add(
            Event(
                eventId = "event4",
                eventName = "Cricket",
                description = "Outdoor activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 18, 15),
                organizer = "Cricket club",
                zoneName = "Field"
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