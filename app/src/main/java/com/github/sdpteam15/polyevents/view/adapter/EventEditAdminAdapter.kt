package com.github.sdpteam15.polyevents.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import java.time.format.DateTimeFormatter

/**
 * Recycler Adapter for the list of event edit request
 * @param context context of parent view used to inflate new views
 * @param lifecycleOwner parent to enable observables to stop observing when the lifecycle is closed
 * @param events List of all events edit request
 * @param origEvent A map that map the eventId to the original event (the one already in database)
 * @param onAcceptListener Listener for the click on the "accept" button
 * @param onRefuseListener Listener for the click on the "refuse" button
 * @param onSeeListener Listener for the click on the "see" button
 */
class EventEditAdminAdapter(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val events: ObservableList<Event>,
    private val origEvent: ObservableMap<String, Event>,
    private val onAcceptListener: (Event) -> Unit,
    private val onRefuseListener: (Event) -> Unit,
    private val onSeeListener: (Event, Boolean, Event?) -> Unit,
) : RecyclerView.Adapter<EventEditAdminAdapter.ItemViewHolder>() {
    private val adapterLayout = LayoutInflater.from(context)

    // Add observer to both list to update the recyclerview content when the lists are updated
    init {
        events.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }

        origEvent.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    /**
     * adapted ViewHolder for each event edit request
     * @param view The view of the current item
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val title = view.findViewById<TextView>(R.id.id_edit_title)
        private val eventName = view.findViewById<TextView>(R.id.id_edit_name)
        private val btnSee = view.findViewById<ImageButton>(R.id.id_edit_see)
        private val btnAccept = view.findViewById<ImageButton>(R.id.id_edit_accept)
        private val btnRefuse = view.findViewById<ImageButton>(R.id.id_edit_refuse)
        private val status = view.findViewById<TextView>(R.id.id_edit_status)

        /**
         * Binds the values of each value of a event edit to a view and add the listener to each button of the view.
         */
        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {
            title.text= when(event.eventId){
                null -> "Creation"
                else -> "Modification"
            }

            eventName.text = event.eventName
            status.setTextColor(when(event.status!!){
                Event.EventStatus.ACCEPTED -> Color.GREEN
                Event.EventStatus.PENDING -> Color.BLACK
                Event.EventStatus.REFUSED -> Color.RED
            })

            status.text = event.status.toString()

            btnAccept.visibility = if (event.status == Event.EventStatus.PENDING) VISIBLE else INVISIBLE
            btnRefuse.visibility = if (event.status == Event.EventStatus.PENDING) VISIBLE else INVISIBLE

            btnSee.setOnClickListener{
                if (event.eventId!=null){
                    onSeeListener(event, false, origEvent[event.eventId])
                }else{
                    onSeeListener(event, true, null)
                }
            }
            btnRefuse.setOnClickListener { onRefuseListener(event) }
            btnAccept.setOnClickListener { onAcceptListener(event) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val view = adapterLayout.inflate(R.layout.card_event_edit, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = events[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return events.size
    }
}