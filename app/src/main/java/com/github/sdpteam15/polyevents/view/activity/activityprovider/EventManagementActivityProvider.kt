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
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.activity.admin.EventManagementActivity
import com.github.sdpteam15.polyevents.view.activity.admin.EventManagementListActivity
import com.github.sdpteam15.polyevents.view.adapter.MyEventEditRequestAdapter
import com.github.sdpteam15.polyevents.view.fragments.admin.EventEditDifferenceFragment

class EventManagementActivityProvider : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var currentStatus: Event.EventStatus = Event.EventStatus.PENDING

    private lateinit var recyclerView: RecyclerView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var spinner: Spinner
    private lateinit var userId: String

    private val eventRequests = ObservableList<Event>()
    private val origEvent = ObservableMap<String, Event>()
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
            else -> currentStatus
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
            else -> currentStatus
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

        userId = currentDatabase.currentUser!!.uid
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
                origEvent,
                modifyEventRequest,
                cancelEventRequest,
                seeDifference
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
        val eventList = ObservableList<Event>()
        eventList.observeAdd(this) {
            if (it.value.status != Event.EventStatus.CANCELED) {
                origEvent[it.value.eventId!!] = it.value
            }
        }
        //Gets the item request of the user and then gets the item list
        currentDatabase.eventDatabase!!.getEvents(null, null, eventList).observe(this) {
            if (it.value) {
                currentDatabase.eventDatabase!!.getEventEdits(
                    {
                        it.whereEqualTo(DatabaseConstant.EventConstant.EVENT_NAME.value, userId)
                    },
                    eventRequests
                ).observeOnce(this) {
                    if (!it.value) {
                        HelperFunctions.showToast(
                            getString(R.string.fail_to_get_list_events_edits_eo),
                            this
                        )
                        finish()
                    }
                }
            } else {
                HelperFunctions.showToast(getString(R.string.fail_to_get_list_event_eo), this)
                finish()
            }
        }
    }

    /**
     * Launch the activity to modify the item request
     */
    private val modifyEventRequest = { event: Event ->
        val intent = Intent(this, EventManagementActivity::class.java).apply {
            putExtra(EventManagementListActivity.INTENT_MANAGER, userId)
            putExtra(EventManagementListActivity.INTENT_MANAGER_EDIT, userId)
            putExtra(EventManagementListActivity.EVENT_ID_INTENT, event.eventEditId)
        }
        startActivity(intent)
    }

    /**
     * Launch the activity to modify the item request
     */
    private val seeDifference = { event: Event, creation: Boolean, eventOrig: Event? ->
        val fragment = EventEditDifferenceFragment(event, creation, eventOrig)
        fragment.show(supportFragmentManager, EventEditDifferenceFragment.TAG)
    }

    /**
     * Delete the item request
     */
    private val cancelEventRequest = { event: Event ->
        val l =
            currentDatabase.eventDatabase!!.removeEventEdit(event.eventEditId!!)
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