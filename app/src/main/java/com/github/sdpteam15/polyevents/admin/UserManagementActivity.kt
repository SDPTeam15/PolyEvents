package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.EventActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Event

class UserManagementActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        recyclerView = findViewById(R.id.recycler_view_user)
        recyclerView.setHasFixedSize(false)
    }
}