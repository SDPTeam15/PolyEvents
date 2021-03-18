package com.github.sdpteam15.polyevents

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID

/**
 * An activity containing events description
 */
class EventActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        updateInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Updates the event information
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateInfo() {
        val id = intent.getStringExtra(EXTRA_EVENT_ID)
        val event = currentDatabase.getEventFromId(id!!)!!
        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.txt_event_Name).apply {
            text = event.name
        }
        findViewById<TextView>(R.id.txt_event_zone).apply {
            text = event.zone
        }
        findViewById<TextView>(R.id.txt_event_date).apply {
            text = event.getTime()
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