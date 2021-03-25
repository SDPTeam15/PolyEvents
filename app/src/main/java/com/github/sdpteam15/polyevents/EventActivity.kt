package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.model.Event

/**
 * An activity containing events description
 */
class EventActivity : AppCompatActivity() {

    var obsEvent = Observable<Event>()

    override fun onResume() {
        super.onResume()
        obsEvent.observe { updateInfo(it!!) }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDatabase.getEventFromId(intent.getStringExtra(EXTRA_EVENT_ID)!!, obsEvent)
            .observe { b ->
                if (b!!) {
                    obsEvent.observe { updateInfo(it!!) }
                }
                setContentView(R.layout.activity_event)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }
    }

    /**
     * Updates the event information
     */
    private fun updateInfo(event: Event) {
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
    }


}