package com.github.sdpteam15.polyevents.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.event.Event

/**
 * The fragment for the home page.
 */
class HomeFragment : Fragment() {

    private lateinit var listUpcomingEventsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        listUpcomingEventsLayout =
            fragmentView.findViewById<LinearLayout>(R.id.id_upcoming_events_list)

        updateContent()

        return fragmentView
    }

    /**
     * Update the content of the upcoming events
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateContent() {
        // Remove all the content first
        listUpcomingEventsLayout.removeAllViews()

        val events = currentDatabase.getUpcomingEvents()

        for (event: Event in events) {
            setupEventTab(event)
        }
    }

    /**
     * Setup the layout for an event tab and add it to the layout provided
     * @param event : the event to add as a tab
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupEventTab(event: Event) {
        val eventTab = layoutInflater.inflate(R.layout.tab_event, null)

        eventTab.findViewById<TextView>(R.id.id_event_name_text).text = event.name

        eventTab.findViewById<TextView>(R.id.id_event_schedule_text).text =
            getString(R.string.at_hour_text, event.getTime())

        eventTab.findViewById<TextView>(R.id.id_event_zone).text = event.zone

        eventTab.findViewById<TextView>(R.id.id_event_description).text = event.description

        // TODO : set the icon of the event
        //eventTab.findViewById<ImageView>(R.id.id_event_icon).setImageBitmap(event.icon)

        listUpcomingEventsLayout.addView(eventTab)
    }
}