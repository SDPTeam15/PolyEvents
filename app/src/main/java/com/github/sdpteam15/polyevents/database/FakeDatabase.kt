package com.github.sdpteam15.polyevents.database

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

object FakeDatabase : DatabaseInterface {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            initEvents()
        initItems()
    }

    private fun initItems() {
        items = arrayListOf("Scie-tronconneuse", "Bonnet de bain")
    }

    private var events: ArrayList<Event>? = null

    private var items: ArrayList<String>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        events = arrayListOf(
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "1", mutableSetOf("sushi", "japan", "cooking")
            ),
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "2"
            ),
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "3"
            ),
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "4"
            ),
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "5"
            ),
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "6"
            ),
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "7"
            ),
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "8"
            ),
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "9"
            ),
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "10"
            ),
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "11"
            ),
            Event(
                "Saxophone demo",
                "Super noisy activity !",
                LocalDateTime.of(2021, 3, 7, 17, 15),
                0.75F,
                "The music band",
                "Concert Hall", null, "12"
            ),
            Event(
                "Sushi demo",
                "Super hungry activity !",
                LocalDateTime.of(2021, 3, 7, 12, 15),
                1F,
                "The fish band",
                "Kitchen", null, "13"
            ),
            Event(
                "Aqua Poney",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                LocalDateTime.of(2021, 3, 7, 14, 15),
                3.5F,
                "The Aqua Poney team",
                "Swimming pool", null, "14"
            ),
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getEventFromId(id: String, profile: ProfileInterface): Event? {
        initEvents()
        return events!!.get(0)
    }

    override fun updateEvent(Event: Event, profile: ProfileInterface): Boolean {
        return true
    }

    override fun updateUserInformation(
        newValues: HashMap<String, String>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getUserInformation(
        user: Observable<UserInterface>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }
    override fun getItemsList(): MutableList<String> {
        return items!!
    }

    override fun addItem(item: String): Boolean {
        return items!!.add(item)
    }

    override fun removeItem(item: String): Boolean {
        return items!!.remove(item)
    }


}