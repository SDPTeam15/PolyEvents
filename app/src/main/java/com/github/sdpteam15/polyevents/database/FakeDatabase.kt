package com.github.sdpteam15.polyevents.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.emptyList
import kotlin.collections.mutableSetOf
import kotlin.collections.single

object FakeDatabase : DatabaseInterface {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            initEvents()
    }

    private var events: ArrayList<Event>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        events = ArrayList()
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "1", mutableSetOf("sushi", "japan", "cooking")
            )
        )
        events?.add(
            Event(
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
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "3"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "4"
            )
        )
        events?.add(
            Event(
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
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "6"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "7"
            )
        )
        events?.add(
            Event(
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
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "9"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "10"
            )
        )
        events?.add(
            Event(
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
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "12"
            )
        )
        events?.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "13"
            )
        )
        events?.add(
            Event(
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
        events?.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "15"
            )
        )
    }

    override val currentUser: DatabaseUserInterface?
        get() = null

    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> =
        emptyList()


    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean {
        return true //TODO
    }


    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean {
        return false //TODO
    }

    override fun removeProfile(
        profile: ProfileInterface,
        success: MutableLiveData<Boolean>,
        uid: String,
        user: UserInterface
    ) {
        //TODO
    }


    override fun updateProfile(profile: ProfileInterface, user: UserInterface): Boolean {
        return false//TODO
    }

    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: ProfileInterface
    ): List<Event> {
        return events!!
    }

    override fun getUpcomingEvents(number: Int, profile: ProfileInterface): List<Event> {
        return events!!
    }

    override fun getEventFromId(id: String, profile: ProfileInterface): Event? {
        return null
    }

    override fun updateEvent(Event: Event, profile: ProfileInterface): Boolean {
        return true
    }

    override fun updateUserInformation(
        newValues: HashMap<String, String>,
        success: MutableLiveData<String>,
        uid: String,
        userAccess: UserInterface
    ):MutableLiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override fun inDatabase(
        isInDb: MutableLiveData<Boolean>,
        uid: String,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getUserInformation(
        listener: MutableLiveData<User>,
        uid: String,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        TODO("Not yet implemented")
    }
}