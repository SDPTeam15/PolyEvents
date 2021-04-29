package com.github.sdpteam15.polyevents.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.adapter.ItemRequestAdminAdapter
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.database.observe.ObservableMap
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.firebase.firestore.Query

/**
 * Activity to display item requests to Admins
 * TODO define what happens when an admin answers an item request
 */
class ItemRequestManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val requests = ObservableList<MaterialRequest>()
    private val userNames = ObservableMap<String, Observable<String>>()
    private val itemNames = ObservableMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById(R.id.id_recycler_item_requests)

        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestList(requests)
        requests.group(this) { it.userId }.then.map(this, userNames) {
            val s = Observable<UserEntity>()
            Database.currentDatabase.userDatabase!!.getUserInformation(s)
            s.map(this) { it1 -> it1.name ?: "UNKNOWN" }.then
        }

        val tempItems = ObservableList<Pair<Item, Int>>()

        tempItems.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        //Wait until we have both requests accepted from the database to show the material requests
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestList(requests)
            .observeOnce(this) {
                Database.currentDatabase.itemDatabase!!.getItemsList(tempItems).observeOnce(this) {
                    //  Database.currentDatabase.userDatabase!!.getProfilesUserList(tempUserList,Database.currentDatabase.currentProfile!!)
                    recyclerView.adapter =
                        ItemRequestAdminAdapter(this, this, requests, userNames, itemNames,acceptMaterialRequest,declineMaterialRequest)
                }
            }

    }

    val acceptMaterialRequest = { request : MaterialRequest->
        // TODO define what happens when we accept a material request
    }

    val declineMaterialRequest = {request : MaterialRequest ->
        // TODO define what happens when we accept a material request
    }

}