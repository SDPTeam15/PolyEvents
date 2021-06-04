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
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabaseUser
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
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
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
class EventListFragmentTest {
    lateinit var event1: Event
    lateinit var event2: Event
    lateinit var event3: Event
    lateinit var user: UserEntity

    val firstEventName = "Sushi demo"

    private lateinit var localDatabase: LocalDatabase

    @Before
    fun setup() {
        event1 = Event(
            eventId = "1",
            eventName = firstEventName,
            description = "Super hungry activity !",
            startTime = LocalDateTime.of(2022, 3, 7, 12, 15),
            endTime = LocalDateTime.of(2022, 3, 7, 13, 0, 0),
            organizer = "The fish band",
            zoneName = "Kitchen",
            tags = mutableListOf("sushi", "japan", "cooking")
        )

        user = UserEntity(
            username = "username",
            uid = "uidtest",
            name = "testname",
            email = "emailtest"
        )
        event1.makeLimitedEvent(25)

        event2 = Event(
            eventId = "2",
            eventName = "Aqua Poney",
            description = "Super cool activity !" +
                    " With a super long description that essentially describes and explains" +
                    " the content of the activity we are speaking of.",
            startTime = LocalDateTime.of(2022, 3, 7, 14, 15),
            endTime = LocalDateTime.of(2022, 3, 8, 13, 0, 0),
            organizer = "The Aqua Poney team",
            zoneName = "Swimming pool"
        )
        event2.makeLimitedEvent(25)

        event3 = Event(
            eventId = "3",
            eventName = "Concert",
            description = "Super noisy activity !",
            startTime = LocalDateTime.of(2022, 3, 7, 21, 15),
            endTime = LocalDateTime.of(2022, 3, 8, 23, 0, 0),
            organizer = "AcademiC DeCibel",
            zoneName = "Concert Hall",
            tags = mutableListOf("music", "live", "pogo")
        )

        FakeDatabaseEvent.events.clear()
        FakeDatabase.eventDatabase.createEvent(
            event1
        )
        FakeDatabase.eventDatabase.createEvent(
            event2
        )
        FakeDatabase.eventDatabase.createEvent(
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
            localDatabase.eventDao()
        ).create(
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
    fun checkObsoleteEventIsRemovedAfterSyncWithDatabase() = runBlocking {
        val testBogusEventId = "Bla bla this event does not exist anymore in remote database"
        val testBogusEvent =
            EventLocal.fromEvent(
                event1.copy(
                    eventId = testBogusEventId
                )
            )

        localDatabase.eventDao().insert(testBogusEvent)

        clickOn(R.id.event_list_my_events_switch)

        assertChecked(R.id.event_list_my_events_switch)

        // My events updated
        assertRecyclerViewItemCount(R.id.recycler_events_list, 0)

        // Check deleted in local database
        val retrievedEvents = localDatabase.eventDao().getEventById(testBogusEventId)
        assert(retrievedEvents.isEmpty())
    }

    @Test
    fun testMyEventsDisplayedWhenSwitchIsOn() = runBlocking {
        clickOn(R.id.event_list_my_events_switch)

        assertChecked(R.id.event_list_my_events_switch)

        // My events initially empty
        assertRecyclerViewItemCount(R.id.recycler_events_list, 0)

        clickOn(R.id.event_list_my_events_switch)

        assertUnchecked(R.id.event_list_my_events_switch)

        val testEvents = FakeDatabaseEvent.events.values.take(2)
        testEvents.forEach {
            // Event id cannot be null if creating eventLocal from event
            localDatabase.eventDao().insert(EventLocal.fromEvent(it))
        }

        clickOn(R.id.event_list_my_events_switch)

        assertChecked(R.id.event_list_my_events_switch)

        // My events updated
        Thread.sleep(2000)
        assertRecyclerViewItemCount(R.id.recycler_events_list, 2)
    }

    @Test
    fun testMyEventsShouldStillBeDisplayedIfNoUserLoggedIn() = runBlocking {
        val testEvents = FakeDatabaseEvent.events.values.take(2)
        testEvents.forEach {
            // Event id cannot be null if creating eventLocal from event
            localDatabase.eventDao().insert(EventLocal.fromEvent(it))
        }

        val mockedDatabase = mock(DatabaseInterface::class.java)
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
    @Suppress("UNCHECKED_CAST")
    fun eventActivityShowsValuesFromGivenActivity() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventActivity::class.java
        )

        val mockedUserDatabase = mock(UserDatabaseInterface::class.java)
        Mockito.`when`(mockedUserDatabase.getUserInformation(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[0] as Observable<UserEntity>).postValue(user)
            Observable(true)
        }

        val events = ObservableList<Event>()
        currentDatabase.eventDatabase.getEvents(events, 1,null )
        currentDatabase.userDatabase = mockedUserDatabase

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
                            user.name
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
        currentDatabase.userDatabase = FakeDatabaseUser
        scenario.close()
    }

}