package com.github.sdpteam15.polyevents.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class ZoneItemAdapter(
    private val zones: ObservableList<Zone>,
    val listener: (Zone) -> Unit,
    val deleteListener: (Zone) -> Unit
) : RecyclerView.Adapter<ZoneItemAdapter.ItemViewHolder>() {
    /**
     * adapted ViewHolder for each Zone
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val eventName = view.findViewById<TextView>(R.id.id_zone_name_text)
        private val btnRemove = view.findViewById<ImageButton>(R.id.id_zone_remove_item)
        private val btnEdit = view.findViewById<ImageButton>(R.id.id_zone_modify_item)

        /**
         * Binds the values of each field of a zone to the layout of an event
         */
        fun bind(zone: Zone) {
            eventName.text = zone.zoneName

            btnRemove.setOnClickListener {
                deleteListener(zone)
            }
            btnEdit.setOnClickListener {
                listener(zone)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_zone, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return zones.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val zone = zones[position]
        holder.bind(zone)
        holder.itemView.setOnClickListener {
            listener(zone)
        }
    }
}