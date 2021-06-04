package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.DatabaseHelper
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.adapter.ZoneItemAdapter


class ZoneManagementListActivity : AppCompatActivity() {
    companion object {
        val EXTRA_ID = "ZONEID"
        val NEW_ZONE = "-1"
        var zones = ObservableList<Zone>()

    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById(R.id.recycler_zones_list)


        val openZone = { zone: Zone ->
            startActivityZone(zone.zoneId!!)
        }

        // Build an alert dialog to warn the user that the action is not reverseible
        val deleteZone = { zone: Zone ->
            HelperFunctions.showAlertDialog(
                this, getString(R.string.message_confirm_delete_zone_title, zone.zoneName),
                getString(R.string.message_confirm_delete_zone),
                {
                    DatabaseHelper.deleteZone(zone)
                    zones.remove(zone)
                }
            )
        }

        recyclerView.adapter = ZoneItemAdapter(zones, openZone, deleteZone)

        getAllZones()

        // Notify the recycler view when an update is made on the zones
        zones.observe(this) { recyclerView.adapter!!.notifyDataSetChanged() }
        //Add zone to google map
        zones.observeAdd(this) { ZoneAreaMapHelper.importNewZone(this, it.value, false) }


        findViewById<ImageButton>(R.id.id_new_zone_button).setOnClickListener {
            startActivityZone(NEW_ZONE)
        }
        //remove the zone from google map on remove
        zones.observeRemove(this) {
            ZoneAreaMapHelper.removeZone(it.value.zoneId!!)
        }
    }

    override fun onResume() {
        super.onResume()
        getAllZones()
    }

    /**
     * Retrieve all zone from the database
     */
    private fun getAllZones() {
        val infoGotten = Observable<Boolean>()
        Database.currentDatabase.zoneDatabase.getAllZones(
            zones.sortAndLimitFrom(this) { it.zoneName }
        ).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast(getString(R.string.fail_to_get_list_zones), this)
                finish()
            }
        }.then.updateOnce(this, infoGotten)
        // Add a progress dialog to wait for the transaction with the database to be over
        HelperFunctions.showProgressDialog(this, listOf(infoGotten), supportFragmentManager)
    }

    /**
     * Start the zone management activity with a specific Zone Id
     */
    private fun startActivityZone(zoneId: String) {
        val intent = Intent(this, ZoneManagementActivity::class.java)
        ZoneManagementActivity.zone.location = ""
        intent.putExtra(EXTRA_ID, zoneId)
        startActivity(intent)
    }
}