package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.admin.EventEditManagementActivity
import com.github.sdpteam15.polyevents.view.adapter.EventEditAdminAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import kotlin.test.assertEquals

@Suppress("UNCHECKED_CAST")
class EventEditDifferenceFragmentTest {
    private val mockedDatabase = HelperTestFunction.defaultMockDatabase()
    private val mockedDatabaseEvent = Mockito.mock(EventDatabaseInterface::class.java)

    private val event1 =
        Event(
            eventName = "Test 1",
            eventId = "Id1",
            zoneId = " zid1",
            zoneName = "zoneName1",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            description = "description"
        )
    private val event2 =
        Event(
            eventName = "Test 2",
            eventId = "Id2",
            zoneId = " zid1",
            zoneName = "zoneName1",
            limitedEvent = true,
            maxNumberOfSlots = 10
        )
    private val event3 =
        Event(eventName = "Test 3", eventId = "Id3", zoneId = " zid2", zoneName = "zoneName2")
    private val event4 =
        Event(eventName = "Test 4", eventId = "Id4", zoneId = " zid3", zoneName = "zoneName3")

    private val eventEdit1 =
        Event(
            eventName = "Test 1",
            eventId = "Id1",
            description = "asdsadas",
            zoneId = " zid1",
            zoneName = "zoneName4",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            eventEditId = "EID1",
            status = Event.EventStatus.PENDING,
            limitedEvent = true,
            maxNumberOfSlots = 10
        )
    private val eventEdit2 =
        Event(
            eventName = "Test 2",
            eventId = null,
            zoneId = " zid1",
            zoneName = "zoneName1",
            eventEditId = "EID2",
            status = Event.EventStatus.PENDING,
            description = "asd",
            limitedEvent = true,
            maxNumberOfSlots = 10
        )
    private val eventEdit3 =
        Event(
            eventName = "Test 3", eventId = "Id3", zoneId = " zid2", zoneName = "zoneName2",
            eventEditId = "EID3", status = Event.EventStatus.PENDING
        )
    private val eventEdit4 =
        Event(
            eventName = "Test 4", eventId = "Id4", zoneId = " zid3", zoneName = "zoneName3",
            eventEditId = "EID4", status = Event.EventStatus.PENDING
        )

    private lateinit var events: MutableList<Event>
    private lateinit var eventsEdit: MutableList<Event>

    private fun setupEventsAndZones() {
        events = mutableListOf(event1, event2, event3, event4)
        eventsEdit = mutableListOf(eventEdit1, eventEdit2, eventEdit3, eventEdit4)
    }


    @Before
    fun setup() {
        setupEventsAndZones()

        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedDatabaseEvent)

        Mockito.`when`(
            mockedDatabaseEvent.getEvents(
                anyOrNull(), anyOrNull(), anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[2] as ObservableList<Event>).addAll(events)
            Observable(true)
        }

        Mockito.`when`(mockedDatabaseEvent.getEventEdits(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[1] as ObservableList<Event>).addAll(eventsEdit)
                Observable(true)
            }


        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                EventEditManagementActivity::class.java
            )
        Database.currentDatabase = mockedDatabase
        ActivityScenario.launch<EventEditManagementActivity>(intent)
        Thread.sleep(500)

    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun displayFragmentProperly() {

        onView(withId(R.id.id_recycler_event_edits)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventEditAdminAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_edit_see)
            )
        )
        Thread.sleep(1000)
        onView(withId(R.id.fragment_event_edit_diff)).check(matches(isDisplayed()))
        onView(withId(R.id.tvModDesc)).check(matches(withText(eventEdit1.description!!)))
        onView(withId(R.id.tvModEndDate)).check(
            matches(
                withText(
                    HelperFunctions.localDatetimeToString(eventEdit1.endTime)
                )
            )
        )
        onView(withId(R.id.tvModStartDate)).check(
            matches(
                withText(
                    HelperFunctions.localDatetimeToString(
                        eventEdit1.startTime
                    )
                )
            )
        )
        onView(withId(R.id.tvModName)).check(
            matches(
                withText(
                    eventEdit1.eventName!!
                )
            )
        )
        onView(withId(R.id.tvModZone)).check(matches(withText(eventEdit1.zoneName!!)))

        onView(withId(R.id.tvOrigDesc)).check(matches(withText(event1.description!!)))
        onView(withId(R.id.tvOrigEndDate)).check(
            matches(
                withText(
                    HelperFunctions.localDatetimeToString(event1.endTime)
                )
            )
        )
        onView(withId(R.id.tvOrigStartDate)).check(
            matches(
                withText(
                    HelperFunctions.localDatetimeToString(
                        event1.startTime
                    )
                )
            )
        )
        onView(withId(R.id.tvOrigName)).check(
            matches(
                withText(
                    event1.eventName!!
                )
            )
        )
        onView(withId(R.id.tvOrigZone)).check(matches(withText(event1.zoneName!!)))
    }

    @Test
    fun displayFragmentWithNullProperly() {
        onView(withId(R.id.id_recycler_event_edits)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventEditAdminAdapter.ItemViewHolder>(
                1, TestHelper.clickChildViewWithId(R.id.id_edit_see)
            )
        )
        Thread.sleep(1000)
        onView(withId(R.id.fragment_event_edit_diff)).check(matches(isDisplayed()))
        onView(withId(R.id.tvModDesc)).check(matches(withText(eventEdit2.description!!)))
        onView(withId(R.id.tvModEndDate)).check(
            matches(
                withText(
                    HelperFunctions.localDatetimeToString(eventEdit2.endTime)
                )
            )
        )
        onView(withId(R.id.tvModStartDate)).check(
            matches(
                withText(
                    HelperFunctions.localDatetimeToString(
                        eventEdit2.startTime
                    )
                )
            )
        )
        onView(withId(R.id.tvModName)).check(
            matches(
                withText(
                    eventEdit2.eventName!!
                )
            )
        )
        onView(withId(R.id.tvModZone)).check(matches(withText(eventEdit2.zoneName!!)))
        onView(withId(R.id.btnCloseFragment)).perform(click())
        onView(withId(R.id.id_recycler_event_edits)).check(matches(isDisplayed()))
    }

    @Test
    fun canAcceptARequest() {

        Mockito.`when`(mockedDatabaseEvent.updateEventEdit(anyOrNull(), anyOrNull())).thenAnswer {
            val eventEdit = (it.arguments[0] as Event)
            assertEquals(eventEdit.status, Event.EventStatus.ACCEPTED)
            Observable(true)
        }
        Mockito.`when`(mockedDatabaseEvent.updateEvent(anyOrNull(), anyOrNull())).thenAnswer {
            assertEquals(eventEdit1, it.arguments[0] as Event)
            Observable(true)
        }

        Mockito.`when`(mockedDatabaseEvent.createEvent(anyOrNull(), anyOrNull())).thenAnswer {
            assertEquals(eventEdit2, it.arguments[0] as Event)
            Observable(true)
        }

        onView(withId(R.id.id_recycler_event_edits)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventEditAdminAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_edit_accept)
            )
        )

        onView(withId(R.id.id_recycler_event_edits)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventEditAdminAdapter.ItemViewHolder>(
                1, TestHelper.clickChildViewWithId(R.id.id_edit_accept)
            )
        )

        Thread.sleep(1000)
        onView(withId(R.id.id_recycler_event_edits)).check(matches(isDisplayed()))
    }

    @Test
    fun canRefuseARequest() {

        Mockito.`when`(mockedDatabaseEvent.updateEventEdit(anyOrNull(), anyOrNull())).thenAnswer {
            val eventEdit = (it.arguments[0] as Event)
            assertEquals(eventEdit.status, Event.EventStatus.REFUSED)
            Observable(true)
        }

        onView(withId(R.id.id_recycler_event_edits)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventEditAdminAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_edit_refuse)
            )
        )
        onView(withId(R.id.id_btn_confirm_refuse_request)).perform(
            click()
        )
        onView(withId(R.id.id_recycler_event_edits)).check(matches(isDisplayed()))
    }

    @Test
    fun canRefuseARequestAndNotChangeTheRecycler() {

        Mockito.`when`(mockedDatabaseEvent.updateEventEdit(anyOrNull(), anyOrNull())).thenAnswer {
            val eventEdit = (it.arguments[0] as Event)
            assertEquals(eventEdit.status, Event.EventStatus.REFUSED)
            Observable(false)
        }

        onView(withId(R.id.id_recycler_event_edits)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventEditAdminAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_edit_refuse)
            )
        )
        onView(withId(R.id.id_btn_confirm_refuse_request)).perform(
            click()
        )

        onView(withId(R.id.id_recycler_event_edits)).check(matches(isDisplayed()))
    }
    @Test
    fun canAcceptARequestAndNotChangeTheRecycler() {
        Mockito.`when`(mockedDatabaseEvent.updateEventEdit(anyOrNull(), anyOrNull())).thenAnswer {
            val eventEdit = (it.arguments[0] as Event)
            assertEquals(eventEdit.status, Event.EventStatus.ACCEPTED)
            Observable(false)
        }

        onView(withId(R.id.id_recycler_event_edits)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventEditAdminAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_edit_accept)
            )
        )

        onView(withId(R.id.id_recycler_event_edits)).check(matches(isDisplayed()))
    }
}