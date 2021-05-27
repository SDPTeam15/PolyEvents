package com.github.sdpteam15.polyevents.view.activity

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.*
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.exceptions.MaxAttendeesException
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.EventLocal
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.adapter.CommentItemAdapter
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_NAME
import com.github.sdpteam15.polyevents.view.fragments.LeaveEventReviewFragment
import com.github.sdpteam15.polyevents.view.service.AlarmReceiver
import com.github.sdpteam15.polyevents.view.service.ReviewHasChanged
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory
import kotlinx.coroutines.runBlocking

/**
 * An activity containing events description. Note that information about the event could be stored from the local
 * database instead if they are stored locally (like the case where you subscribe to or follow an
 * event), but we choose to fetch them remotely to reflect any changes that might be done on the event.
 */
class EventActivity : AppCompatActivity(), ReviewHasChanged {
    // TODO: view on map functionality?

    companion object {
        const val TAG = "EventActivity"

        // Refactored here for tests
        val obsEvent: Observable<Event> = Observable()
        val obsRating: Observable<Float> = Observable()
        val obsComments: ObservableList<Rating> = ObservableList()
        lateinit var event: Event

        // for testing purposes
        lateinit var database: LocalDatabase
    }

    private lateinit var eventId: String

    private lateinit var subscribeButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var leaveReviewDialogFragment: LeaveEventReviewFragment

    // Lazily initialized view model, instantiated only when accessed for the first time
    private val localEventViewModel: EventLocalViewModel by viewModels {
        EventLocalViewModelFactory(database.eventDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        eventId = intent.getStringExtra(EXTRA_EVENT_ID)!!

        database = (application as PolyEventsApplication).database

        subscribeButton = findViewById(R.id.button_subscribe_event)
        recyclerView = findViewById(R.id.id_recycler_comment_list)
        leaveReviewDialogFragment = LeaveEventReviewFragment(eventId, this)

        recyclerView.adapter = CommentItemAdapter(obsComments)
        recyclerView.setHasFixedSize(false)

        refreshEvent()
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
        getEventAndObserve()
        getEventRating()
        getCommentsAndObserve()
    }

    /**
     * Get the currentEvent from the local database
     */
    private fun fetchEventFromLocalDatabase() {
        val localEventObservable = ObservableList<EventLocal>()
        localEventObservable.observe (this) {
            if (it.value.isEmpty()) {
                subscribeButton.setText(R.string.event_follow)
                subscribeButton.setOnClickListener {
                    followEvent()
                }
                subscribeButton.visibility = View.VISIBLE
            } else {
                subscribeButton.setText(R.string.event_unfollow)
                subscribeButton.visibility = View.VISIBLE
                subscribeButton.setOnClickListener {
                    unFollowEvent()
                }
                subscribeButton.visibility = View.VISIBLE
            }
        }
        localEventViewModel.getEventById(eventId, localEventObservable)
    }

    /**
     * get the rating of the event
     */
    private fun getEventRating() {
        currentDatabase.eventDatabase!!.getMeanRatingForEvent(
                eventId,
                obsRating
        )
        obsRating.observeOnce(this, updateIfNotNull = false) { updateRating(it.value) }
    }

    /**
     * Get the comments of an event
     */
    private fun getCommentsAndObserve() {
        currentDatabase.eventDatabase!!.getRatingsForEvent(
                eventId,
                null,
                obsComments
        )
        obsComments.observeAdd(this) {
            //If the comment doesn't have a review, we don't want to display it
            if (it.value.feedback == "") {
                obsComments.remove(it.value)
            } else {
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * Get all the informations of the event
     */
    private fun getEventAndObserve() {
        currentDatabase.eventDatabase!!.getEventFromId(
                eventId,
                obsEvent
        )
                .observe(this) { b ->
                    if (!b.value) {
                        showToast(getString(R.string.event_info_fail), this)
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
        findViewById<TextView>(R.id.txt_event_organizer).apply {
            text = event.organizer
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

        if (event.isLimitedEvent()) {
            subscribeButton.visibility = View.VISIBLE
            if (currentDatabase.currentUser != null
                    && event.getParticipants().contains(currentDatabase.currentUser!!.uid)
            ) {
                subscribeButton.text = resources.getString(R.string.event_unsubscribe)
            }
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

        currentDatabase.eventDatabase!!.updateEvent(event).observeOnce(this) {
            if (it.value) {
                cancelNotification(eventId)
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

            currentDatabase.eventDatabase!!.updateEvent(event).observeOnce(this) {
                if (it.value) {
                    // Now store the event in local cache and set a notification for it
                    val newNotificationId = getNotificationId()
                    localEventViewModel.insert(EventLocal.fromEvent(event).also {
                        it.notificationId = newNotificationId
                    })
                    sendNotification(newNotificationId)
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
        val newNotificationId = getNotificationId()
        localEventViewModel.insert(EventLocal.fromEvent(event).also {
            it.notificationId = newNotificationId
        })
        sendNotification(newNotificationId)
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
        cancelNotification(eventId)
        showToast(resources.getString(R.string.event_successfully_unfollowed), this)
        subscribeButton.setText(R.string.event_follow)
        subscribeButton.setOnClickListener {
            followEvent()
        }
    }

    private fun cancelNotification(eventId: String) {
        val notificationManager = ContextCompat.getSystemService(
            application as PolyEventsApplication,
            NotificationManager::class.java
        ) as NotificationManager
        val localEventObservable = ObservableList<EventLocal>()
        localEventObservable.observe (this) {
            if (it.value.isNotEmpty()) {
                val eventRetrievedNotificationId = it.value[0].notificationId
                if (eventRetrievedNotificationId != null) {
                    notificationManager.cancelNotification(eventRetrievedNotificationId)
                    notificationManager.cancelNotification(-eventRetrievedNotificationId)
                }
            }
            localEventViewModel.delete(EventLocal.fromEvent(event))
        }
        localEventViewModel.getEventById(eventId, localEventObservable)
    }

    /**
     * Set the alarm at the event start time using the Alarm manager. The alarm manager will notify
     * the alarm receiver which is a broadcast receiver from inside and outside the app, and will
     * trigger a notification.
     * Note that we're setting two notifications one 15 min before the event starts and one
     * at the start of the event.
     */
    private fun sendNotification(notificationId: Int) {
        val app = (application as PolyEventsApplication)

        // Get the alarm manager from the system
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the alarm before the event start by 15 min
        val notifyIntent15MinBefore = Intent(app, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_EVENT_ID, eventId)
            // We put the negative notification id for the 15 min notification
            putExtra(EXTRA_NOTIFICATION_ID, -notificationId)
            val notificationMessage = getString(R.string.event_start_soon, event.eventName)
            putExtra(EXTRA_NOTIFICATION_MESSAGE, notificationMessage)
        }
        val notifyPendingIntent15MinBefore = PendingIntent.getBroadcast(
            app,
            REQUEST_CODE,
            notifyIntent15MinBefore,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            HelperFunctions.Converters.fromLocalDateTime(event.startTime!!.minusMinutes(15L))!!,
            notifyPendingIntent15MinBefore
        )

        // Set the second alarm at the event start time
        val notifyIntent = Intent(app, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_EVENT_ID, eventId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            val notificationMessage = getString(R.string.event_started, event.eventName)
            putExtra(EXTRA_NOTIFICATION_MESSAGE, notificationMessage)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            app,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            HelperFunctions.Converters.fromLocalDateTime(event.startTime)!!,
            notifyPendingIntent
        )
    }

    /**
     * Generate a new notification id, by getting the max out of all the current notifications ids
     * and incrementing it.
     */
    private fun getNotificationId(): Int = runBlocking {
        val eventNotificationsIds = database.eventDao().getAll().map { it.notificationId }
        val max = eventNotificationsIds.maxByOrNull {
            it ?: 0
        }  ?: 0
        max + 1
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

}