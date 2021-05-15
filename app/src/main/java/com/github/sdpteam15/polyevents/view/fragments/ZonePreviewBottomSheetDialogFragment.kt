package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.EventPreviewActivity
import com.github.sdpteam15.polyevents.view.adapter.EventPreviewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Documentation on: https://developer.android.com/guide/topics/ui/dialogs.html#kotlin
 */
class ZonePreviewBottomSheetDialogFragment(
    val zoneId: String,
    val onItineraryClickListener: View.OnClickListener
): BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ZonePreviewBottomSheetDialogFragment"
        const val EXTRA_ZONE_ID = "zoneId"
    }

    private lateinit var previewUpcomingEventsTextView: TextView
    private lateinit var seeEventsButton: Button
    private lateinit var showItineraryButton: Button
    private lateinit var previewUpComingEventsRecyclerView: RecyclerView
    private lateinit var zoneNameTextView: TextView
    private lateinit var zoneDescriptionTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(
            R.layout.fragment_preview_zone_events_bottom_sheet,
            container,
            false)

        seeEventsButton = v.findViewById(R.id.zone_preview_show_events_button)
        showItineraryButton = v.findViewById(R.id.zone_preview_show_itinerary_button)
        previewUpcomingEventsTextView = v.findViewById(R.id.zone_preview_dialog_upcoming_events)
        previewUpComingEventsRecyclerView =
            v.findViewById(R.id.zone_preview_dialog_event_recycler_view)
        zoneNameTextView = v.findViewById(R.id.zone_preview_dialog_zone_name)
        zoneDescriptionTextView = v.findViewById(R.id.zone_preview_dialog_zone_description)

        seeEventsButton.setOnClickListener {
            startActivity(
                Intent(context, EventPreviewActivity::class.java).apply {
                    putExtra(EXTRA_ZONE_ID, zoneId)
                }
            )
        }

        showItineraryButton.setOnClickListener {
            dismiss()
            onItineraryClickListener
        }

        val zoneObservable = Observable<Zone>()
        zoneObservable.observeOnce(this) {
            zoneNameTextView.text = it.value.zoneName
            zoneDescriptionTextView.text = it.value.description
        }

        currentDatabase.zoneDatabase!!.getZoneInformation(
            zoneId = zoneId,
            zone = zoneObservable
        )

        // TODO: progress dialogs!
        val eventsObservableList = ObservableList<Event>()
        eventsObservableList.observe(this) {
            if (it.value.isEmpty()) {
                previewUpcomingEventsTextView.text = resources.getString(R.string.no_upcoming_events)
            } else {
                previewUpcomingEventsTextView.text = resources.getString(R.string.upcoming_events)
                previewUpComingEventsRecyclerView.adapter!!.notifyDataSetChanged()
            }
        }

        // setup recycler view for preview events
        val eventPreviewAdapter = EventPreviewAdapter(eventsObservableList)
        previewUpComingEventsRecyclerView.layoutManager = LinearLayoutManager(v.context)
        previewUpComingEventsRecyclerView.adapter = eventPreviewAdapter

        // Select the first 3 events happening in the zone for preview
        currentDatabase.eventDatabase!!.getEventsByZoneId(
            events = eventsObservableList,
            zoneId = zoneId,
            limit = 3
        )

        /*val testEvent1 = Event(
            eventId = "0",
            eventName = "Hello",
            startTime = LocalDateTime.now()
        )

        val testEvent2 = Event(
            eventId = "1",
            eventName = "Hello Again",
            startTime = LocalDateTime.now()
        )

        val testEvent3 = Event(
            eventId = "2",
            eventName = "Hello again again",
            startTime = LocalDateTime.now()
        )

        eventsObservableList.addAll(mutableListOf(testEvent1, testEvent2, testEvent3))*/

        return v
    }
}