package com.github.sdpteam15.polyevents.view.activity.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.view.activity.admin.UserManagementListActivity.Companion.EXTRA_USER_ID
import com.github.sdpteam15.polyevents.view.fragments.ProfileFragment

/**
 * Activity to manage users to give them permissions
 */
class UserManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (!intent.hasExtra(EXTRA_USER_ID))
            onBackPressed()

        val profLog = ProfileFragment(intent.getStringExtra(EXTRA_USER_ID))
        HelperFunctions.changeFragment(this, profLog, R.id.fl_user_management_profile)
    }
}