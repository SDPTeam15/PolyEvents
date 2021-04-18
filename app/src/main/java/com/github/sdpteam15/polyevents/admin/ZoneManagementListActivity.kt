package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R


class ZoneManagementListActivity : AppCompatActivity() {
    companion object {
        val EXTRA_ID = "ZONEID"
        val NEW_ZONE = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        findViewById<Button>(R.id.btnNewZone).setOnClickListener {
            //redirect to ZoneManagementActivity on button click
            val intent = Intent(this, ZoneManagementActivity::class.java)
            ZoneManagementActivity.zone.location = ""
            intent.putExtra(EXTRA_ID, NEW_ZONE)
            startActivity(intent)
        }
    }
}