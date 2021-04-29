package com.github.sdpteam15.polyevents.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.EventActivity
import com.github.sdpteam15.polyevents.PolyEventsApplication
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.room.EventLocal
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Extra containing the event ID to show on the launched event page
 */
const val EXTRA_EVENT_ID = "com.github.sdpteam15.polyevents.event.EVENT_ID"

/**
 * Shows the list of events and displays them in a new event when we click on one of them
 */
class EventListFragment : Fragment() {

    companion object {
        const val TAG = "EventListFragment"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var myEventsSpinner: SwitchMaterial
    val events = ObservableList<Event>()
    val eventsLocal = ObservableList<EventLocal>()
    private val localEventViewModel: EventLocalViewModel by viewModels {
        EventLocalViewModelFactory((requireActivity().application as PolyEventsApplication).database.eventDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_events, container, false)
        recyclerView = fragmentView.findViewById(R.id.recycler_events_list)

        myEventsSpinner = fragmentView.findViewById(R.id.event_list_my_events_switch)
        myEventsSpinner.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.d(TAG, "switch on!")
                if (currentDatabase.currentUser == null) {
                    // Cannot switch to my Events if no user logged in
                    myEventsSpinner.isChecked = false
                    HelperFunctions.showToast(resources.getString(R.string.my_events_log_in), context)
                } else {
                    getUserLocalSubscribedEvents()
                }
            } else {
                Log.d(TAG, "switch off!")
                getEventsListAndDisplay(fragmentView.context)
            }
        }

        val openEvent = { event: Event ->
            val intent = Intent(inflater.context, EventActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, event.eventId)
            }
            startActivity(intent)
        }

        recyclerView.adapter = EventItemAdapter(events, openEvent)
        recyclerView.setHasFixedSize(false)

        getEventsListAndDisplay(fragmentView.context)
        // Inflate the layout for this fragment
        return fragmentView
    }

    override fun onResume() {
        super.onResume()

        getEventsListAndDisplay(context)
    }

    private fun getUserLocalSubscribedEvents() {
        localEventViewModel.getAllEvents(eventsLocal)
        eventsLocal.observe(this) {
            events.clear()
            events.addAll(it.value.map { it.toEvent() })
        }
    }

    private fun getEventsListAndDisplay(context: Context?) {
        // TODO: set limit or not?
        currentDatabase.eventDatabase!!.getEvents(null, 10, events).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to get events information", context)
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