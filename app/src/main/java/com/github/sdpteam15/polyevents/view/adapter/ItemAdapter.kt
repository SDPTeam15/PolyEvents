package com.github.sdpteam15.polyevents.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.Item

/**
 * Adapts items to RecyclerView ItemsViews
 */
class ItemAdapter(
    lifecycleOwner: LifecycleOwner,
    private val items: ObservableList<Pair<Item, Int>>,
    private val listener: (Pair<Item, Int>) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    init {
        items.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    /**
     * adapted ViewHolder for each item
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_item_list_name)
        private val itemCount = view.findViewById<TextView>(R.id.id_item_list_count)
        private val itemType = view.findViewById<TextView>(R.id.id_item_list_type)
        private val btnRemove = view.findViewById<ImageButton>(R.id.id_remove_item)

        /**
         * Binds the values of each view of an event to the layout of an event
         */
        fun bind(item: Pair<Item, Int>) {
            btnRemove.setOnClickListener {
                items.remove(item)
            }
            itemName.text = item.first.itemName
            itemType.text = item.first.itemType
            itemCount.text = item.second.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_material_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener{
            listener(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

