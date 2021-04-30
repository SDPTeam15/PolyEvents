package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelper
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.adapter.ZoneItemAdapter
import com.google.firebase.firestore.Query


class ZoneManagementListActivity : AppCompatActivity() {
    companion object {
        val EXTRA_ID = "ZONEID"
        val NEW_ZONE = "-1"
        var zones = ObservableList<Zone>()

        fun deleteZone(zone: Zone) {
            Database.currentDatabase.zoneDatabase!!.deleteZone(zone, null)
        }
    }

    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById(R.id.recycler_zones_list)

        val openZone = { zone: Zone ->
            startActivityZone(zone.zoneId)
        }

        recyclerView.adapter = ZoneItemAdapter(zones, openZone)
        val matcher = object : Matcher {
            override fun match(collection: Query): Query {
                return collection.orderBy(DatabaseConstant.ZoneConstant.ZONE_NAME.value)
            }
        }

        println(Database.currentDatabase)
        Database.currentDatabase.zoneDatabase!!.getAllZones(matcher, 50, zones).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to get the list of zones", this)
            }
        }

        zones.observe(this) { recyclerView.adapter!!.notifyDataSetChanged() }
        zones.observeAdd(this) { GoogleMapHelper.importNewZone(this, it.value) }
        findViewById<Button>(R.id.btnNewZone).setOnClickListener {
            startActivityZone(NEW_ZONE)
        }
        zones.observeRemove(this) {
            GoogleMapHelper.removeZone(it.value.zoneId!!)
        }
    }

    fun startActivityZone(zoneId: String?) {
        val intent = Intent(this, ZoneManagementActivity::class.java)
        ZoneManagementActivity.zone.location = ""
        intent.putExtra(EXTRA_ID, zoneId ?: NEW_ZONE)
        startActivity(intent)
    }


}