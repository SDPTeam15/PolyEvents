package com.github.sdpteam15.polyevents.view.activity.activityprovider

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
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.admin.EventManagementActivity
import com.github.sdpteam15.polyevents.view.adapter.MyEventEditRequestAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as When

class MyEventEditManagementTest {
    private lateinit var eventEditAccepted: MutableList<Event>
    private lateinit var eventEditRefused: MutableList<Event>
    private lateinit var eventEditPending: MutableList<Event>
    private lateinit var allEvent: MutableList<Event>
    private lateinit var mockedEventDB: EventDatabaseInterface
    private lateinit var mockedDb: DatabaseInterface
    private lateinit var eventEdit1: Event

    fun setupList() {
        eventEdit1 =
            Event(
                eventId = "eventid1",
                eventName = "EventName1",
                eventEditId = "eventeditid1",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                description = "descedit1",
                status = Event.EventStatus.ACCEPTED
            )
        val eventEdit2 =
            Event(
                eventId = "eventid2",
                eventName = "EventName2",
                eventEditId = "eventeditid2",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                description = "descedit2",
                status = Event.EventStatus.REFUSED
            )
        val eventEdit3 =
            Event(
                eventId = "eventid3",
                eventName = "EventName3",
                eventEditId = "eventeditid3",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                description = "descedit3",
                status = Event.EventStatus.PENDING
            )
        eventEditAccepted = mutableListOf(eventEdit1)
        eventEditRefused = mutableListOf(eventEdit2)
        eventEditPending = mutableListOf(eventEdit3)
        allEvent = mutableListOf(eventEdit1, eventEdit2, eventEdit3)
    }

    @Before
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        setupList()
        mockedDb = HelperTestFunction.defaultMockDatabase()
        mockedEventDB = mock(EventDatabaseInterface::class.java)
        When(mockedDb.eventDatabase).thenReturn(mockedEventDB)

        When(mockedEventDB.getEventEdits(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[1] as ObservableList<Event>).addAll(allEvent)
            Observable(true)
        }
        When(mockedDb.currentUser).thenReturn(UserEntity(uid = "uidtest"))

        When(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[2] as ObservableList<Event>).addAll(allEvent)
            Observable(true)
        }
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventManagementActivityProvider::class.java
        )
        Database.currentDatabase = mockedDb
        ActivityScenario.launch<EventManagementActivityProvider>(intent)
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun testCorrectNumberOfItemsDisplayed() {
        Thread.sleep(1000)
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(eventEditPending.size))
        Espresso.onView(withId(R.id.id_change_request_status_left))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(eventEditRefused.size))
        Espresso.onView(withId(R.id.id_change_request_status_left))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(eventEditAccepted.size))
        Espresso.onView(withId(R.id.id_change_request_status_left))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(eventEditPending.size))
        Espresso.onView(withId(R.id.id_change_request_status_right))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(eventEditAccepted.size))
        Espresso.onView(withId(R.id.id_change_request_status_right))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(eventEditRefused.size))
        Espresso.onView(withId(R.id.id_change_request_status_right))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(eventEditPending.size))
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun clickOnModifyLaunchCorrectActivity() {
        Thread.sleep(1000)
        When(mockedEventDB.getEventEditFromId(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[1] as? Observable<Event>)?.postValue(eventEdit1)
            Observable(true)
        }
        Intents.init()
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MyEventEditRequestAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_modify_request)
            )
        )
        Intents.intended(IntentMatchers.hasComponent(EventManagementActivity::class.java.name))
        Intents.release()
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun clickOnDeleteRemoveEventEdit() {
        Thread.sleep(1000)

        When(mockedEventDB.removeEventEdit(anyOrNull())).thenAnswer {
            Observable(true)
        }
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MyEventEditRequestAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_delete_request)
            )
        )

        Thread.sleep(1500)
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests))
            .check(RecyclerViewItemCountAssertion(0))

    }

    @Test
    fun clickOnModifyDisplayCorrectFragment() {
        Thread.sleep(1000)

        When(mockedEventDB.removeEventEdit(anyOrNull())).thenAnswer {
            Observable(true)
        }
        Espresso.onView(withId(R.id.id_recycler_my_event_edit_requests)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MyEventEditRequestAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_see_event)
            )
        )

        Thread.sleep(500)
        Espresso.onView(withId(R.id.fragment_event_edit_diff))
            .check(ViewAssertions.matches(isDisplayed()))
    }
}