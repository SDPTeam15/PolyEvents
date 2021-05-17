package com.github.sdpteam15.polyevents.view.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.view.activity.MainActivity

class StaffHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_home_staff, container, false)
        MainActivity.instance!!.swish(viewRoot!!.findViewById(R.id.spinner_staff), UserRole.STAFF)
        return viewRoot
    }
}