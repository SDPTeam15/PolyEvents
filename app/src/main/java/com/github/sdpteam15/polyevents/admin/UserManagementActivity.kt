package com.github.sdpteam15.polyevents.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R

class UserManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}