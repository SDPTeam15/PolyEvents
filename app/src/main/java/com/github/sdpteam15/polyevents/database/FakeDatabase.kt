package com.github.sdpteam15.polyevents.database

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.Rank
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
object FakeDatabase : DatabaseInterface {
    init {
        initEvents()
        initProfiles()
        initItems()
    }

    private fun initItems() {
        items = mutableListOf("Scie-tronconneuse", "Bonnet de bain")
    }

    private lateinit var events: MutableList<Event>
    private lateinit var profiles: MutableList<ProfileInterface>
    private lateinit var items: MutableList<String>

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        events = mutableListOf()
        events.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen",
                "1",
                mutableSetOf("sushi", "japan", "cooking")
            )
        )
        events.add(
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool",
                "2"
            )
        )
        events.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall",
                "3"
            )
        )
        events.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen",
                "4"
            )
        )
        events.add(
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool",
                "5"
            )
        )
        events.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall",
                "6"
            )
        )
        events.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen",
                "7"
            )
        )
        events.add(
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool",
                "8"
            )
        )
        events.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall",
                "9"
            )
        )
        events.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen",
                "10"
            )
        )
        events.add(
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool",
                "11"
            )
        )
        events.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall",
                "12"
            )
        )
        events.add(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen",
                "13"
            )
        )
        events.add(
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool",
                "14"
            )
        )
        events.add(
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall",
                "15"
            )
        )
    }

    private fun initProfiles() {
        profiles = mutableListOf()
    }

    override val currentUser: DatabaseUserInterface = object : DatabaseUserInterface {
        override var displayName: String = "FakeName"
        override var uid: String = "FakeUID"
        override var email: String = "Fake@mail.ch"
        override val rank: Rank = Rank.Admin
    }

    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> =
        profiles


    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean =
        profiles.add(profile)

    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean = profiles.remove(profile)

    override fun updateProfile(profile: ProfileInterface, user: UserInterface): Boolean = true

    override fun updateProfile(
        newValues: Map<String, String>,
        pid: String,
        userAccess: UserInterface
    ): Observable<Boolean> = Observable(true)

    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: ProfileInterface
    ): List<Event> {
        val res = mutableListOf<Event>()
        for (v in events) {
            res.add(v)
            if (res.size == number)
                break
        }
        return res
    }

    override fun getUpcomingEvents(number: Int, profile: ProfileInterface): List<Event> =
        getListEvent("", number, profile)

    override fun getEventFromId(id: String, profile: ProfileInterface): Event =
        events[0]

    override fun updateEvent(Event: Event, profile: ProfileInterface): Boolean = true

    override fun updateUserInformation(
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> =
        Observable(true)

    override fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface
    ): Observable<Boolean> =
        Observable(true)

    override fun getAvailableItems(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        map["230V plug"] = 20
        map["Cord rewinder (50m)"] = 10
        map["Cooking plate"] = 5
        map["Cord rewinder (100m)"] = 1
        map["Cord rewinder (10m)"] = 30
        map["Fridge (large)"] = 2
        map["Fridge (small)"] = 10

        return map
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> {
        isInDb.value = true
        return Observable(true)
    }

    override fun getUserInformation(
        user: Observable<UserInterface>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> {
        user.value = User.invoke(currentUser)
        return Observable(true)
    }

    override fun getProfileById(
        profile: Observable<ProfileInterface>,
        id: String,
        profileAccess: ProfileInterface
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getItemsList(): MutableList<String> {
        return items
    }

    override fun addItem(item: String): Boolean {
        return items.add(item)
    }

    override fun removeItem(item: String): Boolean {
        return items.remove(item)
    }


}