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
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.exceptions.MaxAttendeesException
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseEvent
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.UserEntity
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
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

        val limitedEvent = Event(
            eventName = "limited Event only",
            description = "Super noisy activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 21, 15),
            organizer = "AcademiC DeCibel",
            zoneName = "Concert Hall",
            tags = mutableSetOf("music", "live", "pogo")
        )
        limitedEvent.makeLimitedEvent(3)

        FakeDatabase.eventDatabase!!.createEvent(limitedEvent)

        currentDatabase = FakeDatabase
        Intents.init()
        // go to activities list fragment
        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Thread.sleep(1000)
    }

    @After
    fun teardown() {
        currentDatabase = FirestoreDatabaseProvider
        Intents.release()
    }

    @Test
    fun correctNumberUpcomingEventsDisplayed() {
        Espresso.onView(withId(R.id.recycler_events_list))
            .check(RecyclerViewItemCountAssertion(FakeDatabaseEvent.events.size))
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
        currentDatabase.eventDatabase!!.getEvents(null, 1, events)

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

    @Test
    fun testEventSubscription() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventActivity::class.java
        )

        val events = ObservableList<Event>()
        currentDatabase.eventDatabase!!.getEvents(null, 1, events)

        // Get the limited event and set its max number of participants
        val limitedEventToTest = events.first { it.isLimitedEvent() }
        limitedEventToTest.makeLimitedEvent(3)
        currentDatabase.eventDatabase!!.updateEvents(limitedEventToTest)

        intent.putExtra(EXTRA_EVENT_ID, limitedEventToTest.eventId!!)
        val scenario = ActivityScenario.launch<EventActivity>(intent)
        Thread.sleep(1000)

        Espresso.onView(withId(R.id.txt_event_Name))
            .check(matches(withText(containsString(limitedEventToTest.eventName))))

        Espresso.onView(withId(R.id.txt_event_description))
            .check(matches(withText(containsString(limitedEventToTest.description))))


        Espresso.onView(withId(R.id.txt_event_organizer))
            .check(matches(withText(containsString(limitedEventToTest.organizer))))

        Espresso.onView(withId(R.id.txt_event_zone))
            .check(matches(withText(containsString(limitedEventToTest.zoneName))))

        Espresso.onView(withId(R.id.txt_event_date))
            .check(matches(withText(containsString(limitedEventToTest.formattedStartTime()))))

        Espresso.onView(withId(R.id.txt_event_tags))
            .check(matches(withText(containsString(limitedEventToTest.tags.joinToString { s -> s }))))

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        clickOn(R.id.button_subscribe_event)

        val updatedEvent = Observable<Event>()
        FakeDatabaseEvent.getEventFromId(limitedEventToTest.eventId!!, updatedEvent)

        assert(limitedEventToTest.getParticipants().contains(currentDatabase.currentUser!!.uid))
        assertDisplayed(R.id.button_subscribe_event, R.string.event_unsubscribe)

        // Unsubscribe
        clickOn(R.id.button_subscribe_event)
        FakeDatabaseEvent.getEventFromId(limitedEventToTest.eventId!!, updatedEvent)

        assert(!limitedEventToTest.getParticipants().contains(currentDatabase.currentUser!!.uid))
        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        scenario.close()
    }

    fun testEventSubscriptionForFullEvent() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventActivity::class.java
        )

        val events = ObservableList<Event>()
        currentDatabase.eventDatabase!!.getEvents(null, 1, events)

        // Set a max
        val fullLimitedEventTest = events.first { it.isLimitedEvent() }
        fullLimitedEventTest.makeLimitedEvent(0)
        currentDatabase.eventDatabase!!.updateEvents(fullLimitedEventTest)

        intent.putExtra(EXTRA_EVENT_ID, fullLimitedEventTest.eventId!!)
        val scenario = ActivityScenario.launch<EventActivity>(intent)
        Thread.sleep(1000)

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        clickOn(R.id.button_subscribe_event)

        // Nothing happens, button subscribe should not have changed
        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)
    }
}

