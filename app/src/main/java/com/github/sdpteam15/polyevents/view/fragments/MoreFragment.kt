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
        return fragmentView
    }
}