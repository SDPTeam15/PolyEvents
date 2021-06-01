package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.NUMBER_UPCOMING_EVENTS
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication.Companion.inTest
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.TimeTableActivity

/**
 * The fragment for the home page.
 */
class VisitorHomeFragment : Fragment() {


    private lateinit var listUpcomingEventsLayout: LinearLayout
    val events = ObservableList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_home_visitor, container, false)
        listUpcomingEventsLayout =
            fragmentView.findViewById<LinearLayout>(R.id.id_upcoming_events_list)

        currentDatabase.eventDatabase!!.getEvents(null, NUMBER_UPCOMING_EVENTS.toLong(), events)
            .observe(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to load events", fragmentView.context)
                }
            }
        events.observe(this) {
            updateContent()
        }

        if(!inTest)
            HelperFunctions.getLocationPermission(requireActivity())
        MainActivity.instance!!.switchRoles(fragmentView!!.findViewById(R.id.spinner_visitor), UserRole.PARTICIPANT)

        fragmentView.findViewById<Button>(R.id.id_timetable_button).setOnClickListener {
            val intent = Intent(activity, TimeTableActivity::class.java)
            startActivity(intent)
        }

        return fragmentView
    }

    /**
     * Update the content of the upcoming events
     */
    fun updateContent() {
        // Remove all the content first
        listUpcomingEventsLayout.removeAllViews()

        for (e in events) {
            setupEventTab(e)
        }
    }

    /**
     * Setup the layout for an event tab and add it to the layout provided
     * @param event : the event to add as a tab
     */
    private fun setupEventTab(event: Event) {
        val eventTab = layoutInflater.inflate(R.layout.card_event, null)

        eventTab.findViewById<TextView>(R.id.id_event_name_text).text = event.eventName

        eventTab.findViewById<TextView>(R.id.id_event_schedule_text).text =
            getString(R.string.at_hour_text, event.formattedStartTime())

        eventTab.findViewById<TextView>(R.id.id_event_zone).text = event.zoneName

        eventTab.findViewById<TextView>(R.id.id_event_description).text = event.description

        // TODO : set the icon of the event
        //eventTab.findViewById<ImageView>(R.id.id_event_icon).setImageBitmap(event.icon)

        listUpcomingEventsLayout.addView(eventTab)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) = HelperFunctions.onRequestPermissionsResult(requestCode, permissions, grantResults)
}