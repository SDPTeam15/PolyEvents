package com.github.sdpteam15.polyevents.view.activity


import android.content.Context
import android.content.Intent
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.room.EventLocal
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When


@RunWith(MockitoJUnitRunner::class)
class EventActivityTest {

    lateinit var testUser: UserEntity
    val uid = "testUid"
    val username = "john"
    val email = "user@email.com"

    lateinit var testLimitedEvent: Event
    val limitedEventId = "limitedEvent"

    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedEventDatabase: EventDatabaseInterface

    lateinit var scenario: ActivityScenario<EventActivity>

    private lateinit var localDatabase: LocalDatabase

    @Before
    fun setup() {
        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )

        mockedDatabase = mock(DatabaseInterface::class.java)
        mockedEventDatabase = mock(EventDatabaseInterface::class.java)
        When(mockedDatabase.currentUser).thenReturn(testUser)
        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDatabase)

        currentDatabase = mockedDatabase

        testLimitedEvent = Event(
            eventId = "limitedEvent",
            eventName = "limited Event only",
            description = "Super noisy activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 21, 15),
            organizer = "AcademiC DeCibel",
            zoneName = "Concert Hall",
            tags = mutableSetOf("music", "live", "pogo")
        )
        testLimitedEvent.makeLimitedEvent(3)

        When(
            mockedEventDatabase.getEventFromId(
                id = limitedEventId, returnEvent = EventActivity.obsEvent
            )
        ).then {
            EventActivity.obsEvent.postValue(testLimitedEvent)
            Observable(true)
        }

        // Create local db
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        localDatabase = Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        // close and remove the mock local database
        localDatabase.close()
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun eventActivityCorrectlyShowsEvent() {
        goToEventActivityWithLimitedEventIntent()

        Espresso.onView(withId(R.id.txt_event_Name))
            .check(matches(withText(containsString(testLimitedEvent.eventName))))

        Espresso.onView(withId(R.id.txt_event_description))
            .check(matches(withText(containsString(testLimitedEvent.description))))


        Espresso.onView(withId(R.id.txt_event_organizer))
            .check(matches(withText(containsString(testLimitedEvent.organizer))))

        Espresso.onView(withId(R.id.txt_event_zone))
            .check(matches(withText(containsString(testLimitedEvent.zoneName))))

        Espresso.onView(withId(R.id.txt_event_date))
            .check(matches(withText(containsString(testLimitedEvent.formattedStartTime()))))

        Espresso.onView(withId(R.id.txt_event_tags))
            .check(matches(withText(containsString(testLimitedEvent.tags.joinToString { s -> s }))))

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)
        //TODO check image is correct
    }

    @Test
    fun testEventSubscription() {
        goToEventActivityWithLimitedEventIntent()

        clickOn(R.id.button_subscribe_event)

        // Making sure EventActivity.obsEvent and the testEvent instance are the same here
        assert(EventActivity.obsEvent.value!!.getParticipants().contains(uid))
        assert(EventActivity.event.getParticipants().contains(uid))
        assert(testLimitedEvent.getParticipants().contains(uid))
        assertDisplayed(R.id.button_subscribe_event, R.string.event_unsubscribe)

        // Unsubscribe
        clickOn(R.id.button_subscribe_event)

        assert(!testLimitedEvent.getParticipants().contains(currentDatabase.currentUser!!.uid))
        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        scenario.close()
    }

    @Test
    fun testEventSubscriptionUpdatesLocalDatabase() = runBlocking {
        goToEventActivityWithLimitedEventIntent()

        // Subscribe to event
        clickOn(R.id.button_subscribe_event)

        val retrievedLocalEventsAfterSubscription = localDatabase.eventDao().getAll()
        assert(retrievedLocalEventsAfterSubscription.isNotEmpty())
        assertEquals(retrievedLocalEventsAfterSubscription[0], EventLocal.fromEvent(testLimitedEvent))

        assertDisplayed(R.id.button_subscribe_event, R.string.event_unsubscribe)

        // Unsubscribe from event
        clickOn(R.id.button_subscribe_event)

        val retrievedLocalEventsAfterUnSubscription = localDatabase.eventDao().getAll()
        assert(retrievedLocalEventsAfterUnSubscription.isEmpty())

        scenario.close()
    }

    @Test
    fun testEventSubscriptionForFullEvent() {
        testLimitedEvent.makeLimitedEvent(1)
        testLimitedEvent.addParticipant("bogusId")
        assert(testLimitedEvent.getMaxNumberOfSlots() == testLimitedEvent.getParticipants().size)

        goToEventActivityWithLimitedEventIntent()

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        clickOn(R.id.button_subscribe_event)

        // Nothing happens, button subscribe should not have changed
        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)
        scenario.close()
    }

    @Test
    fun testSubscriptionToEventWithNoUserLoggedIn() {
        When(mockedDatabase.currentUser).thenReturn(null)

        goToEventActivityWithLimitedEventIntent()

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        clickOn(R.id.button_subscribe_event)
        // Nothing happens, button subscribe should not have changed (Show should toast to login)
        assert(testLimitedEvent.getParticipants().isEmpty())
        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        scenario.close()
    }

    @Test
    fun testEventActivityWhenAlreadySubscribedToEvent() {
        testLimitedEvent.addParticipant(uid)

        goToEventActivityWithLimitedEventIntent()
        assert(EventActivity.obsEvent.value!!.getParticipants().contains(uid))

        assertDisplayed(R.id.button_subscribe_event, R.string.event_unsubscribe)

        // Now unsubscribe
        clickOn(R.id.button_subscribe_event)
        assert(!EventActivity.obsEvent.value!!.getParticipants().contains(uid))
        assert(!testLimitedEvent.getParticipants().contains(uid))

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)
    }

    @Test
    fun testEventFetchFailedDoesNotDisplayAnything() {
        When(mockedEventDatabase.getEventFromId(id = limitedEventId,
            returnEvent = EventActivity.obsEvent)).thenReturn(Observable(false))

        goToEventActivityWithLimitedEventIntent()

        // Nothing displayed
        Espresso.onView(withId(R.id.txt_event_Name))
            .check(matches(withText(containsString(""))))
    }

    @Test
    fun testPublicEventShouldHaveNoSubscribeButton() {
        When(
            mockedEventDatabase.getEventFromId(
                id = limitedEventId, returnEvent = EventActivity.obsEvent
            )
        ).then {
            EventActivity.obsEvent.postValue(testLimitedEvent.copy(
                limitedEvent = false, maxNumberOfSlots = null)
            )
            Observable(true)
        }

        goToEventActivityWithLimitedEventIntent()

        assertNotDisplayed(R.id.button_subscribe_event)
    }

    private fun goToEventActivityWithLimitedEventIntent() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventActivity::class.java
        ).apply {
            putExtra(EXTRA_EVENT_ID, limitedEventId)
        }

        scenario = ActivityScenario.launch(intent)

        EventActivity.database = localDatabase

        Thread.sleep(1000)
    }
}

