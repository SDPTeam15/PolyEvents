package com.github.sdpteam15.polyevents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.UserEntity

class UserListAdapter(
    private val users: ObservableList<UserEntity>,
    private val listener: (UserEntity) -> Unit
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    /**
     * adapted ViewHolder for each event
     * Takes the corresponding event view
     */
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userName = view.findViewById<TextView>(R.id.user_name_list)
        private val userUsername = view.findViewById<TextView>(R.id.user_username_list)
        private val userEmail = view.findViewById<TextView>(R.id.user_email_list)

        /**
         * Binds the values of each field of an event to the layout of an event
         */
        fun bind(user: UserEntity) {
            println("In testo")
            userName.text = user.name
            userUsername.text = user.username
            userEmail.text = user.email
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_users_list, parent, false)
        return UserViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        println("In testo 2")
        val user = users[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            listener(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}