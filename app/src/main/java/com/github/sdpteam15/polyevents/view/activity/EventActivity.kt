package com.github.sdpteam15.polyevents.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showProgressDialog
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.helper.NotificationsHelper
import com.github.sdpteam15.polyevents.helper.NotificationsScheduler
import com.github.sdpteam15.polyevents.model.callback.ReviewHasChanged
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.exceptions.MaxAttendeesException
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.adapter.CommentItemAdapter
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.view.fragments.LeaveEventReviewFragment
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime

/**
 * An activity containing events description. Note that information about the event could be stored from the local
 * database instead if they are stored locally (like the case where you subscribe to or follow an
 * event), but we choose to fetch them remotely to reflect any changes that might be done on the event.
 */
class EventActivity : AppCompatActivity(), ReviewHasChanged {
    // TODO: view on map functionality?
    private lateinit var eventId: String

    private lateinit var subscribeButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var leaveReviewDialogFragment: LeaveEventReviewFragment
    private lateinit var leaveReviewButton: Button

    // Lazily initialized view models, instantiated only when accessed for the first time
    private val localEventViewModel: EventLocalViewModel by viewModels {
        EventLocalViewModelFactory(database.eventDao())
    }

    var eventFetchDoneObservable: Observable<Boolean> = Observable()
    var eventRatingFetchDoneObservable: Observable<Boolean> = Observable()
    var eventCommentsFetchDoneObservable: Observable<Boolean> = Observable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)



        eventId = intent.getStringExtra(EXTRA_EVENT_ID)!!

        database = (application as PolyEventsApplication).localDatabase

        notificationsScheduler = NotificationsHelper(applicationContext)

        subscribeButton = findViewById(R.id.button_subscribe_follow_event)
        recyclerView = findViewById(R.id.id_recycler_comment_list)
        leaveReviewDialogFragment = LeaveEventReviewFragment(eventId, this)
        leaveReviewButton = findViewById(R.id.event_leave_review_button)

        recyclerView.adapter = CommentItemAdapter(obsNonEmptyComments)
        recyclerView.setHasFixedSize(false)

        setObservers()
        refreshEvent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onResume() {
        super.onResume()

        // Get event again in case of changes
        refreshEvent()
    }

    /**
     * Refreshes the activity
     */
    private fun refreshEvent() {
        obsComments.clear()
        obsNonEmptyComments.clear()
        eventFetchDoneObservable= Observable()
        eventRatingFetchDoneObservable= Observable()
        eventCommentsFetchDoneObservable= Observable()
        // Display a loading screen while the queries with the database are not over
        showProgressDialog(
            this, listOf(
                eventFetchDoneObservable,
                eventRatingFetchDoneObservable,
                eventCommentsFetchDoneObservable
            ), supportFragmentManager
        )

        getEventAndObserve()
        getEventRating()
        getCommentsAndObserve()
        getUserAndObserve()
    }

    private fun getUserAndObserve() {
        obsOrganiser.observe(this) {
            findViewById<TextView>(R.id.txt_event_organizer).apply {
                text = it.value.name
            }
        }
    }

    /**
     * Get the currentEvent from the local database
     */
    private fun fetchEventFromLocalDatabase() {
        val localEventObservable = ObservableList<EventLocal>()
        localEventObservable.observe(this) {
            if (it.value.isEmpty()) {
                subscribeButton.setText(R.string.event_follow)
                subscribeButton.setOnClickListener {
                    followEvent()
                }
                subscribeButton.isEnabled = true
            } else {
                subscribeButton.setText(R.string.event_unfollow)
                subscribeButton.setOnClickListener {
                    unFollowEvent()
                }
                subscribeButton.isEnabled = true
            }
        }
        localEventViewModel.getEventById(eventId, localEventObservable)
    }

    /**
     * get the rating of the event
     */
    private fun getEventRating() {
        currentDatabase.eventDatabase.getMeanRatingForEvent(
            eventId,
            obsRating
        ).updateOnce(this, eventRatingFetchDoneObservable)
        obsRating.observeOnce(this, updateIfNotNull = false) { updateRating(it.value) }
    }

    /**
     * Sets the observe add and modify of the observable list of reviews
     */
    private fun setObservers() {
        obsComments.observeAdd(this) {
            //If the comment doesn't have a review, we don't want to display it
            if (it.value.feedback != "") {
                obsNonEmptyComments.add(it.value)
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }
        obsComments.observeRemove(this) {
            //If the comment doesn't have a review, he isn't displayed so no need to remove it and refresh
            if (it.value.feedback != "") {
                obsNonEmptyComments.remove(it.value)
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }
        obsComments.observe(this) {
            updateNumberReviews()
        }
        obsNonEmptyComments.observe(this) {
            updateNumberComments()
        }
    }

    /**
     * Get the comments of an event
     */
    private fun getCommentsAndObserve() {
        currentDatabase.eventDatabase.getRatingsForEvent(
            eventId,
            null,
            obsComments
        ).updateOnce(this, eventCommentsFetchDoneObservable)
    }

    /**
     * Updates the number of reviews on the xml
     */
    private fun updateNumberReviews() {
        if (!PolyEventsApplication.inTest) {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.id_number_reviews).text = obsComments.size.toString()
            }
        }
    }

    /**
     * Updates the number of comments on the xml
     */
    private fun updateNumberComments() {
        if (!PolyEventsApplication.inTest) {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.id_number_comments).text =
                    obsNonEmptyComments.size.toString()
            }
        }
    }

    /**
     * Get all the informations of the event
     */
    private fun getEventAndObserve() {
        currentDatabase.eventDatabase.getEventFromId(
            eventId,
            obsEvent
        ).updateOnce(this, eventFetchDoneObservable)
        eventFetchDoneObservable.observe(this) { b ->
            /*eventFetchDone = true
            checkFetchDataDone()*/
            if (!b.value) {
                showToast(getString(R.string.event_info_fail), this)
            } else {
                if (obsEvent.value!!.organizer != null) {
                    currentDatabase.userDatabase.getUserInformation(
                        obsOrganiser,
                        obsEvent.value!!.organizer!!
                    ).observeOnce(this) {
                        if (!b.value) {
                            showToast(getString(R.string.event_info_fail), this)
                        }
                    }
                }
            }
        }
        obsEvent.observeOnce(this, updateIfNotNull = false) { updateInfo(it.value) }
    }

    /**
     * Updates the rating of the event
     * @param rating rating of the event
     */
    private fun updateRating(rating: Float) {
        findViewById<RatingBar>(R.id.ratingBar_event).apply {
            setRating(rating)
        }
    }

    /**
     * Updates the event information
     */
    private fun updateInfo(event: Event) {
        Companion.event = event
        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.txt_event_Name).apply {
            text = event.eventName
        }
        findViewById<TextView>(R.id.txt_event_zone).apply {
            text = event.zoneName
        }
        findViewById<TextView>(R.id.txt_event_date).apply {
            text = event.formattedStartTime()
        }

        findViewById<TextView>(R.id.txt_event_description).apply {
            text = event.description
        }
        findViewById<TextView>(R.id.txt_event_tags).apply {
            text = event.tags.joinToString { s -> s }
        }
        findViewById<ImageView>(R.id.img_event_logo).apply {
            //TODO : change image
        }

        leaveReviewButton.isEnabled = true

        if (event.isLimitedEvent()) {
            if (currentDatabase.currentUser != null
                && event.getParticipants().contains(currentDatabase.currentUser!!.uid)
            ) {
                subscribeButton.text = resources.getString(R.string.event_unsubscribe)
            } else {
                subscribeButton.text = resources.getString(R.string.event_subscribe)
            }
            subscribeButton.isEnabled = true
        } else {
            // Check if we're following the event
            fetchEventFromLocalDatabase()
        }
    }

    /**
     * Launches the popup to leave a review
     */
    @Suppress("UNUSED_PARAMETER")
    fun onClickEventSubscribe(view: View) {
        if (currentDatabase.currentUser == null) {
            showToast(resources.getString(R.string.toast_subscribe_warning), this)
        } else if (event.getParticipants().contains(currentDatabase.currentUser!!.uid)) {
            unsubscribeFromEvent()
        } else {
            subscribeToEvent()
        }
    }

    /**
     * Unsubscribe user from the event. The updates are done on firestore for the current event,
     * and deletes the event from the local database.
     */
    private fun unsubscribeFromEvent() {
        event.removeParticipant(currentDatabase.currentUser!!.uid)

        currentDatabase.eventDatabase.updateEvent(event).observeOnce(this) {
            if (it.value) {
                cancelNotifications()
                showToast(resources.getString(R.string.event_successfully_unsubscribed), this)

                subscribeButton.text = resources.getString(R.string.event_subscribe)
            } else {
                event.addParticipant(currentDatabase.currentUser!!.uid)
                showToast(resources.getString(R.string.event_unsubscribe_fail), this)
            }
        }
    }

    /**
     * Subscribe user to the event if event still has free slots. Updates the event remotely on firestore
     * as well as inserts the event in the local room database.
     */
    private fun subscribeToEvent() {
        try {
            event.addParticipant(currentDatabase.currentUser!!.uid)

            currentDatabase.eventDatabase.updateEvent(event).observeOnce(this) {
                if (it.value) {
                    scheduleNotificationsAndSaveEvent()
                    showToast(resources.getString(R.string.event_successfully_subscribed), this)
                    subscribeButton.text = resources.getString(R.string.event_unsubscribe)
                } else {
                    event.removeParticipant(currentDatabase.currentUser!!.uid)
                    showToast(resources.getString(R.string.event_subscription_fail), this)
                }
            }
        } catch (e: MaxAttendeesException) {
            showToast(resources.getString(R.string.event_subscribe_at_max_capacity), this)
        }
    }

    /**
     * Follow an event. The event is added to the local cache and a notification for it is fired
     * at the start of the event.
     */
    private fun followEvent() {
        scheduleNotificationsAndSaveEvent()
        subscribeButton.setText(R.string.event_unfollow)
        subscribeButton.setOnClickListener {
            unFollowEvent()
        }
        showToast(resources.getString(R.string.event_successfully_followed), this)
    }

    /**
     * Unfollow an event. The event will be removed from the local database.
     * The notification for it will be cancelled as well
     */
    private fun unFollowEvent() {
        // First remove the notifications set to trigger for this event when we follow it
        cancelNotifications()
        showToast(resources.getString(R.string.event_successfully_unfollowed), this)
        subscribeButton.setText(R.string.event_follow)
        subscribeButton.setOnClickListener {
            followEvent()
        }
    }

    /**
     * Cancel all notifications associated to this event. First we retrieve the event from
     * the local cache to retrieve the notifications ids associated to this event, if any, and use
     * them to cancel the notifications scheduled for this event
     */
    private fun cancelNotifications() {
        val localEventObservable = ObservableList<EventLocal>()
        localEventObservable.observe(this) {
            if (it.value.isNotEmpty()) {
                val eventRetrieved = it.value[0]
                val eventBeforeNotificationId = eventRetrieved.eventBeforeNotificationId
                // Cancel the notification set before the event
                if (eventBeforeNotificationId != null) {
                    notificationsScheduler.cancelNotification(
                        eventBeforeNotificationId
                    )
                }

                val eventStartNotificationId = eventRetrieved.eventStartNotificationId
                // Cancel the notification set at the start of the event
                if (eventStartNotificationId != null) {
                    notificationsScheduler.cancelNotification(
                        eventStartNotificationId
                    )
                }
                // Now remove event from local cache since we're not following it anymore
                localEventViewModel.delete(EventLocal.fromEvent(event))
            }
        }
        localEventViewModel.getEventById(eventId, localEventObservable)
    }

    /**
     * Schedule notifications for this event
     * Note that we're setting two notifications one 15 min before the event starts and one
     * at the start of the event.
     */
    private fun scheduleNotificationsAndSaveEvent() {
        val eventLocalInstance = EventLocal.fromEvent(event)
        val (eventBeforeNotificationId, eventStartNotificationId) =
            scheduleNotificationWithRespectToCurrentTime(
                event = event,
                currentTime = LocalDateTime.now(),
                beforeEventNotificationMessage = getString(
                    R.string.event_start_soon,
                    event.eventName
                ),
                startTimeNotificationMessage = getString(R.string.event_started, event.eventName)
            )
        // Save the event in the local cache, along its associated notifications ids, to easily
        // retrieve these notifications later (to cancel for example)
        eventLocalInstance.eventBeforeNotificationId = eventBeforeNotificationId
        eventLocalInstance.eventStartNotificationId = eventStartNotificationId
        localEventViewModel.insert(eventLocalInstance)
    }

    /**
     * Show the leave review dialog upon click, if user is logged in.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onClickEventLeaveReview(view: View) {
        if (currentDatabase.currentUser == null) {
            showToast(getString(R.string.event_review_warning), this)
        } else {
            leaveReviewDialogFragment.show(
                supportFragmentManager, LeaveEventReviewFragment.TAG
            )
        }
    }

    override fun onLeaveReview() {
        refreshEvent()
    }


    companion object {
        const val TAG = "EventActivity"

        // Refactored here for tests
        val obsEvent: Observable<Event> = Observable()
        val obsRating: Observable<Float> = Observable()
        val obsOrganiser: Observable<UserEntity> = Observable()
        val obsComments: ObservableList<Rating> = ObservableList()
        val obsNonEmptyComments: ObservableList<Rating> = ObservableList()
        lateinit var event: Event

        // Keep an instance for testing purposes
        lateinit var database: LocalDatabase

        // Keep an instance for testing purposes
        lateinit var notificationsScheduler: NotificationsScheduler

        /**
         * Helper function to schedule notifications for an event at a certain time based on the current time
         * If the event end time has already passed, no notifications are scheduled. Otherwise if
         * the event has already started, schedule one notification. Otherwise two notifications, one
         * before and another after the event.
         * @param event the event we're scheduling notifications for
         * @param currentTime the current time from which we're scheduling notifications for this event
         * @param beforeEventNotificationMessage the message to be displayed in the notification popped
         * shortly before the start of the event
         * @param startTimeNotificationMessage the message to be displayed in the notification popped
         * at the start of the event
         * @return Pair with first the notification id (if not null) set before the start of the event
         * and the other (if not null) at the start of the event
         */
        fun scheduleNotificationWithRespectToCurrentTime(
            event: Event,
            currentTime: LocalDateTime,
            beforeEventNotificationMessage: String,
            startTimeNotificationMessage: String
        ): Pair<Int?, Int?> {
            var event15MinBeforeNotificationId: Int? = null
            var eventStartNotificationId: Int? = null
            // Check first if event has already ended, thus don't send notifications
            if (!currentTime.isAfter(event.endTime)) {

                // Check if event hasn't already started, schedule a notification 15 min before the start of the event
                if (currentTime.isBefore(event.startTime)) {
                    val event15MinBeforeNotificationMessage =
                        beforeEventNotificationMessage
                    event15MinBeforeNotificationId =
                        notificationsScheduler.scheduleEventNotification(
                            eventId = event.eventId!!,
                            notificationMessage = event15MinBeforeNotificationMessage,
                            scheduledTime = event.startTime!!.minusMinutes(15L)
                        )
                }

                // Schedule a notification at the start of the event
                val eventStartNotificationMessage =
                    startTimeNotificationMessage
                eventStartNotificationId = notificationsScheduler.scheduleEventNotification(
                    eventId = event.eventId!!,
                    notificationMessage = eventStartNotificationMessage,
                    scheduledTime = event.startTime!!
                )
            }
            return Pair(event15MinBeforeNotificationId, eventStartNotificationId)
        }

    }

}