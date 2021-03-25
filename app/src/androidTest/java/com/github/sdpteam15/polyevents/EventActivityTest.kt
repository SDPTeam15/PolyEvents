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
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.event.EventItemAdapter
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
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
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen",
                "1", mutableSetOf("cooking", "sushi", "miam")
            )
        )

        events.add(
            Event(
                "Aqua Poney",
                "Super cool activity !",
                LocalDateTime.of(2021, 3, 7, 15, 0),
                1.5F,
                "The Aqua Poney team",
                "Swimming pool",
                "2", mutableSetOf("horse", "swimming", "pony")
            )
        )

        events.add(
            Event(
                "Concert",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 21, 15),
                2.75F,
                "AcademiC DeCibel",
                "Concert Hall",
                "3", mutableSetOf("music", "live", "pogo")
            )
        )
        mockedUpcomingEventsProvider = mock(DatabaseInterface::class.java)
        currentDatabase = mockedUpcomingEventsProvider
        `when`(mockedUpcomingEventsProvider.getUpcomingEvents()).thenReturn(events)
        `when`(mockedUpcomingEventsProvider.getEventFromId("1")).thenReturn(events[0])
        `when`(mockedUpcomingEventsProvider.getEventFromId("2")).thenReturn(events[1])
        `when`(mockedUpcomingEventsProvider.getEventFromId("3")).thenReturn(events[2])
        // go to activities list fragment
        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }


    @Test
    fun correctNumberUpcomingEventsDisplayed() {
        Espresso.onView(withId(R.id.recycler_events_list))
            .check(RecyclerViewItemCountAssertion(mockedUpcomingEventsProvider.getUpcomingEvents().size));
    }

    @Test
    fun eventActivityOpensOnClick() {
        Espresso.onView(withId(R.id.recycler_events_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0,
                click()
            )
        )
        intended(hasComponent(EventActivity::class.java.name))
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
            .check(matches(withText(containsString(eventToTest.name))))

        Espresso.onView(withId(R.id.txt_event_description))
            .check(matches(withText(containsString(eventToTest.description))))


        Espresso.onView(withId(R.id.txt_event_organizer))
            .check(matches(withText(containsString(eventToTest.organizer))))

        Espresso.onView(withId(R.id.txt_event_zone))
            .check(matches(withText(containsString(eventToTest.zone))))

        Espresso.onView(withId(R.id.txt_event_date))
            .check(matches(withText(containsString(eventToTest.getTime()))))

        Espresso.onView(withId(R.id.txt_event_tags))
            .check(matches(withText(containsString(eventToTest.tags.joinToString { s -> s }))))

        scenario.close()
        //TODO check image is correct
    }
}

