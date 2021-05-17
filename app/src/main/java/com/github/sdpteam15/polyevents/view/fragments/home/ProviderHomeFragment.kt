package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.view.activity.ItemRequestActivity
import com.github.sdpteam15.polyevents.view.activity.ItemsAdminActivity
import com.github.sdpteam15.polyevents.view.activity.MainActivity

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
        MainActivity.instance!!.swish(viewRoot!!.findViewById(R.id.spinner_provider), UserRole.ORGANIZER)
        return viewRoot
    }
}