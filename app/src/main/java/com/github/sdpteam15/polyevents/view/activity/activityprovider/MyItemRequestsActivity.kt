package com.github.sdpteam15.polyevents.view.activity.activityprovider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
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
    private var currentStatus: MaterialRequest.Status = MaterialRequest.Status.PENDING

    private lateinit var recyclerView: RecyclerView
    private lateinit var title: TextView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var userId: String

    private val requests = ObservableList<MaterialRequest>()
    private val materialRequest = ObservableMap<MaterialRequest.Status, ObservableList<MaterialRequest>>()
    private val observableStatus = Observable(currentStatus)
    private var userName = Database.currentDatabase.currentUser!!.name
    private val itemNames = ObservableMap<String, String>()
    private val items = ObservableList<Triple<Item, Int, Int>>()

    private fun nextStatus(){
        currentStatus = when(currentStatus){
            MaterialRequest.Status.PENDING -> MaterialRequest.Status.ACCEPTED
            MaterialRequest.Status.ACCEPTED -> MaterialRequest.Status.REFUSED
            MaterialRequest.Status.REFUSED -> MaterialRequest.Status.PENDING
        }
        observableStatus.postValue(currentStatus)
    }

    private fun previousStatus(){
        currentStatus = when(currentStatus){
            MaterialRequest.Status.PENDING -> MaterialRequest.Status.REFUSED
            MaterialRequest.Status.REFUSED -> MaterialRequest.Status.ACCEPTED
            MaterialRequest.Status.ACCEPTED -> MaterialRequest.Status.PENDING
        }
        observableStatus.postValue(currentStatus)
    }

    private fun refresh(){
        title.text =  when(currentStatus){
            MaterialRequest.Status.PENDING -> getText(R.string.pending_requests)
            MaterialRequest.Status.REFUSED -> getText(R.string.refused_requests)
            MaterialRequest.Status.ACCEPTED -> getText(R.string.accepted_requests)
        }
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_item_requests)

        userId = intent.getStringExtra(ProviderHomeFragment.ID_USER)!!
        recyclerView = findViewById(R.id.id_recycler_my_item_requests)
        title = findViewById(R.id.id_title_item_request)
        leftButton = findViewById(R.id.id_change_request_status_left)
        rightButton = findViewById(R.id.id_change_request_status_right)

//-------------------------------------------------------------------------------------------

        leftButton.setOnClickListener {
            previousStatus()
        }
        rightButton.setOnClickListener {
            nextStatus()
        }
        observableStatus.observe(this){
            refresh()
        }

        recyclerView.adapter =
            MyItemRequestAdapter(
                this,
                this,
                materialRequest,
                observableStatus,
                userName,
                itemNames,
                acceptMaterialRequest,
                declineMaterialRequest
            )

        observableStatus.observe(this){recyclerView.adapter!!.notifyDataSetChanged()}

        items.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        requests.group(this, materialRequest){
            it.status
        }

        //Wait until we have both requests accepted from the database to show the material requests
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestListByUser(
            requests,
            userId).observeOnce(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to get the list of material requests", this)
                } else {
                    Database.currentDatabase.itemDatabase!!.getItemsList(items)
                        .observeOnce(this) { it2 ->
                            if (!it2.value) {
                                HelperFunctions.showToast("Failed to get the list of items", this)
                            }
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