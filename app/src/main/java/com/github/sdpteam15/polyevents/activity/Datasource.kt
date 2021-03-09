package com.github.sdpteam15.polyevents.activity

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

class Datasource {
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUpcomingActivities(): List<Activity>{
        // TODO : Replace these stubs with a query to the database for (sorted) upcoming activities
        val activities = ArrayList<Activity>()

        activities.add(Activity(
            "Sushi demo",
            "Super hungry activity !",
            LocalDateTime.of(2021, 3, 7, 12, 15),
            1F,
            "The fish band",
            "Kitchen", null))

        activities.add(Activity(
            "Aqua Poney",
            "Super cool activity !",
            LocalDateTime.of(2021, 3, 7, 14, 15),
            3.5F,
            "The Aqua Poney team",
            "Swimming pool", null))

        activities.add(Activity(
            "Saxophone demo",
            "Super noisy activity !",
            LocalDateTime.of(2021, 3, 7, 17, 15),
            0.75F,
            "The music band",
            "Concert Hall", null))
        activities.add(Activity(
            "Sushi demo",
            "Super hungry activity !",
            LocalDateTime.of(2021, 3, 7, 12, 15),
            1F,
            "The fish band",
            "Kitchen", null))

        activities.add(Activity(
            "Aqua Poney",
            "Super cool activity !",
            LocalDateTime.of(2021, 3, 7, 14, 15),
            3.5F,
            "The Aqua Poney team",
            "Swimming pool", null))

        activities.add(Activity(
            "Saxophone demo",
            "Super noisy activity !",
            LocalDateTime.of(2021, 3, 7, 17, 15),
            0.75F,
            "The music band",
            "Concert Hall", null))
        activities.add(Activity(
            "Sushi demo",
            "Super hungry activity !",
            LocalDateTime.of(2021, 3, 7, 12, 15),
            1F,
            "The fish band",
            "Kitchen", null))

        activities.add(Activity(
            "Aqua Poney",
            "Super cool activity !",
            LocalDateTime.of(2021, 3, 7, 14, 15),
            3.5F,
            "The Aqua Poney team",
            "Swimming pool", null))

        activities.add(Activity(
            "Saxophone demo",
            "Super noisy activity !",
            LocalDateTime.of(2021, 3, 7, 17, 15),
            0.75F,
            "The music band",
            "Concert Hall", null))
        activities.add(Activity(
            "Sushi demo",
            "Super hungry activity !",
            LocalDateTime.of(2021, 3, 7, 12, 15),
            1F,
            "The fish band",
            "Kitchen", null))

        activities.add(Activity(
            "Aqua Poney",
            "Super cool activity !",
            LocalDateTime.of(2021, 3, 7, 14, 15),
            3.5F,
            "The Aqua Poney team",
            "Swimming pool", null))

        activities.add(Activity(
            "Saxophone demo",
            "Super noisy activity !",
            LocalDateTime.of(2021, 3, 7, 17, 15),
            0.75F,
            "The music band",
            "Concert Hall", null))
        activities.add(Activity(
            "Sushi demo",
            "Super hungry activity !",
            LocalDateTime.of(2021, 3, 7, 12, 15),
            1F,
            "The fish band",
            "Kitchen", null))

        activities.add(Activity(
            "Aqua Poney",
            "Super cool activity !",
            LocalDateTime.of(2021, 3, 7, 14, 15),
            3.5F,
            "The Aqua Poney team",
            "Swimming pool", null))

        activities.add(Activity(
            "Saxophone demo",
            "Super noisy activity !",
            LocalDateTime.of(2021, 3, 7, 17, 15),
            0.75F,
            "The music band",
            "Concert Hall", null))
        return activities
    }
}