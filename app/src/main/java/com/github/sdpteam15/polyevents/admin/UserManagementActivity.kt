package com.github.sdpteam15.polyevents.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.sdpteam15.polyevents.R

class UserManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}