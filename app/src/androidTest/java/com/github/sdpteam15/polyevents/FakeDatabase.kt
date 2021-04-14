package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.*
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

object FakeDatabase : DatabaseInterface {
    init {
        initEvents()
        initProfiles()
        initItems()
    }

    private fun initItems() {
        items = mutableMapOf()
        items["item1"] = Pair(Item("item1", "230V Plug", ItemType.PLUG), 20)
        items["item2"] = Pair(Item("item2", "Cord rewinder (50m)", ItemType.PLUG), 10)
        items["item3"] = Pair(Item("item3", "Microphone", ItemType.MICROPHONE), 1)
        items["item4"] = Pair(Item("item4", "Cooking plate", ItemType.OTHER), 5)
        items["item5"] = Pair(Item("item5", "Cord rewinder (100m)", ItemType.PLUG), 1)
        items["item6"] = Pair(Item("item6", "Cord rewinder (10m)", ItemType.PLUG), 30)
        items["item7"] = Pair(Item("item7", "Fridge(large)", ItemType.OTHER), 2)

    }

    lateinit var events: MutableMap<String, Event>
    lateinit var profiles: MutableList<UserProfile>
    lateinit var items: MutableMap<String, Pair<Item, Int>>

    private fun initEvents() {
        events = mutableMapOf()
        events["event1"] =
            Event(
                eventId = "event1",
                eventName = "Sushi demo",
                description = "Super hungry activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                organizer = "The fish band",
                zoneName = "Kitchen",
                tags = mutableSetOf("sushi", "japan", "cooking")
            )


        events["event2"] =
            Event(
                eventId = "event2",
                eventName = "Saxophone demo",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
                organizer = "The music band",
                zoneName = "Concert Hall"
            )


        events["event3"] =
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

    }

    private fun initProfiles() {
        profiles = mutableListOf()
    }

    val CURRENT_USER: UserEntity = UserEntity(
        uid = "FakeUID",
        name = "FakeName",
        email = "Fake@mail.ch"
    )

    override val currentUser: UserEntity?
        get() = CURRENT_USER
    override val currentProfile: UserProfile?
        get() = null

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
        matcher: Matcher?,
        number: Long?,
        eventList: ObservableList<Event>,
        profile: UserProfile?
    ): Observable<Boolean> {
        eventList.clear(this)

        eventList.addAll(events.values, this)
        return Observable(true, this)
    }
    /*
    override fun getUpcomingEvents(number: Int, profile: UserProfile?): List<Event> =
            getListEvent("", number, profile)

    override fun getEventFromId(id: String, profile: UserProfile?): Event =
            events.value[0]

    override fun updateEvent(Event: Event, profile: UserProfile?): Boolean = true
    */

    override fun createItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> {
        // generate random document ID like in firebase
        val itemId = generateRandomKey()

        val b = items.put(itemId, Pair(Item(itemId, item.itemName, item.itemType), count)) == null
        return Observable(b, this)
    }

    override fun removeItem(itemId: String, profile: UserProfile?): Observable<Boolean> {
        val b = items.remove(itemId) != null
        return Observable(b, this)
    }

    override fun updateItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (item.itemId == null) return createItem(item, count, profile)
        items[item.itemId!!] = Pair(item, count)
        return Observable(true, this)
    }

    override fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile?
    ): Observable<Boolean> {
        itemList.clear(this)
        for (item in items) {
            itemList.add(item.value, this)
        }
        return Observable(true, this)
    }

    override fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile?
    ): Observable<Boolean> {
        itemList.clear(this)
        val list = mutableListOf<Pair<Item, Int>>()
        for (item in items)
            list.add(item.value)
        itemList.addAll(list, this)
        return Observable(true, this)
    }

    override fun createEvent(event: Event, profile: UserProfile?): Observable<Boolean> {
        val eventId = generateRandomKey()
        val b = events.put(
            eventId,
            Event(
                eventId,
                event.eventName,
                event.organizer,
                event.zoneName,
                event.description,
                event.icon,
                event.startTime,
                event.endTime,
                event.inventory,
                event.tags
            )
        ) == null
        return Observable(b, this)
    }

    override fun updateEvents(event: Event, profile: UserProfile?): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (event.eventId == null) return createEvent(event, profile)
        events[event.eventId!!] = event
        return Observable(true, this)
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        profile: UserProfile?
    ): Observable<Boolean> {
        val event = events[id]
        if(event != null)
            returnEvent.postValue(event, this)
        return Observable(event != null, this)
    }

    override fun updateUserInformation(
        newValues: HashMap<String, String>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        Observable(true, this)

    override fun firstConnexion(
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        Observable(true, this)

    /*
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
    */
    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        isInDb.postValue(true, this)
        return Observable(true, this)
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String?,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        user.value = CURRENT_USER
        return Observable(true)
    }

    /*
        override fun getItemsList(): MutableList<Item> {
            return items
        }

        override fun addItem(item: Item): Boolean {
            return items.add(item)
        }

        override fun removeItem(item: Item): Boolean {
            return items.remove(item)
        }

    */
    override fun setUserLocation(
        location: LatLng,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return Observable(true)
    }

    override fun getUsersLocations(
        usersLocations: Observable<List<LatLng>>,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        // TODO : see whether we write a Python script that send fake data to our database
        usersLocations.postValue(listOf(LatLng(46.548823, 7.017012)))
        return Observable(true)
    }

    /**
     * Generates a random
     */
    private fun generateRandomKey(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..20)
            .map { _ -> Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}