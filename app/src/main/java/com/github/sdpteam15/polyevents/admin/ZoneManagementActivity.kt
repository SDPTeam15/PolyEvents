package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.admin.ZoneManagementListActivity.Companion.EXTRA_ID
import com.github.sdpteam15.polyevents.admin.ZoneManagementListActivity.Companion.NEW_ZONE
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.fragments.MapsFragment
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Zone



class ZoneManagementActivity : AppCompatActivity() {
    companion object {
        val zoneObservable = Observable<Zone>()
        val zone = Zone(location = "")
        var zoneId = ""
        val PARAM_TO_MAP_FRAGMENT = "CURRENT_ZONE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val int = intent
        zoneId = int.getStringExtra(EXTRA_ID).toString()
        val btnManage = findViewById<Button>(R.id.btnManage)
        val btnManageCoor = findViewById<Button>(R.id.btnModifyZoneCoordinates)
        val btnDelete = findViewById<Button>(R.id.btnDeleteZoneCoordinates)
        val tvManage = findViewById<TextView>(R.id.tvManage)
        val etName = findViewById<EditText>(R.id.zoneManagementName)
        val etDesc = findViewById<EditText>(R.id.zoneManagementDescription)
        val etLoc = findViewById<EditText>(R.id.zoneManagementCoordinates)
        val mapFragment = MapsFragment()
        mapFragment.zone = zone
        //By default, set the button as if we create a new zone
        changeCoordinatesText(etLoc,btnManageCoor,btnDelete,"")




        zoneObservable.observe {
            findViewById<FrameLayout>(R.id.flMapEditZone).visibility= View.INVISIBLE
            val zoneInfo = it!!
            etName.setText(zoneInfo.zoneName)
            etDesc.setText(zoneInfo.description)
            changeCoordinatesText(etLoc, btnManageCoor,btnDelete,zoneInfo.location!!)
        }

        if (zoneId == NEW_ZONE) {
            //Create a new zone
            btnManage.text = this.getString(R.string.btn_create_zone_button_text)
            tvManage.text = this.getString(R.string.tv_create_zone_text)
            btnManage.setOnClickListener {
                createZone(etName, etDesc)
            }

        } else {
            //Manage an existing zone, get the information
            btnManage.text = this.getString(R.string.btn_update_zone_button_text)
            tvManage.text = this.getString(R.string.tv_update_zone_text)
            currentDatabase.getZoneInformation(zoneId, zoneObservable)

            btnManage.setOnClickListener {
                updateZoneInfo(etName, etDesc)
            }
        }

        btnDelete.setOnClickListener{
             zone.location =""
            changeCoordinatesText(etLoc,btnManageCoor,btnDelete, "")
        }

        btnManageCoor.setOnClickListener{
            findViewById<FrameLayout>(R.id.flMapEditZone).visibility= View.VISIBLE
            this.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.flMapEditZone, mapFragment as Fragment)
                commit()
            }
        }
    }

    private fun changeCoordinatesText(etLoc:EditText, btnManage:Button, btnDelete:Button, newText:String?){
        var text = ""
        if(newText == null || newText=="") {
            text= getString(R.string.zone_management_coordinates_not_set)
            btnManage.text = getString(R.string.btn_modify_coord_set_text)
            btnDelete.visibility = View.INVISIBLE
        }else{
            btnManage.text = getString(R.string.btn_modify_coord_set_text)
            btnDelete.visibility = View.VISIBLE
            text= getString(R.string.zone_management_coordinates_set)
        }



        etLoc.setText(text)
    }

    private fun createZone(etName: EditText, etDesc: EditText) {
        //Create a new zone based on the fields
        val name = etName.text.toString()
        val desc = etDesc.text.toString()
        val loc = " a"
        if (checkNotEmpty(name, loc, desc)) {
            zone.description = desc
            zone.location = loc
            zone.zoneName = name
            zone.zoneId = null

            currentDatabase.createZone(zone).observe {
                if (it!!) {
                    HelperFunctions.showToast(
                        this.getString(R.string.zone_added_successfully),
                        this
                    )
                    val int = Intent(this, ZoneManagementListActivity::class.java)
                    startActivity(int)
                } else {
                    HelperFunctions.showToast(this.getString(R.string.zone_add_fail), this)
                }
            }
        }
    }

    private fun updateZoneInfo(etName: EditText, etDesc: EditText) {
        //Update zone information based on the fields
        val name = etName.text.toString()
        val desc = etDesc.text.toString()
        val loc = " a"
        if (checkNotEmpty(name, loc, desc)) {
            zone.description = desc
            zone.location = loc
            zone.zoneName = name
            zone.zoneId = zoneId

            currentDatabase.updateZoneInformation(zoneId, zone).observe {
                if (it!!) {
                    HelperFunctions.showToast(
                        this.getString(R.string.zone_added_successfully),
                        this
                    )
                    val int = Intent(this, ZoneManagementListActivity::class.java)
                    startActivity(int)
                } else {
                    HelperFunctions.showToast(this.getString(R.string.zone_add_fail), this)
                }
            }
        }
    }

    private fun checkNotEmpty(name: String, loc: String, desc: String): Boolean {
        if (name == "" || desc == "" || loc == getString(R.string.zone_management_coordinates_not_set)) {
            HelperFunctions.showToast(this.getString(R.string.missing_field_zone_management), this)
            return false
        } else {
            return true
        }
    }
}