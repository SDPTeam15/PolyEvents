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
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.EventAttendee

const val TAG = "EventActivity"

/**
 * An activity containing events description
 */
class EventActivity : AppCompatActivity() {

    var obsEvent = Observable<Event>()
    lateinit var event: Event

    lateinit var subscribeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        subscribeButton = findViewById(R.id.button_subscribe_event)

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
        this.event = event
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

        if (event.limitedEvent) {
            // TODO: change button text if current user already subscribed?
            subscribeButton.visibility = View.VISIBLE
        }
    }

    fun subscribeToEvent(view: View) {
        if (currentDatabase.currentUser == null) {
            Log.d(TAG, "No user logged in")
            showToast(resources.getString(R.string.toast_subscribe_warning), this)
        } else {
            val eventAttendee = Observable<EventAttendee?>()
            currentDatabase.eventDatabase!!.getEventAttendeeByIds(eventId = event.eventId!!,
                userUid = currentDatabase.currentUser!!.uid,
                eventAttendee = eventAttendee
            )
            eventAttendee.observe {
                if (it.value != null) {
                    Log.d(TAG, "User already subscribed to event")
                    showToast("Already subscribed to this event", this)
                    subscribeButton.isEnabled = false
                } else {
                    currentDatabase.eventDatabase!!.addEventAttendee(
                        eventId = event.eventId!!,
                        userUid = currentDatabase.currentUser!!.uid
                    ).observe {
                        if (it.value) {
                            showToast("Successfully subscribed to this event", this)
                        } else {
                            showToast("Could not subscribe to this event", this)
                        }
                    }
                }
            }
        }
    }

}