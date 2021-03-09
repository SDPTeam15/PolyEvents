package com.github.sdpteam15.polyevents.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelper

/**
 * The fragment for the home page.
 */
class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        val linearLayout = fragmentView.findViewById<LinearLayout>(R.id.id_upcoming_activities_list)

        val activities = ActivitiesQueryHelper.getUpcomingActivities()

        for (activity: Activity in activities) {
            setupActivityTab(activity, linearLayout)
        }

        return fragmentView
    }

    /**
     * Setup the layout for an activity tab and add it to the layout provided
     * @param activity : the activity to add as a tab
     * @param incomingActivities : the linear layout where to add this new activity tab
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupActivityTab(activity: Activity, incomingActivities: LinearLayout) {
        val activityTab = layoutInflater.inflate(R.layout.tab_activity, null)

        val activityName = activityTab.findViewById<TextView>(R.id.id_activity_name_text)
        activityName.text = activity.name

        val activitySchedule = activityTab.findViewById<TextView>(R.id.id_activity_schedule_text)
        activitySchedule.text =  getString(R.string.at_hour_text, activity.getTime())

        val activityZone = activityTab.findViewById<TextView>(R.id.id_activity_zone)
        activityZone.text = activity.zone

        val activityDescription = activityTab.findViewById<TextView>(R.id.id_activity_description)
        activityDescription.text = activity.description

        // TODO : set the icon of the activity
        val activityIcon = activityTab.findViewById<ImageView>(R.id.id_activity_icon)
        //activityIcon.setImageBitmap(activity.icon)

        incomingActivities.addView(activityTab)
    }
}