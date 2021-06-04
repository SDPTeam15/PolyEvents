package com.github.sdpteam15.polyevents.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.fragments.ProfileFragment
import kotlinx.coroutines.Dispatchers

class ProfileAdapter(
    private val profileFragment: ProfileFragment,
    private val user: UserEntity,
    private val items: ObservableList<UserProfile>
) : RecyclerView.Adapter<ProfileAdapter.ItemViewHolder>() {

    init {
        items.observe(profileFragment) {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.Main) {
                notifyDataSetChanged()
            }
        }
    }

    /**
     * adapted ViewHolder for each profile
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_list_profile_name)
        private val btnEdit = view.findViewById<ImageButton>(R.id.id_profile_edit_item)
        private val btnRemove = view.findViewById<ImageButton>(R.id.id_profile_remove_item)

        /**
         * Binds the values of each view of an event to the layout of an profile
         */
        fun bind(item: UserProfile) {
            val moreText = if (item.defaultProfile) {
                "(Default)"
            } else {
                ""
            }

            R.id.id_edittext_item_name
            itemName.text =
                if (item.userRole != UserRole.PARTICIPANT)
                    "$moreText ${item.profileName} (${item.userRole})"
                else "$moreText ${item.profileName}"

            if (item.defaultProfile) {
                if (item.userRole == UserRole.ADMIN) {
                    btnEdit.visibility = View.VISIBLE
                } else {
                    btnEdit.visibility = View.INVISIBLE
                }
                btnRemove.visibility = View.INVISIBLE
            } else {
                    btnRemove.visibility = View.VISIBLE
                    btnEdit.visibility = View.VISIBLE
            }

            btnRemove.setOnClickListener {
                items.remove(item)
            }
            btnEdit.setOnClickListener {
                profileFragment.editProfile(item)
                user.loadSuccess = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_profile, parent, false)
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