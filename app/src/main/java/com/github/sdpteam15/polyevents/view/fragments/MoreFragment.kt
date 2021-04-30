package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.view.activity.ItemRequestActivity
import com.github.sdpteam15.polyevents.view.activity.ItemsAdminActivity


/**
 * A simple [Fragment] subclass
 */
class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_more, container, false)
        fragmentView.findViewById<Button>(R.id.btn_admin_items_list).setOnClickListener {
            val intent = Intent(inflater.context, ItemsAdminActivity::class.java)
            startActivity(intent)
        }
        fragmentView.findViewById<Button>(R.id.id_request_button).setOnClickListener {
            val intent = Intent(activity, ItemRequestActivity::class.java)
            startActivity(intent)
        }

        return fragmentView
    }
}