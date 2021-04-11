package com.github.sdpteam15.polyevents.database

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.github.sdpteam15.polyevents.model.*
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.util.*

object FakeDatabase : DatabaseInterface {
    init {
        initEvents()
        initProfiles()
        initItems()
    }

    private fun initItems() {
        items = mutableMapOf()
        items[Item("230V Plug", ItemType.PLUG)] = 20
        items[Item("Cord rewinder (50m)", ItemType.PLUG)] = 10
        items[Item("Microphone", ItemType.MICROPHONE)] = 1
        items[Item("Cooking plate", ItemType.OTHER)] = 5
        items[Item("Cord rewinder (100m)", ItemType.PLUG)] = 1
        items[Item("Cord rewinder (10m)", ItemType.PLUG)] = 30
        items[Item("Fridge(large)", ItemType.OTHER)] = 2

    }

    private lateinit var events: MutableList<Event>
    private lateinit var profiles: MutableList<UserProfile>
    private lateinit var items: MutableMap<Item, Int>

    private fun initEvents() {
        events = mutableListOf()
        events.add(
            Event(
                "Sushi demo",
                "The fish band",
                "Kitchen",
                "Super hungry activity !",
                null,
                LocalDateTime.of(2021, 3, 7, 12, 15),
                LocalDateTime.of(2021, 3, 7, 13, 15),
                mutableListOf(),
                mutableSetOf("sushi", "japan", "cooking"),
                "1"
            )
        )
        events.add(
            Event(
                "Aqua Poney",
                "The Aqua Poney team",
                "Swimming pool",
                "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                null,
                LocalDateTime.of(2021, 3, 7, 14, 15),
                LocalDateTime.of(2021, 3, 7, 17, 45),
                mutableListOf(),
                mutableSetOf(),
                "2"
            )
        )
        events.add(
            Event(
                "Saxophone demo",
                "The music band",
                "Concert Hall",
                "Super noisy activity !",
                null,
                LocalDateTime.of(2021, 3, 7, 17, 15),
                LocalDateTime.of(2021, 3, 7, 18, 0),
                mutableListOf(),
                mutableSetOf(),
                "3"
            )
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
/*
    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: UserProfile?
    ): List<Event> {
        val res = mutableListOf<Event>()
        for (v in events.value) {
            if (v != null) {
                res.add(v)
            }
            if (res.size == number)
                break
        }
        return res
    }*/

    override fun getListEvent(
        matcher: Matcher?,
        number: Long?,
        eventList: ObservableList<Event>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        //request sql returns- task<Document>
        Log.d("FakeDataBase", "getting")
        eventList.clear(this)

        eventList.addAll(events, this)
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
        profile: ProfileInterface
    ): Observable<Boolean> {
        items[item] = count
        return Observable(true, this)
    }

    override fun removeItem(item: Item, profile: ProfileInterface): Observable<Boolean> {
        val b = items.remove(item) != null
        return Observable(b, this)
    }

    override fun updateItem(
        item: Item,
        count: Int,
        profile: ProfileInterface
    ): Observable<Boolean> {
        items.remove(items.keys.first { i -> i.itemId == item.itemId })
        items[item] = count
        return Observable(true, this)
    }

    override fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        itemList.clear(this)
        for (item in items) {
            itemList.add(Pair(item.key, item.value), this)
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
            list.add(Pair(item.key, item.value))
        itemList.addAll(list, this)
        return Observable(true, this)
    }

    override fun createEvent(event: Event, profile: UserProfile?): Observable<Boolean> {
        events.add(event)
        return Observable(true, this)
    }

    override fun updateEvents(event: Event, profile: UserProfile?): Observable<Boolean> {
        events[events.indexOfFirst { e -> e.id == event.id }] = event
        return Observable(true, this)
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        profile: UserProfile?
    ): Observable<Boolean> {
        returnEvent.postValue(events.first { e -> e.id == id }, this)
        return Observable(true, this)
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
}