package com.github.sdpteam15.polyevents


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.NUMBER_UPCOMING_EVENTS
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseEvent
import com.github.sdpteam15.polyevents.fragments.HomeFragment
import com.github.sdpteam15.polyevents.model.Event
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime


@RunWith(MockitoJUnitRunner::class)
class UpcomingEventsHomeFragmentTest {

    var events = ObservableList<Event>()

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        val eventsToAdd = ArrayList<Event>()

        eventsToAdd.add(
                Event(

                    eventName = "Sushi demo",
                    description = "Super hungry activity !",
                    startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                    organizer = "The fish band",
                    zoneName = "Kitchen",
                    tags = mutableSetOf("sushi", "japan", "cooking")
                )
        )

        eventsToAdd.add(
                Event(

                    eventName = "Aqua Poney",
                    description = "Super cool activity !" +
                            " With a super long description that essentially describes and explains" +
                            " the content of the activity we are speaking of.",
                    startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
                    organizer = "The Aqua Poney team",
                    zoneName = "Swimming pool"
                )
        )

        eventsToAdd.add(
            Event(

                eventName = "Concert",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
                organizer = "AcademiC DeCibel",
                zoneName = "Concert Hall"
            )
        )

        eventsToAdd.add(
            Event(

                eventName = "Cricket",
                description = "Outdoor activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 18, 15),
                organizer = "Cricket club",
                zoneName = "Field"
            )
        )


        // Set the activities query helper in home fragment
        val homeFragment = MainActivity.fragments[R.id.ic_home] as HomeFragment
        currentDatabase = FakeDatabase
        FakeDatabase.userToNull = true
        FakeDatabaseEvent.events.clear()
        for (event in eventsToAdd){
            currentDatabase.eventDatabase!!.createEvent(event)
        }
        currentDatabase.eventDatabase!!.getListEvent(null, null, events)

        // Update the content to use the mock activities query helper
        runOnUiThread {
            // Stuff that updates the UI
            homeFragment.updateContent()
        }

        // Initially should be on home fragment but click on it if it gets modified
        Espresso.onView(withId(R.id.ic_home)).perform(click())
    }

    @After
    fun tearDown(){
        FakeDatabase.userToNull = false
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun correctNumberUpcomingActivitiesDisplayed() {
        Espresso.onView(withId(R.id.id_upcoming_events_list)).check(
            matches(
                hasChildCount(
                    NUMBER_UPCOMING_EVENTS
                )
            )
        )
    }
}