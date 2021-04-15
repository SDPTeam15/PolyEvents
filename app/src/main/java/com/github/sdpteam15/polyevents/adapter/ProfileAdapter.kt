package com.github.sdpteam15.polyevents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole

class ProfileAdapter(
    itemsAdminActivity: LifecycleOwner,
    private val items: ObservableList<UserProfile>
) : RecyclerView.Adapter<ProfileAdapter.ItemViewHolder>() {

    init {
        items.observe(itemsAdminActivity) {
            notifyDataSetChanged()
        }
    }

    /**
     * adapted ViewHolder for each item
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_list_item_name)
        private val btnEdit = view.findViewById<ImageButton>(R.id.id_edit_item)
        private val btnRemove = view.findViewById<ImageButton>(R.id.id_remove_item)

        /**
         * Binds the values of each view of an event to the layout of an event
         */
        fun bind(item: UserProfile) {
            btnEdit.setOnClickListener {
                items.edit(item)
            }
            btnRemove.setOnClickListener {
                items.remove(item)
            }
            itemName.text =
                if (item.userRole != UserRole.PARTICIPANT) "${item.profileName} (${item.userRole})" else item.profileName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_material_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}