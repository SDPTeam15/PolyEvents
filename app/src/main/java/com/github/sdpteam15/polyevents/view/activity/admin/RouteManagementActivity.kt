package com.github.sdpteam15.polyevents.view.activity.admin

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.map.MapsFragmentMod
import com.github.sdpteam15.polyevents.view.fragments.MapsFragment

class RouteManagementActivity : AppCompatActivity() {
    companion object {
        var inTest = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        //Get the views needed in the code
        val mapFragment = MapsFragment(MapsFragmentMod.EditRoute)

        //display the FrameLayout that will contain the map fragment
        findViewById<FrameLayout>(R.id.id_fl_map_edit_route).visibility = View.VISIBLE

        //Avoid displaying the map in tests, this makes Cirrus crash
        if (!inTest)
            HelperFunctions.changeFragment(this, mapFragment, R.id.id_fl_map_edit_route)
    }
}