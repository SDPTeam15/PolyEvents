package com.github.sdpteam15.polyevents

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseEvent
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.model.Event
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
class EventListFragmentTest {
    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {

        FakeDatabaseEvent.events.clear()
        FakeDatabase.eventDatabase!!.createEvent(
            Event(
                eventName = "Sushi demo",
                description = "Super hungry activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                endTime = LocalDateTime.of(2021, 3, 7, 13, 0, 0),
                organizer = "The fish band",
                zoneName = "Kitchen",
                tags = mutableSetOf("sushi", "japan", "cooking")
            )
        )
        FakeDatabase.eventDatabase!!.createEvent(
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
        FakeDatabase.eventDatabase!!.createEvent(
            Event(
                eventName = "Concert",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 21, 15),
                organizer = "AcademiC DeCibel",
                zoneName = "Concert Hall",
                tags = mutableSetOf("music", "live", "pogo")
            )
        )

        Database.currentDatabase = FakeDatabase
        Intents.init()
        // go to activities list fragment
        Espresso.onView(ViewMatchers.withId(R.id.ic_list)).perform(ViewActions.click())
        Thread.sleep(1000)
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
        Intents.release()
    }

    @Test
    fun correctNumberUpcomingEventsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list))
            .check(RecyclerViewItemCountAssertion(FakeDatabaseEvent.events.size))
    }

    @Test
    fun eventActivityOpensOnClick() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0,
                ViewActions.click()
            )
        )
        Intents.intended(IntentMatchers.hasComponent(EventActivity::class.java.name))
    }

    @Test
    fun eventActivityShowsValuesFromGivenActivity() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventActivity::class.java
        )
        val events = ObservableList<Event>()
        Database.currentDatabase.eventDatabase!!.getEvents(null, 1, events)

        val eventToTest = events[0]
        intent.putExtra(EXTRA_EVENT_ID, eventToTest.eventId!!)
        val scenario = ActivityScenario.launch<EventActivity>(intent)
        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.txt_event_Name))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            eventToTest.eventName
                        )
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.txt_event_description))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            eventToTest.description
                        )
                    )
                )
            )


        Espresso.onView(ViewMatchers.withId(R.id.txt_event_organizer))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            eventToTest.organizer
                        )
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.txt_event_zone))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            eventToTest.zoneName
                        )
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.txt_event_date))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            eventToTest.formattedStartTime()
                        )
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.txt_event_tags))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        CoreMatchers.containsString(
                            eventToTest.tags.joinToString { s -> s })
                    )
                )
            )

        scenario.close()
        //TODO check image is correct
    }
}