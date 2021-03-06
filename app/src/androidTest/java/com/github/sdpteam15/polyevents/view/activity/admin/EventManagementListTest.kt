package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.adapter.EventListAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class EventManagementListTest {
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedEventDB: EventDatabaseInterface
    lateinit var scenario: ActivityScenario<EventManagementListActivity>

    private val event1 =
        Event(
            eventName = "Test 1",
            eventId = "Id1",
            zoneId = " zid1",
            zoneName = "zoneName1",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now()
        )
    private val event2 =
        Event(eventName = "Test 2", eventId = "Id2", zoneId = " zid1", zoneName = "zoneName1")
    private val event3 =
        Event(eventName = "Test 3", eventId = "Id3", zoneId = " zid2", zoneName = "zoneName2")
    private val event4 =
        Event(eventName = "Test 4", eventId = "Id4", zoneId = " zid3", zoneName = "zoneName3")

    private lateinit var events: MutableList<Event>
    private lateinit var zones: MutableList<Zone>
    private var nbzones: Int = 0

    private fun setupEventsAndZones() {
        events = mutableListOf(event1, event2, event3, event4)
        zones = mutableListOf(
            Zone(zoneId = "zid1", zoneName = "zoneName1"),
            Zone(zoneId = "zid2", zoneName = "zoneName2"),
            Zone(zoneId = "zid3", zoneName = "zoneName3")
        )
        nbzones = zones.size
    }

    private fun setupDb() {
        val mockedZoneDB = Mockito.mock(ZoneDatabaseInterface::class.java)
        val mockeduserDb = Mockito.mock(UserDatabaseInterface::class.java)

        Mockito.`when`(
            mockeduserDb.getListAllUsers(
                anyOrNull()
            )
        ).thenAnswer {
            Observable(true)
        }

        Mockito.`when`(
            mockedZoneDB.getActiveZones(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[0] as ObservableList<Zone>).add(
                Zone(
                    zoneName = "Test zone",
                    zoneId = "zoneId"
                )
            )
            Observable(true)
        }
        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        mockedEventDB = Mockito.mock(EventDatabaseInterface::class.java)

        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        Mockito.`when`(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)
        Mockito.`when`(mockedDatabase.userDatabase).thenReturn(mockeduserDb)
        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[0] as ObservableList<Event>).addAll(events)
                Observable(true, this)
            }

        Mockito.`when`(mockedEventDB.getEventFromId(anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[1] as Observable<Event>).postValue(event1)
                Observable(true)
            }

        Database.currentDatabase = mockedDatabase

    }

    @Before
    fun setup() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventManagementListActivity::class.java
        )

        setupEventsAndZones()
        setupDb()


        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
        scenario.close()
    }

    @Test
    fun clickOnBtnCreateZoneLaunchCorrectActivityWithEmptyFields() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.id_new_event_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun correctNumberZonesDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list_admin))
            .check(RecyclerViewItemCountAssertion(nbzones))
    }

    @Test
    fun failToLoadEventsReturnToMainActivity() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )

        val mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(UserEntity("uid"))
        Mockito.`when`(mockedDatabase.currentUserObservable).thenReturn(Observable())

        MainActivity.instance = null
        MainActivity.selectedRole = UserRole.ADMIN

        val mockedEventDB = Mockito.mock(EventDatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenAnswer {
                Observable(false, this)
            }

        Database.currentDatabase = mockedDatabase
        scenario = ActivityScenario.launch(intent)
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_admin))
            .check(ViewAssertions.matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.id_manage_event_button))
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_admin))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun clickOnZoneDisplayTheEvents() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list_admin)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventListAdapter.CustomViewHolder<EventListAdapter.ZoneViewHolder>>(
                0, click()
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list_admin))
            .check(RecyclerViewItemCountAssertion(nbzones + 2))
    }

    @Test
    fun clickOnUpdateBtnRedirectToCorrectActivity() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list_admin)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventListAdapter.CustomViewHolder<EventListAdapter.ZoneViewHolder>>(
                0, click()
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.recycler_events_list_admin)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventListAdapter.CustomViewHolder<EventListAdapter.EventViewHolder>>(
                1, TestHelper.clickChildViewWithId(R.id.id_edit_event_button)
            )
        )

        Intents.intended(IntentMatchers.hasComponent(EventManagementActivity::class.java.name))
        Intents.intended(
            IntentMatchers.hasExtra(
                EventManagementListActivity.EVENT_ID_INTENT,
                "Id1"
            )
        )
        Intents.release()
    }
}