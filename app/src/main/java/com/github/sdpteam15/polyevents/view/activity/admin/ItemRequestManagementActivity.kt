package com.github.sdpteam15.polyevents.view.activity.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider.materialRequestDatabase
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdminAdapter

/**
 * Activity to display item requests to Admins
 * TODO define what happens when an admin answers an item request
 */
class ItemRequestManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val requests = ObservableList<MaterialRequest>()
    private val userNames = ObservableMap<String, String>()
    private val itemNames = ObservableMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_item_requests)


        requests.group(this) { it.userId }.then.observePut(this) {
            if (!userNames.containsKey(it.key)) {
                val tempUsers = Observable<UserEntity>()
                Database.currentDatabase.userDatabase!!.getUserInformation(tempUsers, it.key)
                    .observeOnce(this) { ans ->
                        if (ans.value) {
                            userNames[it.key] = tempUsers.value?.name ?: "ANONYMOUS"
                        } else {
                            HelperFunctions.showToast("Failed to get Username from database", this)
                        }
                    }
            }
        }


        val tempItems = ObservableList<Pair<Item, Int>>()

        tempItems.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }


        //Wait until we have both requests accepted from the database to show the material requests
        materialRequestDatabase!!.getMaterialRequestList(
            requests,
            { collection ->
                collection.orderBy(DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_STATUS.value)
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

    private val acceptMaterialRequest = { request: MaterialRequest ->
        request.status = MaterialRequest.Status.ACCEPTED
        materialRequestDatabase!!.updateMaterialRequest(
            request.requestId!!,
            request
        ).observeOnce(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to accept the request", this)
            } else {
                requests.set(
                    requests.indexOfFirst { it2 -> it2.requestId == request.requestId },
                    request,
                    this
                )
            }
        }
        Unit
    }

    private val declineMaterialRequest = { request: MaterialRequest ->
        request.status = MaterialRequest.Status.REFUSED
        materialRequestDatabase!!.updateMaterialRequest(
            request.requestId!!,
            request
        ).observeOnce(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to decline the request", this)
            } else {
                requests.set(
                    requests.indexOfFirst { it2 -> it2.requestId == request.requestId },
                    request,
                    this
                )
            }
        }
        Unit
    }

}