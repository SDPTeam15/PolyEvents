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
import com.github.sdpteam15.polyevents.view.activity.EventActivity
import com.github.sdpteam15.polyevents.view.adapter.EventPreviewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Documentation on: https://developer.android.com/guide/topics/ui/dialogs.html#kotlin
 */
class ZonePreviewBottomSheetDialogFragment: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ZonePreviewBottomSheetDialogFragment"
        const val EXTRA_ZONE_ID = "zoneId"
        const val EXTRA_ZONE_NAME = "zoneName"

        fun newInstance(
            zoneId: String,
            onShowEventsClickListener: View.OnClickListener,
            onItineraryClickListener: View.OnClickListener
        ): ZonePreviewBottomSheetDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_ZONE_ID, zoneId)
            val f = ZonePreviewBottomSheetDialogFragment()
            f.onShowEventsClickListener = onShowEventsClickListener
            f.onItineraryClickListener = onItineraryClickListener
            f.arguments = args
            return f
        }
    }

    private var zoneId: String? = null

    private lateinit var previewUpcomingEventsTextView: TextView
    private lateinit var seeEventsButton: Button
    private lateinit var showItineraryButton: Button
    private lateinit var previewUpComingEventsRecyclerView: RecyclerView
    private lateinit var zoneNameTextView: TextView
    private lateinit var zoneDescriptionTextView: TextView

    private lateinit var onItineraryClickListener: View.OnClickListener
    private lateinit var onShowEventsClickListener: View.OnClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(
            R.layout.fragment_preview_zone_events_bottom_sheet,
            container,
            false
        )

        zoneId = arguments?.getString(EXTRA_ZONE_ID)

        seeEventsButton = v.findViewById(R.id.zone_preview_show_events_button)
        showItineraryButton = v.findViewById(R.id.zone_preview_show_itinerary_button)
        previewUpcomingEventsTextView = v.findViewById(R.id.zone_preview_dialog_upcoming_events)
        previewUpComingEventsRecyclerView =
            v.findViewById(R.id.zone_preview_dialog_event_recycler_view)
        zoneNameTextView = v.findViewById(R.id.zone_preview_dialog_zone_name)
        zoneDescriptionTextView = v.findViewById(R.id.zone_preview_dialog_zone_description)

        val zoneObservable = Observable<Zone>()
        zoneObservable.observeOnce(this) {
            val zoneName = it.value.zoneName

            zoneNameTextView.text = zoneName
            zoneDescriptionTextView.text = it.value.description

            // Buttons are disabled until the zone information is retrieved
            seeEventsButton.isEnabled = true
            showItineraryButton.isEnabled = true

            // on click go to the events list per zone fragment (and dismiss the dialog as well on click)
            seeEventsButton.setOnClickListener {
                dismiss()
                onShowEventsClickListener.onClick(view)
            }
            // Set the itinerary button on click listener (and dismiss the dialog as well on click)
            showItineraryButton.setOnClickListener {
                dismiss()
                onItineraryClickListener.onClick(view)
            }
        }

        currentDatabase.zoneDatabase!!.getZoneInformation(
            zoneId = zoneId!!,
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
        val openEvent = { event: Event ->
            val intent = Intent(inflater.context, EventActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, event.eventId)
            }
            startActivity(intent)
        }
        // setup recycler view for preview events
        val eventPreviewAdapter = EventPreviewAdapter(eventsObservableList, openEvent)
        previewUpComingEventsRecyclerView.layoutManager = LinearLayoutManager(v.context)
        previewUpComingEventsRecyclerView.adapter = eventPreviewAdapter

        // Select the first 3 events happening in the zone for preview
        currentDatabase.eventDatabase!!.getEventsByZoneId(
            events = eventsObservableList,
            zoneId = zoneId!!,
            limit = 3
        )

        return v
    }

    /**
     * Set the click listener for the zone itinerary button
     * @param onItineraryClickListener the listener for the zone itinerary button
     */
    fun setOnItineraryClickListener(onItineraryClickListener: View.OnClickListener) {
        this.onItineraryClickListener = View.OnClickListener {
            dismiss()
            onItineraryClickListener.onClick(view)
        }
    }

    /**
     * Set the click listener for the show events button
     * @param onShowEventsClickListener the listener for the show events button
     */
    fun setOnShowEventsClickListener(onShowEventsClickListener: View.OnClickListener) {
        this.onShowEventsClickListener = View.OnClickListener {
            dismiss()
            onShowEventsClickListener.onClick(view)
        }
    }
}