package com.github.sdpteam15.polyevents.item_request

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.ItemsAdminActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item

/**
 * Adapts items to RecyclerView ItemsViews
 */
class ItemAdapter(itemsAdminActivity: ItemsAdminActivity, items : ObservableList<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val items = items

    init {
        currentDatabase.getItemsList(items).observe {
            if (it != true)
               println("query not satisfied")
        }
        items.observeRemove(itemsAdminActivity) {
            Log.d("ItemAdapter","pd")
            if (it != null) {
                Log.d("ItemAdapter", "ongjen")
                currentDatabase.removeItem(it)
            }
        }
        items.observe(itemsAdminActivity) {
            Log.d("ItemAdapter","pd")
            notifyDataSetChanged()
        }
    }

    /**
     * adapted ViewHolder for each item
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // TODO: Consider adding field for itemType
        private val itemName = view.findViewById<TextView>(R.id.id_list_item_name)
        private val btnRemove = view.findViewById<ImageButton>(R.id.id_remove_item)

        /**
         * Binds the values of each view of an event to the layout of an event
         */
        fun bind(item: Item) {
            btnRemove.setOnClickListener {
                items.remove(item)
            }
            itemName.text = item.itemType.itemtype + item.itemId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_material_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]!!
        holder.bind(item)
        /*
        holder.itemView.setOnClickListener {

            onItemClickListener(item)
        }*/
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

