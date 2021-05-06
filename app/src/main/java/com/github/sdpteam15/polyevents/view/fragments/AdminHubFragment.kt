package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.view.activity.admin.EventManagementListActivity
import com.github.sdpteam15.polyevents.view.activity.admin.ItemRequestManagementActivity
import com.github.sdpteam15.polyevents.view.activity.admin.UserManagementListActivity
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementListActivity


/**
 * AdminHub fragment: the fragment containing all shortcuts to the admin management options
 */
class AdminHubFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewRoot = inflater.inflate(R.layout.fragment_admin_hub, container, false)
        viewRoot.findViewById<Button>(R.id.btnRedirectEventManager).setOnClickListener {
            val intent = Intent(inflater.context, EventManagementListActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.btnRedirectUserManagement).setOnClickListener {
            val intent = Intent(inflater.context, UserManagementListActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.btnRedirectItemReqManagement).setOnClickListener {
            val intent = Intent(inflater.context, ItemRequestManagementActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.btnRedirectZoneManagement).setOnClickListener {
            val intent = Intent(inflater.context, ZoneManagementListActivity::class.java)
            startActivity(intent)
        }
        return viewRoot
    }

}