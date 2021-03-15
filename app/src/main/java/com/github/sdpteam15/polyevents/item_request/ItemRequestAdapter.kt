package com.github.sdpteam15.polyevents.item_request

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R

/**
 * Adapts items to RecyclerView's ItemViewHolders
 * Takes :
 * - The list of available items to adapt
 * - A listener that will be triggered on click on a checkbox of an item view holder
 */
class ItemRequestAdapter(
    private val availableItems: List<String>,
    private val onItemCheckChangeListener: (String, Boolean) -> Boolean
) : RecyclerView.Adapter<ItemRequestAdapter.ItemViewHolder>() {

    /**
     * Adapted ViewHolder for each item
     * Takes the corresponding item "tab" view
     */
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_item_name)
        private val itemCheckBox = view.findViewById<CheckBox>(R.id.id_item_requested)

        /**
         * Binds the value of the item to the layout of the item tab
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: String) {
            itemName.text = item
            itemCheckBox.setOnClickListener { view ->
                val isChecked = (view as CheckBox).isChecked
                onItemCheckChangeListener(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = availableItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return availableItems.size
    }
}