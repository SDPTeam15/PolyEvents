package com.github.sdpteam15.polyevents.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.activity.EventActivity
import com.github.sdpteam15.polyevents.view.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Extra containing the event ID to show on the launched event page
 */
const val EXTRA_EVENT_ID = "com.github.sdpteam15.polyevents.event.EVENT_ID"
const val EXTRA_EVENT_NAME = "com.github.sdpteam15.polyevents.event.EVENT_NAME"

/**
 * Shows the list of events and displays them in a new event when we click on one of them
 */
class EventListFragment : Fragment() {

    companion object {
        const val TAG = "EventListFragment"

        // for testing purposes
        lateinit var localDatabase: LocalDatabase
        lateinit var eventLocalViewModel: EventLocalViewModel
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var myEventsSwitch: SwitchMaterial
    val events = ObservableList<Event>()
    private val eventsLocal = ObservableList<EventLocal>()

    // Lazily initialized view model, instantiated only when accessed for the first time
    /*private val localEventViewModel: EventLocalViewModel by viewModels {
        EventLocalViewModelFactory(localDatabase.eventDao())
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_events, container, false)

        localDatabase = (requireActivity().application as PolyEventsApplication).localDatabase
        eventLocalViewModel = EventLocalViewModelFactory(
            localDatabase.eventDao()
        ).create(
            EventLocalViewModel::class.java
        )

        recyclerView = fragmentView.findViewById(R.id.recycler_events_list)

        myEventsSwitch = fragmentView.findViewById(R.id.event_list_my_events_switch)
        myEventsSwitch.setOnCheckedChangeListener { _, isChecked ->
            myEventsSwitchCallback(isChecked)
        }

        val openEvent = { event: Event ->
            val intent = Intent(inflater.context, EventActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, event.eventId)
            }
            startActivity(intent)
        }

        recyclerView.adapter = EventItemAdapter(events, openEvent)
        recyclerView.setHasFixedSize(false)
        // Inflate the layout for this fragment
        return fragmentView
    }

    override fun onResume() {
        super.onResume()

        myEventsSwitchCallback(myEventsSwitch.isChecked)
    }

    override fun onStart() {
        super.onStart()

        myEventsSwitchCallback(myEventsSwitch.isChecked)
    }

    /**
     * Retrieve my events if switch is on, else retrieve all events from remote database
     * @param isChecked true if the switch is on to retrieve only the events the current user $
     * if registered to from the local database
     */
    private fun myEventsSwitchCallback(isChecked: Boolean) {
        if (isChecked) {
            getUserLocalSubscribedEvents()
        } else {
            getEventsListAndDisplay(context)
        }
    }

    /**
     * Get all the events the current user is subscribed to from the local cache (room database).
     */
    private fun getUserLocalSubscribedEvents() {
        eventLocalViewModel.getAllEvents(eventsLocal)

        eventsLocal.observe(this) {
            events.clear()
            events.addAll(it.value.map { it.toEvent() })
        }
    }

    private fun getEventsListAndDisplay(context: Context?) {
        // TODO: set limit or not?
        currentDatabase.eventDatabase.getEvents(null, null, events).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast(getString(R.string.fail_to_get_information), context)
            }
        }
        updateEventsList()
    }

    private fun updateEventsList() {
        // TODO: get events list from local if switched to user registered
        events.observe(this) { recyclerView.adapter!!.notifyDataSetChanged() }
    }

    /**
     * Update the content of the upcoming events
     */
    /*
    fun updateContent() {
        // Remove all the content first
        HelperFunctions.refreshFragment(fragmentManager, this)
    }*/
}