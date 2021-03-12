package com.github.sdpteam15.polyevents.database

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import java.time.LocalDateTime

/**
 * Database
 */
class Database : DatabaseInterface {
    companion object {
        var mutableCurrentDatabase: DatabaseInterface? = null
        var currentDatabase: DatabaseInterface
            get() {
                mutableCurrentDatabase = mutableCurrentDatabase ?: Database()
                return mutableCurrentDatabase!!
            }
            set(value) {
                mutableCurrentDatabase = value
            }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initActivities()
        }
    }

    private var activities: ArrayList<Activity>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initActivities() {
        activities = ArrayList()
        activities?.add(
            Activity(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "1", mutableSetOf("sushi", "japan", "cooking")
            )
        )
        activities?.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "2"
            )
        )
        activities?.add(
            Activity(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "3"
            )
        )
        activities?.add(
            Activity(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "4"
            )
        )
        activities?.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "5"
            )
        )
        activities?.add(
            Activity(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "6"
            )
        )
        activities?.add(
            Activity(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "7"
            )
        )
        activities?.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "8"
            )
        )
        activities?.add(
            Activity(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "9"
            )
        )
        activities?.add(
            Activity(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "10"
            )
        )
        activities?.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "11"
            )
        )
        activities?.add(
            Activity(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "12"
            )
        )
        activities?.add(
            Activity(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "13"
            )
        )
        activities?.add(
            Activity(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "14"
            )
        )
        activities?.add(
            Activity(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "15"
            )
        )
    }

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
    ): List<Activity> = activities as List<Activity>

    override fun getUpcomingActivities(number: Int, profile: ProfileInterface): List<Activity> =
        activities as List<Activity>

    override fun getActivityFromId(id: String, profile: ProfileInterface): Activity? = try {
        activities?.single { activity -> activity.id == id }
    } catch (e: NoSuchElementException) {
        null
    }

    override fun updateActivity(Activity: Activity, profile: ProfileInterface): Boolean = true
}