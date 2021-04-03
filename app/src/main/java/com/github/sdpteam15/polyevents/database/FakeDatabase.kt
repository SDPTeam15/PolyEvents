package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.*
import java.time.LocalDateTime
import java.util.*

object FakeDatabase : DatabaseInterface {
    init {
        initEvents()
        initProfiles()
        initItems()
    }

    private fun initItems() {
        items = mutableListOf(
            Item("micro1", ItemType.MICROPHONE),
            Item("plug2", ItemType.PLUG)
        )
    }

    private lateinit var events: MutableList<Event>
    private lateinit var profiles: MutableList<UserProfile>
    private lateinit var items: MutableList<Item>

    private fun initEvents() {
        events = mutableListOf()
        events.add(
            Event(
                eventId = "event1",
                eventName = "Sushi demo",
                description = "Super hungry activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                organizer = "The fish band",
                zoneName = "Kitchen",
                tags = mutableSetOf("sushi", "japan", "cooking")
            )
        )

        events.add(
            Event(
                eventId = "event2",
                eventName = "Saxophone demo",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
                organizer = "The music band",
                zoneName = "Concert Hall"
            )
        )

        events.add(
            Event(
                eventId = "event3",
                eventName = "Aqua Poney",
                description = "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
                organizer = "The Aqua Poney team",
                zoneName = "Swimming pool"
            )
        )
    }

    private fun initProfiles() {
        profiles = mutableListOf()
    }

    val CURRENT_USER: UserEntity = UserEntity(
        uid = "FakeUID",
        displayName = "FakeName",
        email = "Fake@mail.ch"
    )

    override val currentUser: UserEntity?
        get() = TODO("Not yet implemented")
    override val currentProfile: UserProfile?
        get() = TODO("Not yet implemented")

    override fun getProfilesList(uid: String, user: UserEntity?): List<UserProfile> =
        profiles


    override fun addProfile(profile: UserProfile, uid: String, user: UserEntity?): Boolean =
        profiles.add(profile)

    override fun removeProfile(
        profile: UserProfile,
        uid: String?,
        user: UserEntity?
    ): Boolean = profiles.remove(profile)

    override fun updateProfile(profile: UserProfile, user: UserEntity?): Boolean = true

    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: UserProfile?
    ): List<Event> {
        val res = mutableListOf<Event>()
        for (v in events) {
            res.add(v)
            if (res.size == number)
                break
        }
        return res
    }

    override fun getUpcomingEvents(number: Int, profile: UserProfile?): List<Event> =
        getListEvent("", number, profile)

    override fun getEventFromId(id: String, profile: UserProfile?): Event =
        events[0]

    override fun updateEvent(Event: Event, profile: UserProfile?): Boolean = true

    override fun updateUserInformation(
        newValues: HashMap<String, String>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        Observable(true)

    override fun firstConnexion(
        user: UserEntity,
        userAccess: UserEntity?
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
        userAccess: UserEntity?
    ): Observable<Boolean> {
        isInDb.value = true
        return Observable(true)
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String?,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        user.value = CURRENT_USER
        return Observable(true)
    }

    override fun getItemsList(): MutableList<Item> {
        return items
    }

    override fun addItem(item: Item): Boolean {
        return items.add(item)
    }

    override fun removeItem(item: Item): Boolean {
        return items.remove(item)
    }


}