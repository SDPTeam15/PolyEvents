package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.exceptions.MaxAttendeesException
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.room.EventLocal
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory

/**
 * An activity containing events description
 */
class EventActivity : AppCompatActivity() {

    companion object {
        const val TAG = "EventActivity"

        // Refactored here for tests
        var obsEvent = Observable<Event>()
        lateinit var event: Event
    }

    private lateinit var subscribeButton: Button

    private val localEventViewModel: EventLocalViewModel by viewModels {
        EventLocalViewModelFactory((application as PolyEventsApplication).database.eventDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        subscribeButton = findViewById(R.id.button_subscribe_event)

        getEventAndObserve()

        val obs : ObservableList<EventLocal> = ObservableList()
        localEventViewModel.getAllEvents(obs)
        obs.observe(this) {
            Log.d(TAG, "Getting events!")
            Log.d(TAG, it.value.joinToString(separator = ","))
        }
    }

    override fun onResume() {
        super.onResume()

        // Get event again in case of changes
        getEventAndObserve()
    }

    private fun getEventAndObserve() {
        currentDatabase.eventDatabase!!.getEventFromId(intent.getStringExtra(EXTRA_EVENT_ID)!!, obsEvent)
            .observe(this) { b ->
                if (!b.value) {
                    showToast(getString(R.string.event_info_fail), this)
                }
            }
        obsEvent.observe(this) { updateInfo(it.value) }
    }
    /**
     * Updates the event information
     */
    private fun updateInfo(event: Event) {
        /*Log.d(TAG, "BEGIN INSERTING INTO ROOM DATABASE")
        eventViewModel.insert(
                EventLocal(
                        eventId = event.eventId!! + "2",
                        eventName = event.eventName,
                        organizer = event.organizer,
                        zoneName = event.zoneName,
                        description = event.description,
                        startTime = event.startTime,
                        tags = event.tags
                )
        ).invokeOnCompletion {
            Log.d(TAG, "INSERTED EVENT INTO ROOM DATBASE")
        }*/

        EventActivity.event = event
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
                && event.getParticipants().contains(currentDatabase.currentUser!!.uid)) {
                subscribeButton.setText(resources.getString(R.string.event_unsubscribe))
            }
        } else {
            subscribeButton.visibility = View.GONE
        }
    }

    fun onClickEventSubscribe(view: View) {
        if (currentDatabase.currentUser == null) {
            showToast(resources.getString(R.string.toast_subscribe_warning), this)
        } else if (event.getParticipants().contains(currentDatabase.currentUser!!.uid)){
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

}