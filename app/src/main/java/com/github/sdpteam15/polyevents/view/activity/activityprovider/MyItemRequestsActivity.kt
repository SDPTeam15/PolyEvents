package com.github.sdpteam15.polyevents.view.activity.activityprovider

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.activity.ItemRequestActivity
import com.github.sdpteam15.polyevents.view.adapter.MyItemRequestAdapter
import com.github.sdpteam15.polyevents.view.fragments.home.ProviderHomeFragment

/**
 * Extra containing the event ID to show on the launched event page
 */
const val EXTRA_ITEM_REQUEST_ID = "com.github.sdpteam15.polyevents.requests.ITEM_REQUEST_ID"

class MyItemRequestsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var currentStatus: MaterialRequest.Status = MaterialRequest.Status.PENDING

    private lateinit var recyclerView: RecyclerView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var spinner: Spinner
    private lateinit var userId: String

    private val requests = ObservableList<MaterialRequest>()
    private val materialRequest =
        ObservableMap<MaterialRequest.Status, ObservableList<MaterialRequest>>()
    private val observableStatus = Observable(currentStatus)
    private var userName = Database.currentDatabase.currentUser!!.name
    private val itemNames = ObservableMap<String, String>()
    private val items = ObservableList<Triple<Item, Int, Int>>()
    private val statusNames = ArrayList<String>()

    /**
     * Select next status page
     */
    private fun nextStatus() {
        currentStatus = when (currentStatus) {
            MaterialRequest.Status.PENDING -> MaterialRequest.Status.ACCEPTED
            MaterialRequest.Status.ACCEPTED -> MaterialRequest.Status.REFUSED
            MaterialRequest.Status.REFUSED -> MaterialRequest.Status.PENDING
        }
        observableStatus.postValue(currentStatus)
    }

    /**
     * Select previous status page
     */
    private fun previousStatus() {
        currentStatus = when (currentStatus) {
            MaterialRequest.Status.PENDING -> MaterialRequest.Status.REFUSED
            MaterialRequest.Status.REFUSED -> MaterialRequest.Status.ACCEPTED
            MaterialRequest.Status.ACCEPTED -> MaterialRequest.Status.PENDING
        }
        observableStatus.postValue(currentStatus)
    }

    /**
     * Refreshes the view
     */
    private fun refresh() {
        spinner.setSelection(currentStatus.ordinal)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_item_requests)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        userId = intent.getStringExtra(ProviderHomeFragment.ID_USER)!!
        recyclerView = findViewById(R.id.id_recycler_my_item_requests)
        leftButton = findViewById(R.id.id_change_request_status_left)
        rightButton = findViewById(R.id.id_change_request_status_right)
        spinner = findViewById(R.id.id_title_item_request)
//-------------------------------------------------------------------------------------------
        //List of status
        statusNames.add(getString(R.string.pending_requests))
        statusNames.add(getString(R.string.accepted_requests))
        statusNames.add(getString(R.string.refused_requests))
        val adapter =
            ArrayAdapter(this, R.layout.spinner_dropdown_item, statusNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(currentStatus.ordinal)
        spinner.onItemSelectedListener = this

        leftButton.setOnClickListener {
            previousStatus()
        }
        rightButton.setOnClickListener {
            nextStatus()
        }
        observableStatus.observe(this) {
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
                modifyMaterialRequest,
                cancelMaterialRequest
            )

        observableStatus.observe(this) { recyclerView.adapter!!.notifyDataSetChanged() }

        items.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        requests.group(this, materialRequest) {
            it.status
        }
    }

    override fun onResume() {
        super.onResume()
        getItemRequestsFromDB()
    }

    /**
     * Gets the item request of the user and then gets the item list
     */
    private fun getItemRequestsFromDB(){
        //Gets the item request of the user and then gets the item list
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestListByUser(
            requests,
            userId
        ).observeOnce(this) {
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

    /**
     * Launches the activity to modify the item request
     */
    private val modifyMaterialRequest = { request: MaterialRequest ->

        val intent = Intent(this, ItemRequestActivity::class.java).apply {
            putExtra(EXTRA_ITEM_REQUEST_ID, request.requestId)
        }
        startActivity(intent)
    }

    /**
     * Deletes the item request
     */
    private val cancelMaterialRequest = { request: MaterialRequest ->
        val l =
            Database.currentDatabase.materialRequestDatabase!!.deleteMaterialRequest(request.requestId!!)
        l.observe(this) {
            if (it.value)
                requests.remove(request)
        }
        Unit
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        currentStatus = MaterialRequest.Status.fromOrdinal(p2)!!
        observableStatus.postValue(currentStatus)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}