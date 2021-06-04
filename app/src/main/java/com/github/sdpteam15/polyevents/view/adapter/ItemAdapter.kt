package com.github.sdpteam15.polyevents.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.observable.ObservableList

/**
 * Adapts items to RecyclerView ItemsViews
 * @param lifecycleOwner the lifecycleOwner for the observers
 * @param items the list of items with their total quantities and their remaining quantities
 * @param addItemListener the listener to notify when we want to add an item to the DB
 * @param removeItemListener the listener to notify when we want to remove an item to the DB
 */
class ItemAdapter(
    lifecycleOwner: LifecycleOwner,
    private val items: ObservableList<Triple<Item, Int, Int>>,
    private val addItemListener: (Triple<Item, Int, Int>) -> Unit,
    private val removeItemListener: (Triple<Item, Int, Int>) -> Unit
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
        private val btnModify = view.findViewById<ImageButton>(R.id.id_modify_item)

        /**
         * Binds the values of each view of an event to the layout of an event
         */
        @SuppressLint("SetTextI18n")
        fun bind(item: Triple<Item, Int, Int>) {
            btnRemove.setOnClickListener {
                removeItemListener(item)
            }
            btnModify.setOnClickListener {
                addItemListener(item)
            }

            itemName.text = item.first.itemName
            itemType.text = item.first.itemType
            itemCount.text = item.third.toString() + "/" + item.second.toString()
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
        holder.itemView.setOnClickListener {
            addItemListener(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

