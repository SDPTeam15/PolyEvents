package com.github.sdpteam15.polyevents.view.fragments

import android.content.Context
import android.content.Intent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseEvent
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.EventLocal
import com.github.sdpteam15.polyevents.view.activity.EventActivity
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory
import com.schibsted.spain.barista.assertion.BaristaCheckedAssertions.assertChecked
import com.schibsted.spain.barista.assertion.BaristaCheckedAssertions.assertUnchecked
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime

// TODO: test room with internet off?
@RunWith(MockitoJUnitRunner::class)
class EventListFragmentTest {
    lateinit var event1: Event
    lateinit var event2: Event
    lateinit var event3 : Event

    val firstEventName = "Sushi demo"

    private lateinit var localDatabase: LocalDatabase

    @Before
    fun setup() {
        event1 = Event(
            eventName = firstEventName,
            description = "Super hungry activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
            endTime = LocalDateTime.of(2021, 3, 7, 13, 0, 0),
            organizer = "The fish band",
            zoneName = "Kitchen",
            tags = mutableSetOf("sushi", "japan", "cooking")
        )
        event1.makeLimitedEvent(25)

        event2 = Event(
            eventName = "Aqua Poney",
            description = "Super cool activity !" +
                    " With a super long description that essentially describes and explains" +
                    " the content of the activity we are speaking of.",
            startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
            organizer = "The Aqua Poney team",
            zoneName = "Swimming pool"
        )
        event2.makeLimitedEvent(25)

        event3 = Event(
            eventName = "Concert",
            description = "Super noisy activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 21, 15),
            organizer = "AcademiC DeCibel",
            zoneName = "Concert Hall",
            tags = mutableSetOf("music", "live", "pogo")
        )

        FakeDatabaseEvent.events.clear()
        FakeDatabase.eventDatabase!!.createEvent(
            event1
        )
        FakeDatabase.eventDatabase!!.createEvent(
            event2
        )
        FakeDatabase.eventDatabase!!.createEvent(
            event3
        )

        currentDatabase = FakeDatabase

        // Create local db
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        localDatabase = Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()

        launchFragmentInContainer<EventListFragment>(themeResId = R.style.Theme_PolyEvents)

        EventListFragment.localDatabase = localDatabase
        EventListFragment.eventLocalViewModel = EventLocalViewModelFactory(
            localDatabase.eventDao()).create(
            EventLocalViewModel::class.java
        )
    }

    @After
    fun teardown() {
        // close and remove the mock local database
        localDatabase.close()
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun testMyEventsDisplayedWhenSwitchIsOn() = runBlocking {
        clickOn(R.id.event_list_my_events_switch)

        assertChecked(R.id.event_list_my_events_switch)

        // My events initially empty
        assertRecyclerViewItemCount(R.id.recycler_events_list, 0)

        clickOn(R.id.event_list_my_events_switch)

        assertUnchecked(R.id.event_list_my_events_switch)

        // Event id cannot be null if creating eventLocal from event
        localDatabase.eventDao().insert(EventLocal.fromEvent(event1.copy(eventId = "1")))
        localDatabase.eventDao().insert(EventLocal.fromEvent(event2.copy(eventId = "2")))

        clickOn(R.id.event_list_my_events_switch)

        assertChecked(R.id.event_list_my_events_switch)

        // My events updated
        assertRecyclerViewItemCount(R.id.recycler_events_list, 2)
    }

    @Test
    fun testMyEventsShouldStillBeDisplayedIfNoUserLoggedIn() = runBlocking {
        // Event id cannot be null if creating eventLocal from event
        localDatabase.eventDao().insert(EventLocal.fromEvent(event1.copy(eventId = "1")))
        localDatabase.eventDao().insert(EventLocal.fromEvent(event2.copy(eventId = "2")))

        val mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(null)
        currentDatabase = mockedDatabase

        clickOn(R.id.event_list_my_events_switch)

        assertChecked(R.id.event_list_my_events_switch)
        assertRecyclerViewItemCount(R.id.recycler_events_list, 2)
    }

    @Test
    fun correctNumberUpcomingEventsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list))
            .check(RecyclerViewItemCountAssertion(FakeDatabaseEvent.events.size))
    }

    @Test
    fun eventActivityOpensOnClick() {
        assertDisplayedAtPosition(
            R.id.recycler_events_list, 0, R.id.id_event_name_text, firstEventName
        )

        clickListItem(R.id.recycler_events_list, 0)

        assertDisplayed(R.id.txt_event_Name)
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