package com.github.sdpteam15.polyevents


import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.database.FakeDatabase
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.model.Event
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime


@RunWith(MockitoJUnitRunner::class)
class EventActivityTest {
    lateinit var events: ArrayList<Event>
    lateinit var mockedUpcomingEventsProvider: DatabaseInterface

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        events = ArrayList<Event>()
        events.add(
            Event(
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
                eventName = "Concert",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 21, 15),
                organizer = "AcademiC DeCibel",
                zoneName = "Concert Hall",
                tags = mutableSetOf("music", "live", "pogo")
            )
        )

        mockedUpcomingEventsProvider = mock(DatabaseInterface::class.java)
        currentDatabase = mockedUpcomingEventsProvider
        `when`(mockedUpcomingEventsProvider.getUpcomingEvents()).thenReturn(events)
        `when`(mockedUpcomingEventsProvider.getEventFromId("1")).thenReturn(events[0])
        `when`(mockedUpcomingEventsProvider.getEventFromId("2")).thenReturn(events[1])
        `when`(mockedUpcomingEventsProvider.getEventFromId("3")).thenReturn(events[2])
        // go to activities list fragment
        mainActivity = ActivityScenarioRule(MainActivity::class.java)
        Espresso.onView(withId(R.id.ic_list)).perform(click())
    }


    @Test
    fun correctNumberUpcomingEventsDisplayed() {
        Espresso.onView(withId(R.id.recycler_events_list))
            .check(RecyclerViewItemCountAssertion(mockedUpcomingEventsProvider.getUpcomingEvents().size))
    }

    @Test
    fun eventActivityOpensOnClick() {
        Intents.init()
        Espresso.onView(withId(R.id.recycler_events_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0,
                click()
            )
        )
        intended(hasComponent(EventActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun eventActivityShowsValuesFromGivenActivity() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventActivity::class.java
        )
        intent.putExtra(EXTRA_EVENT_ID, "1")
        val eventToTest = mockedUpcomingEventsProvider.getEventFromId("1") as Event
        val scenario = ActivityScenario.launch<EventActivity>(intent)
        Thread.sleep(1000)

        Espresso.onView(withId(R.id.txt_event_Name))
            .check(matches(withText(containsString(eventToTest.eventName))))

        Espresso.onView(withId(R.id.txt_event_description))
            .check(matches(withText(containsString(eventToTest.description))))


        Espresso.onView(withId(R.id.txt_event_organizer))
            .check(matches(withText(containsString(eventToTest.organizer))))

        Espresso.onView(withId(R.id.txt_event_zone))
            .check(matches(withText(containsString(eventToTest.zoneName))))

        Espresso.onView(withId(R.id.txt_event_date))
            .check(matches(withText(containsString(eventToTest.formattedStartTime()))))

        Espresso.onView(withId(R.id.txt_event_tags))
            .check(matches(withText(containsString(eventToTest.tags.joinToString { s -> s }))))

        scenario.close()
        //TODO check image is correct
    }
}

