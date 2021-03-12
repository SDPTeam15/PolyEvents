package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.user.Profile.Companion.CurrentProfile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User.Companion.currentUser
import com.github.sdpteam15.polyevents.user.UserInterface

const val NUMBER_UPCOMING_ACTIVITIES = 3

/**
 * Database interface
 */
interface DatabaseInterface {

    /**
     * Current user of this database
     */
    val currentUser : DatabaseUserInterface?

    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param user user for database access
     * @return list of profile of a user uid
     */
    fun getListProfile(
        uid: String,
        user: UserInterface = currentUser as UserInterface
    ): List<ProfileInterface>

    /**
     * Add profile to a user
     * @param profile profile to add
     * @param uid uid
     * @param user user for database access
     * @return if the operation succeed
     */
    fun addProfile(
        profile: ProfileInterface, uid: String,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Remove profile from a user
     * @param profile profile to remove
     * @param uid uid
     * @param user user for database access
     * @return if the operation succeed
     */
    fun removeProfile(
        profile: ProfileInterface, uid: String = (currentUser as UserInterface).uid,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Update profile
     * @param Activity Activity to update
     * @param profile profile for database access
     * @return if the operation succeed
     */
    fun updateProfile(
        profile: ProfileInterface,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Get list of activity
     * @param matcher matcher for the recherche
     * @param number maximum of result
     * @param profile profile for database access
     * @return list of activity
     */
    fun getListActivity(
        matcher: String? = null, number: Int? = null,
        profile: ProfileInterface = CurrentProfile
    ): List<Activity>

    /**
     * Query the upcoming activities
     * @param number : the number of activities to retrieve
     * @param profile profile for database access
     * @return List of activities in upcoming order (closest first)
     */
    fun getUpcomingActivities(
        number: Int = NUMBER_UPCOMING_ACTIVITIES,
        profile: ProfileInterface = CurrentProfile
    ): List<Activity>

    /**
     * Get activity from ID
     * @param id ID of the Activity
     * @param profile profile for database access
     * @return Activity corresponding to the given ID
     */
    fun getActivityFromId(
        id: String,
        profile: ProfileInterface = CurrentProfile
    ): Activity?

    /**
     * Update or request an update for an activity
     * @param Activity Activity to update
     * @param profile profile for database access
     */
    fun updateActivity(
        Activity: Activity,
        profile: ProfileInterface = CurrentProfile
    ): Boolean
}