package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.activityprovider.MyItemRequestsActivity
import com.github.sdpteam15.polyevents.view.activity.staff.EXTRA_ID_USER_STAFF
import com.github.sdpteam15.polyevents.view.activity.staff.StaffRequestsActivity

class StaffHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_home_staff, container, false)
        MainActivity.instance!!.switchRoles(viewRoot!!.findViewById(R.id.spinner_staff), UserRole.STAFF)
        viewRoot.findViewById<Button>(R.id.id_deliveries_button).setOnClickListener {
            val intent = Intent(activity, StaffRequestsActivity::class.java)
            intent.putExtra(EXTRA_ID_USER_STAFF, Database.currentDatabase.currentUser!!.uid)
            startActivity(intent)
        }
        return viewRoot
    }
}