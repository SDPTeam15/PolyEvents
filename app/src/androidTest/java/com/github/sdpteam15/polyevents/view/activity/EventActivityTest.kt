package com.github.sdpteam15.polyevents.view.activity


import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RatingBar
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID
import com.schibsted.spain.barista.assertion.BaristaProgressBarAssertions.assertProgress
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
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
            eventId = limitedEventId,
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
        scenario.close()
        // close and remove the mock local database
        localDatabase.close()
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun newCommentAdded() {
        goToEventActivityWithIntent()

        assertEquals(0, EventActivity.obsComments.size)
        val comment = Rating("Rating 1", 5f, "TROP COOL yes")
        EventActivity.obsComments.add(comment, this)
        assertEquals(1, EventActivity.obsComments.size)
        EventActivity.obsComments.clear()
        val comment2 = Rating("Rating 2", 5f, "")
        EventActivity.obsComments.add(comment2, this)
        Thread.sleep(1000)
        assertEquals(0, EventActivity.obsComments.size)
    }

    @Test
    fun eventActivityCorrectlyShowsEvent() {
        goToEventActivityWithIntent()

        onView(withId(R.id.txt_event_Name))
            .check(matches(withText(containsString(testLimitedEvent.eventName))))

        onView(withId(R.id.txt_event_description))
            .check(matches(withText(containsString(testLimitedEvent.description))))


        onView(withId(R.id.txt_event_organizer))
            .check(matches(withText(containsString(testLimitedEvent.organizer))))

        onView(withId(R.id.txt_event_zone))
            .check(matches(withText(containsString(testLimitedEvent.zoneName))))

        onView(withId(R.id.txt_event_date))
            .check(matches(withText(containsString(testLimitedEvent.formattedStartTime()))))

        onView(withId(R.id.txt_event_tags))
            .check(matches(withText(containsString(testLimitedEvent.tags.joinToString { s -> s }))))

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)
        //TODO check image is correct
    }

    @Test
    fun testEventSubscription() {
        goToEventActivityWithIntent()

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
    }

    @Test
    fun testEventSubscriptionUpdatesLocalDatabase() = runBlocking {
        goToEventActivityWithIntent()

        // Subscribe to event
        clickOn(R.id.button_subscribe_event)

        val retrievedLocalEventsAfterSubscription = localDatabase.eventDao().getAll()
        assert(retrievedLocalEventsAfterSubscription.isNotEmpty())
        assertEquals(
            retrievedLocalEventsAfterSubscription[0],
            EventLocal.fromEvent(testLimitedEvent)
        )

        assertDisplayed(R.id.button_subscribe_event, R.string.event_unsubscribe)

        // Unsubscribe from event
        clickOn(R.id.button_subscribe_event)

        val retrievedLocalEventsAfterUnSubscription = localDatabase.eventDao().getAll()
        assert(retrievedLocalEventsAfterUnSubscription.isEmpty())
    }

    @Test
    fun testEventSubscriptionForFullEvent() {
        testLimitedEvent.makeLimitedEvent(1)
        testLimitedEvent.addParticipant("bogusId")
        assert(testLimitedEvent.getMaxNumberOfSlots() == testLimitedEvent.getParticipants().size)

        goToEventActivityWithIntent()

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        clickOn(R.id.button_subscribe_event)

        // Nothing happens, button subscribe should not have changed
        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)
    }

    @Test
    fun testSubscriptionToEventWithNoUserLoggedIn() {
        When(mockedDatabase.currentUser).thenReturn(null)

        goToEventActivityWithIntent()

        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)

        clickOn(R.id.button_subscribe_event)
        // Nothing happens, button subscribe should not have changed (Show should toast to login)
        assert(testLimitedEvent.getParticipants().isEmpty())
        assertDisplayed(R.id.button_subscribe_event, R.string.event_subscribe)
    }

    @Test
    fun testEventActivityWhenAlreadySubscribedToEvent() {
        testLimitedEvent.addParticipant(uid)

        goToEventActivityWithIntent()
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
        When(
            mockedEventDatabase.getEventFromId(
                id = limitedEventId,
                returnEvent = EventActivity.obsEvent
            )
        ).thenReturn(Observable(false))

        goToEventActivityWithIntent()

        // Nothing displayed
        onView(withId(R.id.txt_event_Name))
            .check(matches(withText(containsString(""))))
    }

    @Test
    fun testPublicEventShouldHaveNoSubscribeButton() {
        When(
            mockedEventDatabase.getEventFromId(
                id = limitedEventId, returnEvent = EventActivity.obsEvent
            )
        ).then {
            EventActivity.obsEvent.postValue(
                testLimitedEvent.copy(
                    limitedEvent = false, maxNumberOfSlots = null
                )
            )
            Observable(true)
        }

        goToEventActivityWithIntent()

        assertNotDisplayed(R.id.button_subscribe_event)
    }

    @Test
    fun testLeaveReviewIsCorrectlyDisplayed() {
        goToEventActivityWithIntent()
        assertDisplayed(R.id.event_leave_review_button)

        // Click review event
        clickOn(R.id.event_leave_review_button)
        assertDisplayed(R.id.leave_review_dialog_fragment)
        assertDisplayed(R.id.leave_review_fragment_save_button)
        assertDisplayed(R.id.leave_review_fragment_title)
        assertDisplayed(R.id.leave_review_fragment_rating)
        assertDisplayed(R.id.leave_review_fragment_feedback_text)
        assertDisplayed(R.id.leave_review_fragment_cancel_button)

        // Click on cancel
        clickOn(R.id.leave_review_fragment_cancel_button)
        assertNotExist(R.id.leave_review_fragment_save_button)
        assertNotExist(R.id.leave_review_fragment_title)
        assertNotExist(R.id.leave_review_fragment_rating)
        assertNotExist(R.id.leave_review_fragment_feedback_text)
        assertNotExist(R.id.leave_review_fragment_cancel_button)
    }

    @Test
    fun testUserNotLoggedInCantLeaveReview() {
        When(mockedDatabase.currentUser).thenReturn(null)
        goToEventActivityWithIntent()

        assertNotExist(R.id.leave_review_fragment_save_button)
        assertNotExist(R.id.leave_review_fragment_title)
        assertNotExist(R.id.leave_review_fragment_rating)
        assertNotExist(R.id.leave_review_fragment_feedback_text)
        assertNotExist(R.id.leave_review_fragment_cancel_button)
    }

    @Test
    fun testUserShouldNotBeAbleToStoreRatingIfHasNotRatedYet() {
        goToEventActivityWithIntent()
        assertDisplayed(R.id.event_leave_review_button)

        // Click review event
        clickOn(R.id.event_leave_review_button)
        assertDisplayed(R.id.leave_review_dialog_fragment)

        // Try to save rating
        clickOn(R.id.leave_review_fragment_save_button)
        // Dialog should still be displayed since the save shouldn't have worked
        assertDisplayed(R.id.leave_review_dialog_fragment)
    }

    @Test
    fun testUserShouldBeAbleToLeaveRating() {
        var createdRating: Rating? = null
        When(
            mockedEventDatabase.addRatingToEvent(
                anyOrNull(), anyOrNull()
            )
        ).thenAnswer {
            createdRating = (it.arguments[0] as Rating?)
            Observable(true)
        }

        goToEventActivityWithIntent()

        // Click review event
        clickOn(R.id.event_leave_review_button)
        assertDisplayed(R.id.leave_review_dialog_fragment)

        onView(withId(R.id.leave_review_fragment_rating)).perform(SetRating(4.0f))
        clickOn(R.id.leave_review_fragment_save_button)

        //assertNull(createdRating?.feedback)
        assertEquals(createdRating?.rate, 4.0f)
        assertEquals(createdRating?.userId, testUser.uid)
        assertEquals(createdRating?.eventId, limitedEventId)
    }

    @Test
    fun testUserCanCancelLeavingRating() {
        var createdRating: Rating? = null
        When(
            mockedEventDatabase.addRatingToEvent(
                anyOrNull(), anyOrNull()
            )
        ).thenAnswer {
            createdRating = (it.arguments[0] as Rating?)
            Observable(true)
        }

        goToEventActivityWithIntent()

        // Click review event
        clickOn(R.id.event_leave_review_button)
        assertDisplayed(R.id.leave_review_dialog_fragment)

        onView(withId(R.id.leave_review_fragment_rating)).perform(SetRating(4.0f))
        clickOn(R.id.leave_review_fragment_cancel_button)

        assertNull(createdRating)
        assertNotExist(R.id.leave_review_dialog_fragment)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testShouldUpdateRatingIfUserRatedAlready() {
        var existingRating = Rating(
            ratingId = "1",
            userId = testUser.uid,
            eventId = limitedEventId,
            rate = 4.0f,
            feedback = "Noice"
        )
        When(
            mockedEventDatabase.getUserRatingFromEvent(
                userId = anyOrNull(),
                eventId = anyOrNull(),
                returnedRating = anyOrNull(),
                userAccess = anyOrNull()
            )
        ).thenAnswer {
            // Not very robust, need to change if changed method signature
            (it.arguments[2] as Observable<Rating>).postValue(
                existingRating
            )
            Observable(true)
        }

        When(
            mockedEventDatabase.updateRating(
                rating = anyOrNull(),
                userAccess = anyOrNull()
            )
        ).then {
            existingRating = it.arguments[0] as Rating
            Observable(true)
        }

        goToEventActivityWithIntent()

        // Click review event
        clickOn(R.id.event_leave_review_button)
        assertDisplayed(R.id.leave_review_dialog_fragment)

        // Check fetched rating is displayed
        assertDisplayed(R.id.leave_review_fragment_feedback_text, existingRating.feedback!!)
        assertProgress(R.id.leave_review_fragment_rating, existingRating.rate!!.toInt())

        onView(withId(R.id.leave_review_fragment_rating)).perform(SetRating(3.0f))
        clickOn(R.id.leave_review_fragment_save_button)

        assertNotExist(R.id.leave_review_dialog_fragment)
        assertEquals(existingRating.rate, 3.0f)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testUpdateRatingWhenFailed() {
        val existingRating = Rating(
            ratingId = "1",
            userId = testUser.uid,
            eventId = limitedEventId,
            rate = 4.0f,
            feedback = "Noice"
        )
        When(
            mockedEventDatabase.getUserRatingFromEvent(
                userId = anyOrNull(),
                eventId = anyOrNull(),
                returnedRating = anyOrNull(),
                userAccess = anyOrNull()
            )
        ).thenAnswer {
            // Not very robust, need to change if changed method signature
            (it.arguments[2] as Observable<Rating>).postValue(
                existingRating
            )
            Observable(true)
        }

        When(
            mockedEventDatabase.updateRating(
                rating = anyOrNull(),
                userAccess = anyOrNull()
            )
        ).then {
            // Update failed
            Observable(false)
        }

        goToEventActivityWithIntent()

        // Click review event
        clickOn(R.id.event_leave_review_button)
        assertDisplayed(R.id.leave_review_dialog_fragment)

        // Check fetched rating is displayed
        assertDisplayed(R.id.leave_review_fragment_feedback_text, existingRating.feedback!!)
        assertProgress(R.id.leave_review_fragment_rating, existingRating.rate!!.toInt())

        onView(withId(R.id.leave_review_fragment_rating)).perform(SetRating(3.0f))
        clickOn(R.id.leave_review_fragment_save_button)

        assertNotExist(R.id.leave_review_dialog_fragment)
        assertNotEquals(existingRating.rate, 3.0f)
    }


    /**
     * Idea taken from StackOverflow
     * https://stackoverflow.com/questions/25209508/how-to-set-a-specific-rating-on-ratingbar-in-espresso/25226081
     */
    class SetRating(val newRating: Float) : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(RatingBar::class.java)
        }

        override fun getDescription(): String {
            return "Custom view action to set rating."
        }

        override fun perform(uiController: UiController?, view: View) {
            val ratingBar = view as RatingBar
            ratingBar.rating = newRating
        }
    }

    private fun goToEventActivityWithIntent() {
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

