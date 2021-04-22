package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.exceptions.MaxAttendeesException
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.Event

const val TAG = "EventActivity"

/**
 * An activity containing events description
 */
class EventActivity : AppCompatActivity() {

    private lateinit var subscribeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        subscribeButton = findViewById(R.id.button_subscribe_event)

        getEventAndObserve()
    }

    override fun onResume() {
        super.onResume()

        // Get event again in case of changes
        getEventAndObserve()
    }

    private fun getEventAndObserve() {
        Log.d(TAG, intent.getStringExtra(EXTRA_EVENT_ID)!!)
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

    fun unsubscribeFromEvent() {
        event.removeParticipant(currentDatabase.currentUser!!.uid)

        currentDatabase.eventDatabase!!.updateEvents(event)
        showToast(resources.getString(R.string.event_successfully_unsubscribed), this)

        subscribeButton.setText(resources.getString(R.string.event_subscribe))
    }

    fun subscribeToEvent() {
        try {
            event.addParticipant(currentDatabase.currentUser!!.uid)

            currentDatabase.eventDatabase!!.updateEvents(event)
            showToast(resources.getString(R.string.event_successfully_subscribed), this)
            subscribeButton.setText(resources.getString(R.string.event_unsubscribe))
        } catch (e: MaxAttendeesException) {
            showToast(resources.getString(R.string.event_subscribe_at_max_capacity), this)
        }
    }

    // Refactored here for tests
    companion object {
        var obsEvent = Observable<Event>()
        lateinit var event: Event
    }

}