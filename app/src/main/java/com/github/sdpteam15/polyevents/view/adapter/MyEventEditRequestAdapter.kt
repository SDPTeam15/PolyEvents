package com.github.sdpteam15.polyevents.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap

/**
 * Recycler Adapter for the list of items
 * Shows each item with its available quantity
 * @param context context of parent view used to inflate new views
 * @param lifecycleOwner parent to enable observables to stop observing when the lifecycle is closed
 * @param requests List of all item requests
 */
class MyEventEditRequestAdapter(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val requests: ObservableMap<Event.EventStatus, ObservableList<Event>>,
    private val typeToDisplay: Observable<Event.EventStatus>,
    private val events: ObservableMap<String, Event>,
    private val onModifyListener: (Event) -> Unit,
    private val onCancelListener: (Event) -> Unit,
    private val seeListener: (Event, Boolean, Event?) -> Unit
) : RecyclerView.Adapter<MyEventEditRequestAdapter.ItemViewHolder>() {
    private val adapterLayout = LayoutInflater.from(context)

    init {
        requests.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }

        events.observe(lifecycleOwner) { notifyDataSetChanged() }
    }

    /**
     * adapted ViewHolder for each item
     * Takes the corresponding material request view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val eventName = view.findViewById<TextView>(R.id.id_modify_event_name)
        private val btnModify = view.findViewById<ImageButton>(R.id.id_modify_request)
        private val btnCancel = view.findViewById<ImageButton>(R.id.id_delete_request)
        private val status = view.findViewById<TextView>(R.id.id_request_status)
        private val adminMessage = view.findViewById<TextView>(R.id.id_admin_message)
        private val refusalLayout = view.findViewById<LinearLayout>(R.id.id_reason_of_refusal)
        private val btnSee = view.findViewById<ImageButton>(R.id.id_see_event)
        private val tvMode = view.findViewById<TextView>(R.id.tvMode)

        /**
         * Binds the values of each value of a material request to a view
         */
        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {

            eventName.text = "Event name: " + event.eventName
            status.setTextColor(
                when (event.status!!) {
                    Event.EventStatus.ACCEPTED -> Color.GREEN
                    Event.EventStatus.PENDING -> Color.BLACK
                    Event.EventStatus.REFUSED -> Color.RED
                }
            )
            status.text = event.status.toString()

            btnModify.visibility =
                if (event.status == Event.EventStatus.PENDING) VISIBLE else INVISIBLE
            btnCancel.visibility =
                if (event.status == Event.EventStatus.PENDING) VISIBLE else INVISIBLE
            btnCancel.setOnClickListener { onCancelListener(event) }
            if (event.eventId == null) {
                btnSee.setOnClickListener { seeListener(event, true, null) }
            } else {
                btnSee.setOnClickListener {
                    seeListener(
                        event,
                        false,
                        events[event.eventId!!]
                    )
                }
            }

            btnModify.setOnClickListener { onModifyListener(event) }
            refusalLayout.visibility = if (event.adminMessage != null) VISIBLE else GONE
            adminMessage.text = event.adminMessage
            tvMode.text = if (event.eventId == null) {
                "Creation"
            } else {
                "Modification"
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val view = adapterLayout.inflate(R.layout.card_my_event_edit_request, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = requests[typeToDisplay.value]!![position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return requests[typeToDisplay.value]?.size ?: 0
    }
}