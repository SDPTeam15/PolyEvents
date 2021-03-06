package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.admin.*


/**
 * AdminHub fragment: the fragment containing all shortcuts to the admin management options
 */
class AdminHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewRoot = inflater.inflate(R.layout.fragment_home_admin, container, false)
        viewRoot.findViewById<Button>(R.id.id_event_manager_button).setOnClickListener {
            val intent = Intent(inflater.context, EventManagementListActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.id_user_management_button).setOnClickListener {
            val intent = Intent(inflater.context, UserManagementListActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.id_item_request_management_button).setOnClickListener {
            val intent = Intent(inflater.context, ItemRequestManagementActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.id_zone_management_button).setOnClickListener {
            val intent = Intent(inflater.context, ZoneManagementListActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.id_route_manager_button).setOnClickListener {
            val intent = Intent(inflater.context, RouteManagementActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.id_items_list_management_button).setOnClickListener {
            val intent = Intent(inflater.context, ItemsAdminActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.id_event_edit_button).setOnClickListener {
            val intent = Intent(inflater.context, EventEditManagementActivity::class.java)
            startActivity(intent)
        }

        MainActivity.instance!!.switchRoles(
            viewRoot.findViewById(R.id.spinner_admin),
            UserRole.ADMIN
        )
        return viewRoot
    }
}