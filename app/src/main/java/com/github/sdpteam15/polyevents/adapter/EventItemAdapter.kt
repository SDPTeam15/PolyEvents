package com.github.sdpteam15.polyevents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event

/**
 * Adapts events to RecyclerView's ItemViewHolders
 * @param events The list of events to adapt
 * @param listener A listener that will be triggered on click of an ItemViewHolder element
 */
class EventItemAdapter(
    private val events: ObservableList<Event>,
    private val listener: (Event) -> Unit
) : RecyclerView.Adapter<EventItemAdapter.ItemViewHolder>() {

    /**
     * adapted ViewHolder for each event
     * Takes the corresponding event view
     */
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val eventName = view.findViewById<TextView>(R.id.id_event_name_text)
        private val eventSchedule = view.findViewById<TextView>(R.id.id_event_schedule_text)
        private val eventZone = view.findViewById<TextView>(R.id.id_event_zone)
        private val eventDescription = view.findViewById<TextView>(R.id.id_event_description)
        private val eventIcon = view.findViewById<ImageView>(R.id.id_event_icon)

        /**
         * Binds the values of each field of an event to the layout of an event
         */
        fun bind(event: Event) {
            eventName.text = event.eventName
            eventSchedule.text = "at ${event.formattedStartTime()}"
            eventZone.text = event.zoneName
            eventDescription.text = event.description

            // TODO : set the icon of the event
            //eventIcon.setImageBitmap(event.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_event, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.setOnClickListener {
            listener(event)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}



