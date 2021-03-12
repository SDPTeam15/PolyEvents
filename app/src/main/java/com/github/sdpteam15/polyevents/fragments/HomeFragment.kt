package com.github.sdpteam15.polyevents.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.database.Database.currentDatabase

/**
 * The fragment for the home page.
 */
class HomeFragment : Fragment() {

    private lateinit var listUpcomingActivitiesLayout: LinearLayout

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
        listUpcomingActivitiesLayout =
            fragmentView.findViewById<LinearLayout>(R.id.id_upcoming_activities_list)

        updateContent()

        return fragmentView
    }

    /**
     * Update the content of the upcoming activities
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateContent() {
        // Remove all the content first
        listUpcomingActivitiesLayout.removeAllViews()

        val activities = currentDatabase.getUpcomingActivities()

        for (activity: Activity in activities) {
            setupActivityTab(activity)
        }
    }

    /**
     * Setup the layout for an activity tab and add it to the layout provided
     * @param activity : the activity to add as a tab
     * @param incomingActivities : the linear layout where to add this new activity tab
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupActivityTab(activity: Activity) {
        val activityTab = layoutInflater.inflate(R.layout.tab_activity, null)

        activityTab.findViewById<TextView>(R.id.id_activity_name_text).text = activity.name

        activityTab.findViewById<TextView>(R.id.id_activity_schedule_text).text =
            getString(R.string.at_hour_text, activity.getTime())

        activityTab.findViewById<TextView>(R.id.id_activity_zone).text = activity.zone

        activityTab.findViewById<TextView>(R.id.id_activity_description).text = activity.description

        // TODO : set the icon of the activity
        //activityTab.findViewById<ImageView>(R.id.id_activity_icon).setImageBitmap(activity.icon)

        listUpcomingActivitiesLayout.addView(activityTab)
    }
}