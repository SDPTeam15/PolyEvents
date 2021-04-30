package com.github.sdpteam15.polyevents.view.activity.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdminAdapter
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserEntity
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

        val maps = ObservableMap<String, ObservableList<MaterialRequest>>()
        requests.group(this, maps) { it.userId }.then.map(this, userNames) {
            val tempUsers = Observable<UserEntity>()
            Database.currentDatabase.userDatabase!!.getUserInformation(tempUsers, it[0].userId)
            tempUsers.map(this) { it1 -> it1.name ?: "UNKNOWN" }.then
        }

        val tempItems = ObservableList<Pair<Item, Int>>()

        tempItems.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        //Wait until we have both requests accepted from the database to show the material requests
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestList(
            requests,
            object : Matcher {
                override fun match(collection: Query): Query {
                    return collection.orderBy(DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_TIME.value)
                }
            })
            .observeOnce(this) {
                Database.currentDatabase.itemDatabase!!.getItemsList(tempItems).observeOnce(this) {
                    recyclerView.adapter =
                        ItemRequestAdminAdapter(
                            this,
                            this,
                            requests,
                            userNames,
                            itemNames,
                            acceptMaterialRequest,
                            declineMaterialRequest
                        )
                }
            }

    }

    val acceptMaterialRequest = { request: MaterialRequest ->
        // TODO define what happens when we accept a material request
    }

    val declineMaterialRequest = { request: MaterialRequest ->
        // TODO define what happens when we accept a material request
    }

}