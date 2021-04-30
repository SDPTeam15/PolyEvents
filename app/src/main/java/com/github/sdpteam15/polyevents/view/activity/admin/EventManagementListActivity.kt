package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R

class EventManagementListActivity : AppCompatActivity() {
    companion object{
        const val NEW_EVENT_ID = "-1"
        const val EVENT_ID_INTENT = "EVENT_ID"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.btnNewEvent).setOnClickListener {
            val intent = Intent(this, EventManagementActivity::class.java)
            intent.putExtra(EVENT_ID_INTENT, NEW_EVENT_ID)
            startActivity(intent)
        }
    }
}