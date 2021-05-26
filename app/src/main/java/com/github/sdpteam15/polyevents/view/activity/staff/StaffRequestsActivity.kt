package com.github.sdpteam15.polyevents.view.activity.staff

import android.os.Bundle
import android.util.Log
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
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_STATUS
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest.Status.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.activity.staff.StaffRequestsActivity.Companion.StaffRequestStatus.DELIVERY
import com.github.sdpteam15.polyevents.view.activity.staff.StaffRequestsActivity.Companion.StaffRequestStatus.RETURN
import com.github.sdpteam15.polyevents.view.adapter.StaffItemRequestAdapter

/**
 * Extra containing the user ID of the staff
 */
const val EXTRA_ID_USER_STAFF = "com.github.sdpteam15.polyevents.requests.STAFF_USER_ID"

class StaffRequestsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var spinner: Spinner
    private lateinit var staffUserId: String

    private val requests = ObservableList<MaterialRequest>()
    private val materialRequest =
        ObservableMap<StaffRequestStatus, ObservableList<MaterialRequest>>()
    private val currentStatus = Observable(DELIVERY)
    private var staffName = Database.currentDatabase.currentUser!!.name
    private val userNames = ObservableMap<String, String>()
    private val itemNames = ObservableMap<String, String>()
    private val items = ObservableList<Triple<Item, Int, Int>>()
    private val statusNames = ArrayList<String>()

    /**
     * Select next status page
     */
    private fun nextStatus() {
        currentStatus.postValue(
            when (currentStatus.value) {
                DELIVERY -> RETURN
                RETURN -> DELIVERY
                else -> DELIVERY
            }
        )
    }

    /**
     * Select previous status page
     */
    private fun previousStatus() {
        currentStatus.postValue(
            when (currentStatus.value) {
                RETURN -> DELIVERY
                DELIVERY -> RETURN
                else -> DELIVERY
            }
        )
    }

    /**
     * Refreshes the view
     */
    private fun refresh() {
        spinner.setSelection(currentStatus.value!!.ordinal)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_item_requests)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        staffUserId = intent.getStringExtra(EXTRA_ID_USER_STAFF)!!
        recyclerView = findViewById(R.id.id_recycler_my_item_requests)
        leftButton = findViewById(R.id.id_change_request_status_left)
        rightButton = findViewById(R.id.id_change_request_status_right)
        spinner = findViewById(R.id.id_title_item_request)
//-------------------------------------------------------------------------------------------
        //List of status
        statusNames.add("Deliveries")
        statusNames.add("Returns")
        val adapter =
            ArrayAdapter(this, R.layout.spinner_dropdown_item, statusNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(currentStatus.value!!.ordinal)
        spinner.onItemSelectedListener = this

        leftButton.setOnClickListener {
            previousStatus()
        }
        rightButton.setOnClickListener {
            nextStatus()
        }


        recyclerView.adapter =
            StaffItemRequestAdapter(
                this,
                this,
                materialRequest,
                currentStatus,
                itemNames,
                userNames,
                staffName,
                modifyMaterialRequest,
                cancelMaterialRequest
            )

        currentStatus.observe(this) {
            refresh()
            Log.d("YYYYYYYYYY", items.size.toString())
        }

        items.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        requests.group(this, materialRequest) {
            fromOrdinal(staffRequestStatusFromMaterialStatus(it.status))!!
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
        //Gets the item request of the user and then gets the item list
        Log.d("ZZZZZZZZZZZZZZZZZZ", allStaffStatuses().toString())
        Database.currentDatabase.materialRequestDatabase!!.getMaterialRequestList(
            requests,
            {
                it.orderBy(MATERIAL_REQUEST_STATUS.value)
            }
        ).observeOnce(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to get the list of material requests", this)
            }
        }
        Database.currentDatabase.itemDatabase!!.getItemsList(items)
            .observeOnce(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to get the list of items", this)
                }
            }

        val tempUsers = ObservableList<UserEntity>()
        tempUsers.group(this) { it.uid }.then.map(this, userNames) { it[0].name ?: "UNKNOWN" }
        Database.currentDatabase.userDatabase!!.getListAllUsers(tempUsers)
            .observeOnce(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to get the list of users", this)
                }
            }
    }

    private val modifyMaterialRequest = { request: MaterialRequest ->
        val l =
            Database.currentDatabase.materialRequestDatabase!!.deleteMaterialRequest(request.requestId!!)
        l.observe(this) {
            if (it.value)
                requests.remove(request)
        }
        Unit
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
        currentStatus.postValue(fromOrdinal(p2)!!)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    companion object {
        enum class StaffRequestStatus() {
            DELIVERY,
            RETURN,
            OTHERS;


            fun getItemRequestStatuses(): MutableList<Long> {
                return when (this) {
                    DELIVERY -> listOf(ACCEPTED, DELIVERING, DELIVERED)
                    RETURN -> listOf(RETURN_REQUESTED, RETURNING, RETURNED)
                    OTHERS -> listOf(PENDING, REFUSED)
                }.map { it.ordinal.toLong() }.toMutableList()
            }
        }

        fun allStaffStatuses(): MutableList<Long> {
            return StaffRequestStatus.values().flatMap { it.getItemRequestStatuses() }
                .toMutableList()
        }

        fun staffRequestStatusFromMaterialStatus(status: MaterialRequest.Status): Int {
            return mapOrdinal.entries.firstOrNull { it.value.ordinal == status.ordinal }!!.key
        }

        private val mapOrdinal = StaffRequestStatus.values().map { it.ordinal to it }.toMap()
        fun fromOrdinal(ordinal: Int) = mapOrdinal[ordinal]


    }
}