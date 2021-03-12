package com.github.sdpteam15.polyevents.database

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import java.time.LocalDateTime
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

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
            initEvents()
        }
    }

    private var events: ArrayList<Event>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        events = ArrayList()
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry event !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "1", mutableSetOf("sushi", "japan", "cooking")
            )
        )
        events?.add(
            Event(
                "Aqua Poney",
                "Super cool event !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the event we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "2"
            )
        )
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy event !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "3"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry event !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "4"
            )
        )
        events?.add(
            Event(
                "Aqua Poney",
                "Super cool event !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the event we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "5"
            )
        )
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy event !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "6"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry event !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "7"
            )
        )
        events?.add(
            Event(
                "Aqua Poney",
                "Super cool event !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the event we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "8"
            )
        )
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy event !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "9"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry event !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "10"
            )
        )
        events?.add(
            Event(
                "Aqua Poney",
                "Super cool event !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the event we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "11"
            )
        )
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy event !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "12"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry event !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "13"
            )
        )
        events?.add(
            Event(
                "Aqua Poney",
                "Super cool event !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the event we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "14"
            )
        )
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy event !",
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

    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: ProfileInterface
    ): List<Event> = events as List<Event>

    override fun getUpcomingEvents(number: Int, profile: ProfileInterface): List<Event> =
        events as List<Event>

    override fun getEventFromId(id: String, profile: ProfileInterface): Event? = try {
        events?.single { event -> event.id == id }
    } catch (e: NoSuchElementException) {
        null
    }

    override fun updateEvent(Event: Event, profile: ProfileInterface): Boolean = true
}