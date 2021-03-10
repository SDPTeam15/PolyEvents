package com.github.sdpteam15.polyevents.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.helper.ActivitiesQueryHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        val linearLayout = fragmentView.findViewById<LinearLayout>(R.id.upcoming_activities_list)

        val activities = ActivitiesQueryHelper.getUpcomingActivities()

        for(activity: Activity in activities) {
            setupActivityTab(activity, linearLayout)
        }

        return fragmentView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupActivityTab(activity: Activity, incomingActivities: LinearLayout) {
        val activityTab = layoutInflater.inflate(R.layout.tab_activity, null)

        val activityName = activityTab.findViewById<TextView>(R.id.id_activity_name_text)
        activityName.text = activity.name

        val activitySchedule = activityTab.findViewById<TextView>(R.id.id_activity_schedule_text)
        activitySchedule.text = "at ${activity.getTime()}"

        val activityZone = activityTab.findViewById<TextView>(R.id.id_activity_zone)
        activityZone.text = activity.zone

        // TODO : set the icon of the activity
        val activityIcon = activityTab.findViewById<ImageView>(R.id.id_activity_icon)
        //activityIcon.setImageBitmap(activity.icon)

        incomingActivities.addView(activityTab)
    }
}