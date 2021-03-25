package com.github.sdpteam15.polyevents.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.EventActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions

/**
 * Extra containing the event ID to show on the launched event page
 */
const val EXTRA_EVENT_ID = "com.github.sdpteam15.polyevents.event.EVENT_ID"

/**
 * Shows the list of events and displays them in a new event when we click on one of them
 */
class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    val events = ObservableList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = fragmentView.findViewById<RecyclerView>(R.id.recycler_events_list)
        currentDatabase.getListEvent(null,10, events).observe(this) {
            if (it!!){
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }
        val openEvent = { event: Event ->
            val intent = Intent(inflater.context, EventActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, event.id)
            }
            startActivity(intent)
        }

        recyclerView.adapter = EventItemAdapter(events, openEvent)
        events.observe(this) { recyclerView.adapter!!.notifyDataSetChanged() }
        recyclerView.setHasFixedSize(false)
        // Inflate the layout for this fragment
        return fragmentView
    }

    /**
     * Update the content of the upcoming events
     */
    fun updateContent() {
        // Remove all the content first
        HelperFunctions.refreshFragment(fragmentManager, this)
    }
}