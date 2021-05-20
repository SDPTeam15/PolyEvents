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
import com.github.sdpteam15.polyevents.view.activity.ItemRequestActivity
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.activityprovider.EventManagementActivityProvider
import com.github.sdpteam15.polyevents.view.activity.admin.EventManagementListActivity

class ProviderHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_home_provider, container, false)
        viewRoot.findViewById<Button>(R.id.id_request_button).setOnClickListener {
            val intent = Intent(activity, ItemRequestActivity::class.java)
            startActivity(intent)
        }

        viewRoot.findViewById<Button>(R.id.btnRedirectEventManager).setOnClickListener {
            val intent = Intent(activity, EventManagementListActivity::class.java)
            intent.putExtra(EventManagementListActivity.ORGANISER_LIST,"organiser")
            startActivity(intent)
        }

        viewRoot.findViewById<Button>(R.id.btnRedirectProviderEditRequests).setOnClickListener {
            val intent = Intent(activity, EventManagementActivityProvider::class.java)
            startActivity(intent)
        }


        MainActivity.instance!!.switchRoles(viewRoot!!.findViewById(R.id.spinner_provider), UserRole.ORGANIZER)
        return viewRoot
    }
}