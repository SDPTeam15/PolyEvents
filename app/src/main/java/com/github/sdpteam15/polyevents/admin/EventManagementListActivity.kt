package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R

class EventManagementListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.btnNewEvent).setOnClickListener {
            val intent = Intent(this, EventManagementActivity::class.java)
            startActivity(intent)
        }
    }
}