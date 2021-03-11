package com.github.sdpteam15.polyevents.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.ActivityActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.activity.ActivityItemAdapter
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelper
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelperInterface
import com.github.sdpteam15.polyevents.helper.HelperFunctions


const val EXTRA_ACTIVITY = "com.github.sdpteam15.polyevents.activity.ACTIVITY_ID"

/**
 * Shows the list of activities and displays them in a new activity when we click on one of them
 */
class ListFragment : Fragment() {

    var currentQueryHelper: ActivitiesQueryHelperInterface = ActivitiesQueryHelper
        set(value) {
            field = value
            updateContent()
        }
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = fragmentView.findViewById<RecyclerView>(R.id.recycler_activites_list)

        val activities = currentQueryHelper.getUpcomingActivities()

        val openActivity = { activity: Activity ->
            val intent = Intent(inflater.context, ActivityActivity::class.java).apply {
                putExtra(EXTRA_ACTIVITY, activity.id)
            }
            startActivity(intent)
        }

        recyclerView.adapter = ActivityItemAdapter(activities, openActivity)

        recyclerView.setHasFixedSize(false)
        // Inflate the layout for this fragment
        return fragmentView
    }

    /**
     * Update the content of the upcoming activities
     */
    fun updateContent() {
        // Remove all the content first
        HelperFunctions.refreshFragment(fragmentManager, this)

    }

}
