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
import com.github.sdpteam15.polyevents.helper.DatabaseHelper.addToUsersFromDB
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest.Status.*
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.adapter.MyItemRequestAdapter
import com.github.sdpteam15.polyevents.view.fragments.home.ProviderHomeFragment

/**
 * Extra containing the event ID to show on the launched event page
 */
const val EXTRA_ITEM_REQUEST_ID = "com.github.sdpteam15.polyevents.requests.ITEM_REQUEST_ID"

class MyItemRequestsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var currentStatus: MaterialRequest.Status = PENDING

    private lateinit var recyclerView: RecyclerView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var spinner: Spinner
    private lateinit var userId: String

    private val requests = ObservableList<MaterialRequest>()
    private val materialRequest =
        ObservableMap<MaterialRequest.Status, ObservableList<MaterialRequest>>()
    private val observableStatus = Observable(currentStatus)
    private var userNames = ObservableMap<String, String>()
    private val itemNames = ObservableMap<String, String>()
    private val items = ObservableList<Triple<Item, Int, Int>>()
    private val zoneNameFromEventId = ObservableMap<String, String>()
    private val statusNames = ArrayList<String>()

    /**
     * Select next status page
     */
    private fun nextStatus() {
        currentStatus = when (currentStatus) {
            PENDING -> ACCEPTED
            ACCEPTED -> REFUSED
            REFUSED -> PENDING
            else -> currentStatus //should never happen
        }
        observableStatus.postValue(currentStatus)
    }

    /**
     * Select previous status page
     */
    private fun previousStatus() {
        currentStatus = when (currentStatus) {
            PENDING -> REFUSED
            REFUSED -> ACCEPTED
            ACCEPTED -> PENDING
            else -> currentStatus //should never happen
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
                userNames,
                itemNames,
                zoneNameFromEventId,
                modifyMaterialRequest,
                cancelMaterialRequest,
                returnMaterialRequest
            )

        observableStatus.observe(this) { recyclerView.adapter!!.notifyDataSetChanged() }

        items.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        requests.observeAdd(this) {
            if (it.value.staffInChargeId != null && !userNames.containsKey(it.value.staffInChargeId!!)) {
                addToUsersFromDB(it.value.staffInChargeId!!, userNames, this, this)
            }
            if (!userNames.containsKey(it.value.userId)) {
                addToUsersFromDB(it.value.userId, userNames, this, this)
            }
        }


        requests.group(this, materialRequest) {
            when (it.status) {
                DELIVERING, DELIVERED, RETURN_REQUESTED, RETURNING, RETURNED -> ACCEPTED
                else -> it.status
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getItemRequestsFromDB()
    }

    /**
     * Gets the item request of the user and then gets the item list
     */
    private fun getItemRequestsFromDB() {
        val observableDBAnswer = Observable<Boolean>()
        //Gets the item request of the user and then gets the item list
        Database.currentDatabase.materialRequestDatabase.getMaterialRequestListByUser(
            requests,
            userId
        ).observeOnce(this) {
            if (!it.value) {
                observableDBAnswer.postValue(false)
                HelperFunctions.showToast(
                    getString(R.string.fail_to_get_list_material_requests_eo),
                    this
                )
            } else {
                Database.currentDatabase.itemDatabase.getItemsList(items)
                    .observeOnce(this) { it2 ->
                        if (!it2.value) {
                            HelperFunctions.showToast(
                                getString(R.string.fail_to_get_list_items),
                                this
                            )
                        }
                    }
                val sentEventIds = mutableListOf<String>()
                for (request in requests) {
                    if (request.eventId !in sentEventIds) {
                        sentEventIds.add(request.eventId)
                        val event = Observable<Event>()
                        val zone = Observable<Zone>()
                        Database.currentDatabase.eventDatabase.getEventFromId(
                            request.eventId,
                            event
                        ).observeOnce(this) {
                            if (it.value) {
                                Database.currentDatabase.zoneDatabase.getZoneInformation(
                                    event.value!!.zoneId!!,
                                    zone
                                ).observeOnce(this) {
                                    if (it.value) {
                                        zoneNameFromEventId[event.value!!.eventId!!] =
                                            zone.value!!.zoneName!!
                                    }
                                }
                            }
                        }
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
            Database.currentDatabase.materialRequestDatabase.deleteMaterialRequest(request.requestId!!)
        l.observe(this) {
            if (it.value)
                requests.remove(request)
        }
        Unit
    }

    /**
     * Launches the activity to modify the item request
     */
    private val returnMaterialRequest = { request: MaterialRequest ->
        val newRequest = request.copy(status = RETURN_REQUESTED, staffInChargeId = null)
        Database.currentDatabase.materialRequestDatabase.updateMaterialRequest(
            request.requestId!!,
            newRequest
        ).observeOnce(this) {
            if (it.value) {
                requests[requests.indexOfFirst { it2 -> it2.requestId == request.requestId }] =
                    newRequest
            }
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