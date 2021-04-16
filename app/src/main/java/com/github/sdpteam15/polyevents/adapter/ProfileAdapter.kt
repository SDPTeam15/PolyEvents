package com.github.sdpteam15.polyevents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.fragments.ProfileFragment
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole

class ProfileAdapter(
    private val profileFragment: ProfileFragment,
    private val items: ObservableList<UserProfile>
) : RecyclerView.Adapter<ProfileAdapter.ItemViewHolder>() {

    init {
        items.observe {
            notifyDataSetChanged()
        }
    }

    /**
     * adapted ViewHolder for each item
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_list_profile_name)
        private val btnEdit = view.findViewById<ImageButton>(R.id.id_profile_edit_item)
        private val btnRemove = view.findViewById<ImageButton>(R.id.id_profile_remove_item)

        /**
         * Binds the values of each view of an event to the layout of an event
         */
        fun bind(item: UserProfile) {

            R.id.id_edittext_item_name
            itemName.text =
                if (item.userRole != UserRole.PARTICIPANT) "${item.profileName} (${item.userRole})" else item.profileName
            btnRemove.setOnClickListener {
                items.remove(item)
            }
            btnEdit.setOnClickListener {
                profileFragment.editProfile(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_profile, parent, false)
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