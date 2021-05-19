package com.github.sdpteam15.polyevents.view.activity.activityprovider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdminAdapter
import com.github.sdpteam15.polyevents.view.adapter.MyItemRequestAdapter
import com.github.sdpteam15.polyevents.view.fragments.home.ProviderHomeFragment

class MyItemRequestsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var title: TextView
    private lateinit var userId: String

    private val requests = ObservableList<MaterialRequest>()
    private val userNames = ObservableMap<String, String>()
    private val itemNames = ObservableMap<String, String>()
    private val items = ObservableList<Triple<Item, Int, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_item_requests)

        userId = intent.getStringExtra(ProviderHomeFragment.ID_USER)!!
        recyclerView = findViewById(R.id.id_recycler_my_item_requests)
        title = findViewById(R.id.id_title_item_request)
//-------------------------------------------------------------------------------------------


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


        items.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }


        //Wait until we have both requests accepted from the database to show the material requests
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestListByUser(
            requests,
            userId,
            { collection ->
                collection.orderBy(DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_STATUS.value)
            })
            .observeOnce(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to get the list of material requests", this)
                } else {
                    Database.currentDatabase.itemDatabase!!.getItemsList(items)
                        .observeOnce(this) { it2 ->
                            if (!it2.value) {
                                HelperFunctions.showToast("Failed to get the list of items", this)
                            }
                            recyclerView.adapter =
                                MyItemRequestAdapter(
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
    }

    private val acceptMaterialRequest = {
            request: MaterialRequest ->
    }
    private val declineMaterialRequest = {
            request: MaterialRequest ->
    }
}