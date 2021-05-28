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
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.activity.ItemRequestActivity
import com.github.sdpteam15.polyevents.view.adapter.MyEventEditRequestAdapter
import com.github.sdpteam15.polyevents.view.fragments.home.ProviderHomeFragment

class EventManagementActivityProvider : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var currentStatus: Event.EventStatus = Event.EventStatus.PENDING

    private lateinit var recyclerView: RecyclerView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var spinner: Spinner
    private lateinit var userId: String

    private val eventRequests = ObservableList<Event>()
    private val materialRequest =
        ObservableMap<Event.EventStatus, ObservableList<Event>>()
    private val observableStatus = Observable(currentStatus)
    private val statusNames = ArrayList<String>()

    /**
     * Select next status page
     */
    private fun nextStatus() {
        currentStatus = when (currentStatus) {
            Event.EventStatus.PENDING -> Event.EventStatus.ACCEPTED
            Event.EventStatus.ACCEPTED -> Event.EventStatus.REFUSED
            Event.EventStatus.REFUSED -> Event.EventStatus.PENDING
        }
        observableStatus.postValue(currentStatus)
    }

    /**
     * Select previous status page
     */
    private fun previousStatus() {
        currentStatus = when (currentStatus) {
            Event.EventStatus.PENDING -> Event.EventStatus.REFUSED
            Event.EventStatus.REFUSED -> Event.EventStatus.ACCEPTED
            Event.EventStatus.ACCEPTED -> Event.EventStatus.PENDING
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
        setContentView(R.layout.activity_event_management_provider)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        userId = intent.getStringExtra(ProviderHomeFragment.ID_USER)!!
        recyclerView = findViewById(R.id.id_recycler_my_event_edit_requests)
        leftButton = findViewById(R.id.id_change_request_status_left)
        rightButton = findViewById(R.id.id_change_request_status_right)
        spinner = findViewById(R.id.id_title_event_edit_request)

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
            MyEventEditRequestAdapter(
                this,
                this,
                materialRequest,
                observableStatus,
                modifyEventRequest,
                cancelEventRequest
            )

        observableStatus.observe(this) { recyclerView.adapter!!.notifyDataSetChanged() }

        eventRequests.group(this, materialRequest) {
            it.status!!
        }
    }

    override fun onResume() {
        super.onResume()
        getEventEditRequestsFromDB()
    }

    /**
     * Get the item request of the user and then gets the item list
     */
    private fun getEventEditRequestsFromDB() {
        //Gets the item request of the user and then gets the item list
        Database.currentDatabase.eventDatabase!!.getEventEdits(
            {
                it.whereEqualTo(DatabaseConstant.EventConstant.EVENT_NAME.value,userId)
            },
            eventRequests
        ).observeOnce(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to get the list of event edit requests", this)
            }
        }
    }

    /**
     * Launch the activity to modify the item request
     */
    private val modifyEventRequest = { event: Event ->

        val intent = Intent(this, ItemRequestActivity::class.java).apply {
            putExtra(EXTRA_ITEM_REQUEST_ID, event.eventEditId)
        }
        startActivity(intent)
    }

    /**
     * Delete the item request
     */
    private val cancelEventRequest = { event: Event ->
        val l =
            Database.currentDatabase.eventDatabase!!.removeEventEdit(event.eventEditId!!)
        l.observe(this) {
            if (it.value)
                eventRequests.remove(event)
        }
        Unit
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        currentStatus = Event.EventStatus.fromOrdinal(p2)!!
        observableStatus.postValue(currentStatus)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}