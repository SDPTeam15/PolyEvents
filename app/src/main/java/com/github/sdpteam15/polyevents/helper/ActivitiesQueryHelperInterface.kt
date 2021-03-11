package com.github.sdpteam15.polyevents.helper

import com.github.sdpteam15.polyevents.activity.Activity

const val NBR_UPCOMING_ACTIVITIES = 3

/**
 * Helper interface for making queries about activities
 */
interface ActivitiesQueryHelperInterface {

    /**
     * Query the upcoming activities
     * @param nbr : the number of activities to retrieve
     * @return List of activities in upcoming order (closest first)
     */
    fun getUpcomingActivities(nbr: Int= NBR_UPCOMING_ACTIVITIES): List<Activity>
}