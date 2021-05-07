package com.github.sdpteam15.polyevents.view.activity

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.exceptions.MaxAttendeesException
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.room.EventLocal
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.view.fragments.LeaveEventReviewFragment
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory

/**
 * An activity containing events description
 */
class EventActivity : AppCompatActivity() {
    // TODO: view on map functionality?

    companion object {
        const val TAG = "EventActivity"

        // Refactored here for tests
        val obsEvent: Observable<Event> = Observable()
        lateinit var event: Event

        // for testing purposes
        lateinit var database: LocalDatabase
    }

    private lateinit var subscribeButton: Button

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

        database = (application as PolyEventsApplication).database

        subscribeButton = findViewById(R.id.button_subscribe_event)

        leaveReviewDialogFragment = LeaveEventReviewFragment()

        getEventAndObserve()
    }

    override fun onResume() {
        super.onResume()

        // Get event again in case of changes
        getEventAndObserve()
    }

    private fun getEventAndObserve() {
        currentDatabase.eventDatabase!!.getEventFromId(
            intent.getStringExtra(EXTRA_EVENT_ID)!!,
            obsEvent
        )
            .observe(this) { b ->
                if (!b.value) {
                    showToast(getString(R.string.event_info_fail), this)
                }
            }
        obsEvent.observeOnce(this, updateIfNotNull = false){ updateInfo(it.value) }
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
            subscribeButton.visibility = View.GONE
        }
    }

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
        currentDatabase.eventDatabase!!.updateEvents(event)

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
            currentDatabase.eventDatabase!!.updateEvents(event)
            showToast(resources.getString(R.string.event_successfully_subscribed), this)
            subscribeButton.setText(resources.getString(R.string.event_unsubscribe))
        } catch (e: MaxAttendeesException) {
            showToast(resources.getString(R.string.event_subscribe_at_max_capacity), this)
        }
    }

    /**
     * Show the leave review dialog upon click
     */
    fun onClickEventLeaveReview(view: View) {
        leaveReviewDialogFragment.show(
               supportFragmentManager, LeaveEventReviewFragment.TAG
        )
    }

}