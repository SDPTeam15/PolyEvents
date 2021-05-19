package com.github.sdpteam15.polyevents.view.activity.activityprovider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.sdpteam15.polyevents.R

class EventManagementActivityProvider : AppCompatActivity() {
    companion object {
        const val INTENT_MANAGER = "MANAGER"
        const val INTENT_MANAGER_EDIT = "EDIT_MANAGER"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management_provider)
    }
}