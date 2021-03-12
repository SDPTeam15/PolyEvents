package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.database.DatabaseInterface

object FakeDatabase : DatabaseInterface {

    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> =
        emptyList()

    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean =
        true

    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean = true

    override fun updateProfile(profile: ProfileInterface, user: UserInterface): Boolean = true

    override fun getListActivity(
        matcher: String?,
        number: Int?,
        profile: ProfileInterface
    ): List<Activity> = emptyList()

    override fun getUpcomingActivities(number: Int, profile: ProfileInterface): List<Activity> =
        emptyList()


    override fun getActivityFromId(id: String, profile: ProfileInterface): Activity? = null

    override fun updateActivity(Activity: Activity, profile: ProfileInterface): Boolean = true
}