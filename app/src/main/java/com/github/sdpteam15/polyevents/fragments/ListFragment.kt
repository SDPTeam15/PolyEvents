package com.github.sdpteam15.polyevents.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.ActivityActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.activity.ActivityItemAdapter
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelper
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelperInterface


const val EXTRA_ACTIVITY = "com.github.sdpteam15.polyevents.activity.ACTIVITY_ID"

/**
 * Shows the list of activities and displays them in a new activity when we click on one of them
 */
class ListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    private var currentQueryHelper = ActivitiesQueryHelper


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activities = currentQueryHelper.getUpcomingActivities()
        val fragmentView = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView = fragmentView.findViewById<RecyclerView>(R.id.recycler_activites_list)

        val openActivity = { activity: Activity ->
            val intent = Intent(inflater.context, ActivityActivity::class.java).apply {
                putExtra(EXTRA_ACTIVITY, activity.id)
            }
            startActivity(intent)
        }

        recyclerView.adapter = ActivityItemAdapter(activities, openActivity)

        recyclerView.setHasFixedSize(true)
        // Inflate the layout for this fragment
        return fragmentView
    }
}
