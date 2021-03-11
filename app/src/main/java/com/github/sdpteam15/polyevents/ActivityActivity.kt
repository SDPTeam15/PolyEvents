package com.github.sdpteam15.polyevents

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.fragments.EXTRA_ACTIVITY
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelper
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelperInterface

class ActivityActivity : AppCompatActivity() {

    var currentQueryHelper: ActivitiesQueryHelperInterface = ActivitiesQueryHelper
        @RequiresApi(Build.VERSION_CODES.O)
        set(value) {
            field = value
            writeInfos()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        writeInfos()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun writeInfos() {
        val id = intent.getStringExtra(EXTRA_ACTIVITY)
        val activity = currentQueryHelper.getActivityFromId(id!!)
        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.txt_activity_Name).apply {
            text = activity.name
        }
        findViewById<TextView>(R.id.txt_activity_zone).apply {
            text = activity.zone
        }
        findViewById<TextView>(R.id.txt_activity_date).apply {
            text = activity.getTime()
        }
        findViewById<TextView>(R.id.txt_activity_organizer).apply {
            text = activity.organizer
        }
        findViewById<TextView>(R.id.txt_activity_description).apply {
            text = activity.description
        }
        findViewById<TextView>(R.id.txt_activity_tags).apply {
            text = activity.tags.joinToString { s -> s }
        }
        findViewById<ImageView>(R.id.img_activity_logo).apply {
            //TODO : change image
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)

    }

}