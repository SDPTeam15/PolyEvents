package com.github.sdpteam15.polyevents.view.activity.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R

class EventManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}