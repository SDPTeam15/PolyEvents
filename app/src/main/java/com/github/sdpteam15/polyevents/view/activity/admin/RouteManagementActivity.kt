package com.github.sdpteam15.polyevents.view.activity.admin

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.view.fragments.MapsFragment

class RouteManagementActivity : AppCompatActivity() {
    companion object {
        var inTest = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //Get the views needed in the code
        val mapFragment = MapsFragment(MapsFragment.MapsFragmentMod.EditRoute)

        //display the FrameLayout that will contain the map fragment
        findViewById<FrameLayout>(R.id.flMapEditRoute).visibility = View.VISIBLE

        //TODO add the area to be modified (once the zone modifier is implemented)
        //disable the back button in the navigation bar to avoid confusion
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        //Avoid displaying the map in tests, this makes Cirrus crash
        if (!inTest)
            HelperFunctions.changeFragment(this, mapFragment, R.id.flMapEditRoute)
    }
}