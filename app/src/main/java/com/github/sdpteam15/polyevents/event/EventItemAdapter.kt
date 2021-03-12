package com.github.sdpteam15.polyevents.event

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R

/**
 * Adapts events to RecyclerView's ItemViewHolders
 * @param events The list of events to adapt
 * @param listener A listener that will be triggered on click of an ItemViewHolder element
 */
class EventItemAdapter(
    private val events: List<Event>,
    private val listener: (Event) -> Unit
) : RecyclerView.Adapter<EventItemAdapter.ItemViewHolder>() {

    /**
     * adapted ViewHolder for each event
     * Takes the corresponding event view
     */
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val eventName = view.findViewById<TextView>(R.id.id_event_name_text)
        private val eventSchedule = view.findViewById<TextView>(R.id.id_event_schedule_text)
        private val eventZone = view.findViewById<TextView>(R.id.id_event_zone)
        private val eventDescription = view.findViewById<TextView>(R.id.id_event_description)
        private val eventIcon = view.findViewById<ImageView>(R.id.id_event_icon)

        /**
         * Binds the values of each field of an event to the layout of an event
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(event: Event) {
            eventName.text = event.name
            eventSchedule.text = "at ${event.getTime()}"
            eventZone.text = event.zone
            eventDescription.text = event.description

            // TODO : set the icon of the event
            //eventIcon.setImageBitmap(event.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_event, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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



