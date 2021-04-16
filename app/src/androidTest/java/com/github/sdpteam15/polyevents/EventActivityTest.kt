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
import com.github.sdpteam15.polyevents.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.FakeDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.model.Event
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime


@RunWith(MockitoJUnitRunner::class)
class EventActivityTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {

        FakeDatabase.events.clear()
        FakeDatabase.createEvent(
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
        FakeDatabase.createEvent(
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
        FakeDatabase.createEvent(
            Event(
                eventName = "Concert",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 21, 15),
                organizer = "AcademiC DeCibel",
                zoneName = "Concert Hall",
                tags = mutableSetOf("music", "live", "pogo")
            )
        )
        currentDatabase = FakeDatabase
        Intents.init()
        // go to activities list fragment
        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Thread.sleep(1000)

    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun correctNumberUpcomingEventsDisplayed() {
        Espresso.onView(withId(R.id.recycler_events_list))
            .check(RecyclerViewItemCountAssertion(FakeDatabase.events.size))
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
        val events = ObservableList<Event>()
        currentDatabase.getListEvent(null, 1, events)

        val eventToTest = events[0]
        intent.putExtra(EXTRA_EVENT_ID, eventToTest.eventId!!)
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

