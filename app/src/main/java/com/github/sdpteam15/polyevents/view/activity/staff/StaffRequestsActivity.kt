package com.github.sdpteam15.polyevents.view.activity.staff

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.DatabaseHelper
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest.Status.*
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.activity.staff.StaffRequestsActivity.Companion.StaffRequestStatus.*
import com.github.sdpteam15.polyevents.view.adapter.StaffItemRequestAdapter

/**
 * Extra containing the user ID of the staff
 */
const val EXTRA_ID_USER_STAFF = "com.github.sdpteam15.polyevents.requests.STAFF_USER_ID"

/**
 * Activity for staff item requests management
 * Staffs can take in charge item requests and deliver them to event providers
 */
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
    private val staffId = currentDatabase.currentUser!!.uid
    private val users = ObservableList<UserEntity>()
    private val userNames = ObservableMap<String, String>()
    private val itemNames = ObservableMap<String, String>()
    private val zoneNameFromEventId = ObservableMap<String, String>()
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
        setContentView(R.layout.activity_staff_item_requests)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        staffUserId = intent.getStringExtra(EXTRA_ID_USER_STAFF)!!
        recyclerView = findViewById(R.id.id_recycler_staff_item_requests)
        leftButton = findViewById(R.id.id_change_request_status_left)
        rightButton = findViewById(R.id.id_change_request_status_right)
        spinner = findViewById(R.id.id_title_item_request)
//-------------------------------------------------------------------------------------------
        //List of status
        statusNames.add(getString(R.string.deliveries_staff))
        statusNames.add(getString(R.string.returns_staff))
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
                zoneNameFromEventId,
                staffId,
                acceptMaterialDelivery,
                cancelMaterialDelivery,
                onMaterialDelivered
            )

        currentStatus.observe(this) {
            refresh()
        }

        requests.observeUpdate(this) {
            if (it.sender != currentDatabase) {
                currentDatabase.materialRequestDatabase.updateMaterialRequest(
                    it.value.requestId!!,
                    it.value
                ).observeOnce { it2 ->
                    if (!it2.value) {
                        showToast(getString(R.string.fail_to_update_material_request), this)
                    }
                }
            }
        }

        items.observeUpdate(this) {
            if (it.sender != currentDatabase) {
                currentDatabase.itemDatabase.updateItem(
                    it.value.first,
                    it.value.second,
                    it.value.third
                )
            }
        }

        items.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        requests.group(this, materialRequest) {
            staffRequestStatusFromMaterialStatus(it.status)
        }
        requests.observeAdd(this) {
            if (!userNames.containsKey(it.value.userId)) {
                DatabaseHelper.addToUsersFromDB(it.value.userId, userNames, this, this)
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
        //Gets the item request of the user and then gets the item list
        currentDatabase.materialRequestDatabase.getMaterialRequestList(
            requests.sortAndLimitFrom(this) { it.status }
        ).observeOnce(this) {
            if (!it.value) {
                showToast(getString(R.string.fail_to_get_list_material_requests), this)
            } else {
                val sentEventIds = mutableListOf<String>()
                for (request in requests) {
                    if (request.eventId !in sentEventIds) {
                        sentEventIds.add(request.eventId)
                        val event = Observable<Event>()
                        val zone = Observable<Zone>()
                        currentDatabase.eventDatabase.getEventFromId(request.eventId, event)
                            .observeOnce(this) {
                                if (it.value) {
                                    currentDatabase.zoneDatabase.getZoneInformation(
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

        currentDatabase.itemDatabase.getItemsList(items)
            .observeOnce(this) {
                if (!it.value) {
                    showToast(getString(R.string.fail_to_get_list_items_staff), this)
                }
            }

    }

    /**
     * Accept a material request delivery / return
     */
    private val acceptMaterialDelivery = { request: MaterialRequest ->
        requests.set(
            requests.indexOfFirst { it.requestId == request.requestId }, when (request.status) {
                ACCEPTED -> request.copy(status = DELIVERING, staffInChargeId = staffUserId)
                RETURN_REQUESTED -> request.copy(status = RETURNING, staffInChargeId = staffUserId)
                else -> request // should never happen
            }, this
        )
        Unit
    }

    /**
     * Accept a material request delivery / return
     */
    private val onMaterialDelivered = { request: MaterialRequest ->
        requests.set(
            requests.indexOfFirst { it.requestId == request.requestId }, when (request.status) {
                DELIVERING -> request.copy(status = DELIVERED)
                RETURNING -> {
                    for (item in request.items) {
                        val oldItemIndex = items.indexOfFirst { it.first.itemId == item.key }
                        items[oldItemIndex] =
                            items[oldItemIndex].copy(third = items[oldItemIndex].third + item.value)

                    }
                    request.copy(status = RETURNED)
                }
                else -> request // should never happen
            }, this
        )


        Unit
    }

    /**
     * Cancel a material request delivery / return
     */
    private val cancelMaterialDelivery = { request: MaterialRequest ->
        requests.set(
            requests.indexOfFirst { it.requestId == request.requestId }, when (request.status) {
                DELIVERING -> request.copy(status = ACCEPTED, staffInChargeId = null)
                RETURNING -> request.copy(status = RETURN_REQUESTED, staffInChargeId = null)
                else -> request // should never happen
            }, this
        )
        Unit
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        currentStatus.postValue(fromOrdinal(p2)!!)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


    companion object {
        /**
         * Status to display on the staff request activity
         * They regroups Material requests statuses within the same delivery process
         * The OTHERS are not displayed on the screen, for example an item request which is refused
         */
        enum class StaffRequestStatus {
            DELIVERY,
            RETURN,
            OTHERS;
        }

        private val mapOrdinal = StaffRequestStatus.values().map { it.ordinal to it }.toMap()

        private val mapCategory = mapOf(
            ACCEPTED to DELIVERY,
            DELIVERING to DELIVERY,
            DELIVERED to DELIVERY,

            RETURN_REQUESTED to RETURN,
            RETURNING to RETURN,
            RETURNED to RETURN,

            PENDING to OTHERS,
            REFUSED to OTHERS,
            CANCELED to OTHERS
        )

        private fun staffRequestStatusFromMaterialStatus(status: MaterialRequest.Status): StaffRequestStatus {
            return mapCategory[status]!!
        }


        private fun fromOrdinal(ordinal: Int) = mapOrdinal[ordinal]


    }
}