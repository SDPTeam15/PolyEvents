package com.github.sdpteam15.polyevents.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.Zone


class ZoneManagementActivity : AppCompatActivity() {
    companion object {
        val zoneObservable = Observable<Zone>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val int = intent
        val zoneId = int.getStringExtra(EXTRA_ID)
        val btnManage = findViewById<Button>(R.id.btnManage)

        if(zoneId.equals("0")){
            //Create a new zone
            btnManage.text = this.getString(R.string.btn_create_zone_button_text)
            btnManage.setOnClickListener {
                //Create a new zone based on the field
                val newZone = Zone(zoneId,"","","")
                currentDatabase.createZone(newZone)
            }

        }else{
            //Manage an existing zone, get the information
            btnManage.text = this.getString(R.string.btn_update_zone_button_text)
            currentDatabase.getZoneInformation(zoneId!!, zoneObservable).observe {
                //Update the fields in the activity

            }

            btnManage.setOnClickListener {
                //Create a new zone based on the field
                val newZone = Zone(zoneId,"","","")
                currentDatabase.updateZoneInformation(zoneId,newZone)
            }
        }

    }
}