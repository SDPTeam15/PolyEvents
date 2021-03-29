package com.github.sdpteam15.polyevents.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.admin.EventManagementActivity
import com.github.sdpteam15.polyevents.admin.ItemRequestManagementActivity
import com.github.sdpteam15.polyevents.admin.UserManagementActivity
import com.github.sdpteam15.polyevents.admin.ZoneManagementActivity


/**
 * A simple [Fragment] subclass.
 * Use the [AdminHubFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminHubFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewRoot = inflater.inflate(R.layout.fragment_admin_hub, container, false)
        viewRoot.findViewById<Button>(R.id.btnRedirectEventManager).setOnClickListener {
            val intent = Intent(inflater.context, EventManagementActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.btnRedirectUserManagement).setOnClickListener {
            val intent = Intent(inflater.context, UserManagementActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.btnRedirectItemReqManagement).setOnClickListener {
            val intent = Intent(inflater.context, ItemRequestManagementActivity::class.java)
            startActivity(intent)
        }
        viewRoot.findViewById<Button>(R.id.btnRedirectZoneManagement).setOnClickListener {
            val intent = Intent(inflater.context, ZoneManagementActivity::class.java)
            startActivity(intent)
        }
        return viewRoot
    }

}