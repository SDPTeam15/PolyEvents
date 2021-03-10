package com.github.sdpteam15.polyevents

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.fragments.EXTRA_MESSAGE
import java.util.*

class ActivityActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val activity = Activity.instances.get(message)
        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.txt_activity_Name).apply {
            text = activity!!.name
        }
        findViewById<TextView>(R.id.txt_activity_zone).apply {
            text = activity!!.zone
        }
        findViewById<TextView>(R.id.txt_activity_date).apply {
            text = activity!!.getTime()
        }
        findViewById<TextView>(R.id.txt_activity_organizer).apply {
            text = activity!!.organizer
        }
        findViewById<TextView>(R.id.txt_activity_description).apply {
            text = activity!!.description
        }
        findViewById<TextView>(R.id.txt_activity_tags).apply {
            text = activity!!.tags.joinToString { s->s }
        }
        findViewById<ImageView>(R.id.img_activity_logo).apply{

        }
    }
}