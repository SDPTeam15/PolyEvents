package com.github.sdpteam15.polyevents.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList

/**
 * An adapter for an event preview item in the recycler view. The list of events
 * previews are just for display, they are no listeners attached when clicked upon.
 */
class EventPreviewAdapter(private val dataSet: ObservableList<Event>,
                          private val listener: (Event) -> Unit) :
    RecyclerView.Adapter<EventPreviewAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventNameTextView: TextView = view.findViewById(R.id.card_preview_event_name)
        val eventTimeTextView: TextView = view.findViewById(R.id.card_preview_event_time)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_preview_event, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentEvent = dataSet[position]
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.eventNameTextView.text = currentEvent.eventName
        viewHolder.eventTimeTextView.text = currentEvent.formattedStartTime()
        viewHolder.itemView.setOnClickListener {
            listener(currentEvent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}