package com.github.sdpteam15.polyevents.veiw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.UserEntity

class UserListAdapter(
    private val users: ObservableList<UserEntity>,
    private val listener: (UserEntity) -> Unit
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    /**
     * adapted ViewHolder for each users
     * Takes the corresponding user view
     */
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userName = view.findViewById<TextView>(R.id.user_name_list)
        private val userUsername = view.findViewById<TextView>(R.id.user_username_list)
        private val userEmail = view.findViewById<TextView>(R.id.user_email_list)

        /**
         * Binds the values of each field of an user to the layout of an user
         */
        fun bind(user: UserEntity) {
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