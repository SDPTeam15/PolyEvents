package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.EventActivity
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.view.fragments.ZonePreviewBottomSheetDialogFragment.Companion.EXTRA_ZONE_ID
import com.github.sdpteam15.polyevents.view.fragments.ZonePreviewBottomSheetDialogFragment.Companion.EXTRA_ZONE_NAME

/**
 * A fragment representing a list of Items.
 */
class ZoneEventsFragment : Fragment(R.layout.fragment_zone_events_list) {

    private var zoneId: String? = null
    private var zoneName: String? = null

    private lateinit var zoneNameTextView: TextView
    private lateinit var eventsRecyclerView: RecyclerView

    private lateinit var customBackButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        arguments?.let {
            zoneId = it.getString(EXTRA_ZONE_ID)
            zoneName = it.getString(EXTRA_ZONE_NAME)
        }

        if (view != null) {
            zoneNameTextView = view.findViewById(R.id.fragment_zone_events_zone_name_text_view)
            if (zoneName != null) {
                zoneNameTextView.text = zoneName
            }

            val eventsObservable = ObservableList<Event>()

            val openEvent = { event: Event ->
                val intent = Intent(inflater.context, EventActivity::class.java).apply {
                    putExtra(EXTRA_EVENT_ID, event.eventId)
                }
                startActivity(intent)
            }

            val eventAdapter = EventItemAdapter(events = eventsObservable, openEvent)

            eventsObservable.observe(this) {
                eventAdapter.notifyDataSetChanged()
            }

            eventsRecyclerView = view.findViewById(R.id.zone_events_fragment_recycler_view)
            eventsRecyclerView.adapter = eventAdapter
            eventsRecyclerView.layoutManager = LinearLayoutManager(context)

            currentDatabase.eventDatabase!!.getEventsByZoneId(
                zoneId = zoneId!!,
                events = eventsObservable
            )
            // Go back to MapFragment on press back
            customBackButton = view.findViewById(R.id.fragment_zone_events_back_button)
            customBackButton.setOnClickListener {
                HelperFunctions.changeFragment(activity, MainActivity.fragments[R.id.ic_map])
            }
        }

        return view
    }
}