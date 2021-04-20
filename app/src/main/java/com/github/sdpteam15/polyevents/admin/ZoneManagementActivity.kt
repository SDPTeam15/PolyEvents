package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.admin.ZoneManagementListActivity.Companion.EXTRA_ID
import com.github.sdpteam15.polyevents.admin.ZoneManagementListActivity.Companion.NEW_ZONE
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.fragments.MapsFragment
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Zone


class ZoneManagementActivity : AppCompatActivity() {
    companion object {
        var zoneObservable = Observable<Zone>()
        val zone = Zone(location = "")
        var zoneId = ""
        var inTest = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //Get the views needed in the code
        val int = intent
        zoneId = int.getStringExtra(EXTRA_ID)!!
        val btnManage = findViewById<Button>(R.id.btnManage)
        val btnManageCoor = findViewById<Button>(R.id.btnModifyZoneCoordinates)
        val btnDelete = findViewById<Button>(R.id.btnDeleteZoneCoordinates)
        val tvManage = findViewById<TextView>(R.id.tvManage)
        val etName = findViewById<EditText>(R.id.zoneManagementName)
        val etDesc = findViewById<EditText>(R.id.zoneManagementDescription)
        val etLoc = findViewById<EditText>(R.id.zoneManagementCoordinates)
        val mapFragment = MapsFragment()
        mapFragment.zone = zone
        zoneObservable = Observable()
        zoneObservable.observe(this) {
            //Reactive the back button and make the map fragment invisible
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            findViewById<FrameLayout>(R.id.flMapEditZone).visibility = View.INVISIBLE
            //Set the retrieve texts in the fields
            val zoneInfo = it!!
            etName.setText(zoneInfo.zoneName)
            etDesc.setText(zoneInfo.description)
            changeCoordinatesText(etLoc, btnManageCoor, btnDelete, zoneInfo.location)
        }

        if (zoneId == NEW_ZONE) {
            zoneId = GoogleMapHelper.uidZone++.toString()
            GoogleMapHelper.zonesToArea[zoneId.toInt()] = Pair(null, mutableListOf())
            // Create a new zone, setup the text of the button consequently
            changeCoordinatesText(etLoc, btnManageCoor, btnDelete, "")
            btnManage.text = this.getString(R.string.btn_create_zone_button_text)
            tvManage.text = this.getString(R.string.tv_create_zone_text)
            //Click on manage create a new zone
            btnManage.setOnClickListener {
                createZone(etName, etDesc, etLoc)
            }
        } else {
            // Manage an existing zone, setup the text of the button consequently
            changeCoordinatesText(etLoc, btnManageCoor, btnDelete, zoneId)
            btnManage.text = this.getString(R.string.btn_update_zone_button_text)
            tvManage.text = this.getString(R.string.tv_update_zone_text)

            // Get the zone information in the database
            currentDatabase.getZoneInformation(zoneId, zoneObservable)
            // Click on manage update the zone
            btnManage.setOnClickListener {
                updateZoneInfo(etName, etDesc, etLoc)
            }
        }
        GoogleMapHelper.editingZone = zoneId.toInt()
        setupListener(btnDelete, btnManageCoor, etLoc, etDesc, etName, mapFragment)
    }

    /**
     * Method that will set the listener on the buttons properly
     * @param btnDelete: The button handling coordinates deletion
     * @param btnManageCoor: The button handling coordinates update or set
     * @param etLoc: EditText containing information about the coordinates
     * @param etDesc: EditText containing information about the description
     * @param etName: EditText containing information about the name
     * @param mapFragment: The map fragment object
     */
    private fun setupListener(
        btnDelete: Button,
        btnManageCoor: Button,
        etLoc: EditText,
        etDesc: EditText,
        etName: EditText,
        mapFragment: MapsFragment
    ) {
        btnDelete.setOnClickListener {
            //reset the location field text
            zone.location = ""
            GoogleMapHelper.removeZoneAreas(GoogleMapHelper.editingZone)
            //Set the correct text and visibility on the buttons
            changeCoordinatesText(etLoc, btnManageCoor, btnDelete, "")
        }
        btnManageCoor.setOnClickListener {
            //display the FrameLayout that will contain the map fragment
            findViewById<FrameLayout>(R.id.flMapEditZone).visibility = View.VISIBLE

            //Set the currently set information
            zone.description = etDesc.text.toString()
            zone.zoneName = etName.text.toString()
            //TODO add the area to be modified (once the zone modifier is implemented)
            //disable the back button in the navigation bar to avoid confusion
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            //Avoid displaying the map in tests, this makes Cirrus crash
            if (!inTest)
                HelperFunctions.changeFragment(this, mapFragment, R.id.flMapEditZone)
        }
    }

    /**
     * Change the text of the coordinates  field
     * @param etLoc: editText containing the text about coordinates
     * @param btnManage: button handling update or create zone
     * @param btnDelete: The button handling coordinates deletion
     * @param locationText: The location text we should inspect
     */
    private fun changeCoordinatesText(
        etLoc: EditText,
        btnManage: Button,
        btnDelete: Button,
        locationText: String?
    ) {
        var text: String
        if (locationText == null || locationText == "") {
            //If the location is not currently set, delete button invisible and set the correct text
            text = getString(R.string.zone_management_coordinates_not_set)
            btnManage.text = getString(R.string.btn_modify_coord_set_text)
            btnDelete.visibility = View.INVISIBLE
        } else {
            //If the location is currently set, delete button visible and set the correct text
            btnManage.text = getString(R.string.btn_modify_coord_update_text)
            btnDelete.visibility = View.VISIBLE
            text = getString(R.string.zone_management_coordinates_set)
        }

        etLoc.setText(text)
    }

    /**
     * Handle the zone creation event
     * @param etName: EditText containing information about the name
     * @param etDesc: EditText containing information about the description
     * @param etLoc: EditText containing information about the coordinates
     */
    private fun createZone(etName: EditText, etDesc: EditText, etLoc: EditText) {
        //Create a new zone based on the fields
        val name = etName.text.toString()
        val desc = etDesc.text.toString()
        val loc = etLoc.text.toString()
        //check if the strings are all set properly
        if (checkNotEmpty(name, loc, desc)) {
            //set the correct information
            zone.description = desc
            zone.zoneName = name
            //zoneId is null to create a new Area
            zone.zoneId = null
            currentDatabase.createZone(zone).observe {
                callbackHandler(
                    it,
                    this.getString(R.string.zone_added_successfully),
                    this.getString(R.string.zone_add_fail)
                )
            }
        }
    }

    /**
     * Handle the zone update event
     * @param etName: EditText containing information about the name
     * @param etDesc: EditText containing information about the description
     * @param etLoc: EditText containing information about the coordinates
     */
    private fun updateZoneInfo(etName: EditText, etDesc: EditText, etLoc: EditText) {
        //Update zone information based on the fields
        val name = etName.text.toString()
        val desc = etDesc.text.toString()
        val loc = etLoc.text.toString()
        if (checkNotEmpty(name, loc, desc)) {
            //set the correct information
            zone.description = desc
            zone.zoneName = name
            zone.zoneId = zoneId

            currentDatabase.updateZoneInformation(zoneId, zone).observe {
                callbackHandler(
                    it,
                    this.getString(R.string.zone_updated_successfully),
                    this.getString(R.string.zone_update_fail)
                )
            }
        }
    }

    /**
     * This method handle the callback from the creation and update method of the database
     * @param it: The return value from the database
     * @param succMess: the message to display in case of success
     * @param failMess: The message to display in case of failure
     */
    private fun callbackHandler(it: Boolean?, succMess: String, failMess: String) {
        if (it!!) {
            //Show a toast indicating that the area was successfully created and redirect to the correct activity
            HelperFunctions.showToast(
                succMess,
                this
            )
            val int = Intent(this, ZoneManagementListActivity::class.java)
            GoogleMapHelper.setZone(zone)
            startActivity(int)
        } else {
            //show a toast indicating that there was an error and stay on this activity
            HelperFunctions.showToast(failMess, this)
        }
    }

    /**
     * @param name : the name entered in the zoneName field
     * @param loc : text in the zoneCoord text
     * @param desc : the description entered in the zoneDesc field
     * @return true if the string are correctly set, false otherwise
     */
    private fun checkNotEmpty(name: String, loc: String?, desc: String): Boolean {
        if (name == "" || desc == "" || loc == getString(R.string.zone_management_coordinates_not_set)) {
            //Show a small message that invite the user to try again
            HelperFunctions.showToast(this.getString(R.string.missing_field_zone_management), this)
            return false
        }
        return true
    }
}