package com.github.sdpteam15.polyevents.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.localDatetimeToString
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap

/**
 * Recycler Adapter for the list of items
 * Shows each item with its available quantity
 * @param context context of parent view used to inflate new views
 * @param lifecycleOwner parent to enable observables to stop observing when the lifecycle is closed
 * @param availableItems Map of each available item grouped by types
 * @param mapSelectedItems Item counts for each item
 */
class EventListAdapter(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        private val allEvents: ObservableMap<String, Pair<String, ObservableList<Event>>>,
        private val modifyListener: (String) -> Unit,
        private val deleteListener: (String, Event) -> Unit
) : RecyclerView.Adapter<EventListAdapter.CustomViewHolder<*>>() {
    private var isZoneOpen = mutableMapOf<String, Boolean>()
    private val inflater = LayoutInflater.from(context)
    private val noHourText = "No hours defined"

    init {
        allEvents.observe(lifecycleOwner) {
            for (k in it.value.keys) {
                if (k !in isZoneOpen) {
                    isZoneOpen[k] = false
                }
            }
            notifyDataSetChanged()
        }
    }


    abstract inner class CustomViewHolder<T>(view: View) :
            RecyclerView.ViewHolder(view) {
        abstract fun bind(value: T)
        abstract fun unbind()
    }

    /**
     * Adapted ViewHolder for each item type
     * Takes the corresponding item type "tab" view
     */
    inner class ZoneViewHolder(private val view: View) :
            CustomViewHolder<String>(view) {
        private val zoneName = view.findViewById<TextView>(R.id.id_zone_name_text)

        override fun bind(value: String) {
            zoneName.text = allEvents[value]!!.first
            view.setOnClickListener {
                isZoneOpen[value] = !isZoneOpen[value]!!
                notifyDataSetChanged()
            }
        }

        override fun unbind() {
            //do nothing
        }

    }

    /**
     * Adapted ViewHolder for each item
     * Takes the corresponding item "tab" view
     */
    inner class EventViewHolder(private val view: View) :
            CustomViewHolder<Event>(view) {

        private lateinit var event: Event

        /**
         * Binds the value of the item to the layout of the item tab
         */
        override fun bind(value: Event) {
            this.event = value
            view.findViewById<TextView>(R.id.tvEventTitle).text = event.eventName
            view.findViewById<TextView>(R.id.tvEventStartDate).text = localDatetimeToString(event.startTime, noHourText)
            view.findViewById<TextView>(R.id.tvEventEndDate).text = localDatetimeToString(event.endTime, noHourText)
            view.findViewById<ImageButton>(R.id.idEditEventButton).setOnClickListener { modifyListener(event.eventId!!) }
            view.findViewById<ImageButton>(R.id.idDeleteEventButton).setOnClickListener {
                deleteListener(event.zoneId!!, event)
            }
        }

        override fun unbind() {
            //Do nothing
        }
    }


    override fun getItemCount(): Int {
        var count = 0
        for (isOpen in isZoneOpen.entries) {
            count += 1 + (if (!isOpen.value) 0 else allEvents[isOpen.key]?.second?.size ?: 0)
        }
        return count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder<*> {
        //Create the correct holder
        return when (viewType) {
            ZONE_HOLDER -> {
                val view = inflater.inflate(R.layout.card_zone_name, parent, false)
                ZoneViewHolder(view)
            }
            EVENT_HOLDER -> {
                val view = inflater.inflate(R.layout.card_event_by_zone, parent, false)
                EventViewHolder(view)
            }
            //Should not happen
            else -> throw IllegalArgumentException("wrong view type $viewType")
        }
    }

    override fun onViewRecycled(holder: CustomViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onBindViewHolder(holder: CustomViewHolder<*>, position: Int) {
        when (holder) {
            is ZoneViewHolder -> {
                var res = 0
                for (zoneId in allEvents.keys) {
                    // The zone is always displayed => added to count
                    if (res++ == position) {
                        // if this is the zone we are looking for, bind it
                        holder.bind(zoneId)
                        return
                    }

                    // Add the events to count only if they are visible in the recycler view
                    if (isZoneOpen[zoneId] == true) {
                        res += allEvents[zoneId]?.second?.size ?: 0
                    }
                }
            }
            is EventViewHolder -> {
                var res = 0
                for (zoneId in allEvents.keys) {
                    // The zone is always displayed => added to count
                    res++
                    // Add the events to count only if they are visible in the recycler view
                    if (isZoneOpen[zoneId] == true) {
                        for (event in allEvents[zoneId]?.second ?: listOf()) {
                            if (res++ == position) {
                                // if this is the event we are looking for, bind it
                                holder.bind(event)
                                return
                            }
                        }
                    }
                }
            }
            //Should not happen
            else -> throw java.lang.IllegalArgumentException("invalid position")

        }
    }

    override fun getItemViewType(position: Int): Int {
        var res = 0
        for (zone in allEvents.keys) {
            // The zone is always displayed => added to count
            if (res++ == position) {
                //Return the identifier for zone hodler
                return ZONE_HOLDER
            }
            // Add the events to count only if they are visible in the recycler view
            if (isZoneOpen[zone] == true) {
                for (event in allEvents[zone]?.second ?: listOf()) {
                    if (res++ == position) {
                        //Return the identifier for event holder
                        return EVENT_HOLDER
                    }
                }
            }
        }
        //should never happen
        return -1
    }

    companion object {
        private const val ZONE_HOLDER = 0
        private const val EVENT_HOLDER = 1
    }
}
