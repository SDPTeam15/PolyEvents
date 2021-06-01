package com.github.sdpteam15.polyevents.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
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
import com.github.sdpteam15.polyevents.view.fragments.LeaveEventReviewFragment
import com.github.sdpteam15.polyevents.view.service.ReviewHasChanged
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory

/**
 * An activity containing events description
 */
class EventActivity : AppCompatActivity(), ReviewHasChanged {
    // TODO: view on map functionality?

    companion object {
        const val TAG = "EventActivity"

        // Refactored here for tests
        val obsEvent: Observable<Event> = Observable()
        val obsRating: Observable<Float> = Observable()
        val obsComments: ObservableList<Rating> = ObservableList()
        val obsNonEmptyComments: ObservableList<Rating> = ObservableList()
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

        recyclerView.adapter = CommentItemAdapter(obsNonEmptyComments)
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
     * get the rating of the event
     */
    private fun getEventRating() {
        currentDatabase.eventDatabase!!.getMeanRatingForEvent(
                eventId,
                obsRating
        )
        obsRating.observeOnce(this, updateIfNotNull = false) {
            updateRating(it.value)
        }
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
            Log.d(
                "ADD",
                "SIZE IS ${obsComments.size}"
            )
            if (it.value.feedback != "") {
                obsNonEmptyComments.add(it.value)
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }
        obsComments.observeRemove(this) {
            Log.d(
                "REMOVE",
                "SIZE IS ${obsComments.size}"
            )
        }

        obsComments.observe(this){
            updateNumberReviews()
        }
        obsNonEmptyComments.observe(this){
            updateNumberComments()
        }
    }

    private fun updateNumberReviews(){
        findViewById<TextView>(R.id.id_number_reviews).apply {
            setText(obsComments.size.toString())
        }
    }

    private fun updateNumberComments(){
        findViewById<TextView>(R.id.id_number_comments).apply {
            setText(obsNonEmptyComments.size.toString())
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
            subscribeButton.visibility = View.INVISIBLE
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

        localEventViewModel.delete(EventLocal.fromEvent(event))
        currentDatabase.eventDatabase!!.updateEvent(event)

        showToast(resources.getString(R.string.event_successfully_unsubscribed), this)

        subscribeButton.setText(resources.getString(R.string.event_subscribe))
    }

    /**
     * Subscribe user to the event if event still has free slots. Updates the event remotely on firestore
     * as well as inserts the event in the local room database.
     */
    private fun subscribeToEvent() {
        try {
            event.addParticipant(currentDatabase.currentUser!!.uid)

            localEventViewModel.insert(EventLocal.fromEvent(event))
            currentDatabase.eventDatabase!!.updateEvent(event)
            showToast(resources.getString(R.string.event_successfully_subscribed), this)
            subscribeButton.setText(resources.getString(R.string.event_unsubscribe))
        } catch (e: MaxAttendeesException) {
            showToast(resources.getString(R.string.event_subscribe_at_max_capacity), this)
        }
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