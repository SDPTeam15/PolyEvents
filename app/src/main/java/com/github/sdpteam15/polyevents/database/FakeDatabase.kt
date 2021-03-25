package com.github.sdpteam15.polyevents.database

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.database.observe.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import com.github.sdpteam15.polyevents.user.ProfileInterface
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
        items = mutableListOf()
        items.add(Item("Scie-tronconneuse", ItemType.OTHER))
        items.add(Item("Bonnet de bain", ItemType.OTHER))
    }

    private lateinit var events: MutableList<Event>
    private lateinit var profiles: MutableList<ProfileInterface>
    private lateinit var items: MutableList<Item>

    @RequiresApi(Build.VERSION_CODES.O)
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

    override val currentUser: DatabaseUserInterface = object : DatabaseUserInterface {
        override var displayName: String = "FakeName"
        override var uid: String = "FakeUID"
        override var email: String = "Fake@mail.ch"
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
/*
    override fun getListEvent(
            matcher: String?,
            number: Int?,
            profile: ProfileInterface
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
        eventList.clear()

        for (e in events) eventList.add(e)
        return Observable(true)
    }
    /*
    override fun getUpcomingEvents(number: Int, profile: ProfileInterface): List<Event> =
            getListEvent("", number, profile)

    override fun getEventFromId(id: String, profile: ProfileInterface): Event =
            events.value[0]

    override fun updateEvent(Event: Event, profile: ProfileInterface): Boolean = true
    */

    override fun createItem(item: Item, profile: ProfileInterface): Observable<Boolean> {
        items.add(item)
        return Observable(true)
    }

    override fun removeItem(item: Item, profile: ProfileInterface): Observable<Boolean> {
        val b = items.remove(item)
        Log.d("Database", "removed $item = $b")
        Log.d("Database", "new list = $items")
        return Observable(b)
    }

    override fun updateItem(item: Item, profile: ProfileInterface): Observable<Boolean> {
        items[items.indexOfFirst { i -> i.itemId == item.itemId }] = item
        return Observable(true)
    }

    override fun getItemsList(
        itemList: ObservableList<Item>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        itemList.clear()
        for (item in items)
            itemList.add(item)
        return Observable(true)
    }

    override fun getAvailableItems(
        itemList: ObservableList<Pair<Item,Int>>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        itemList.clear()
        for (item in items)
            itemList.add(Pair(item, 10))
        return Observable(true)
    }

    override fun createEvent(event: Event, profile: ProfileInterface): Observable<Boolean> {
        events.add(event)
        return Observable(true)
    }

    override fun updateEvents(event: Event, profile: ProfileInterface): Observable<Boolean> {
        events[events.indexOfFirst { e -> e.id == event.id }] = event
        return Observable(true)
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        returnEvent.postValue(events.first { e -> e.id == id })
        return Observable(true)
    }

    override fun updateUserInformation(
        newValues: HashMap<String, String>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> =
        Observable(true)

    override fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface
    ): Observable<Boolean> =
        Observable(true)

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
/*
    override fun getItemsList(): MutableList<String> {
        return items
    }

    override fun addItem(item: String): Boolean {
        return items.add(item)
    }

    override fun removeItem(item: String): Boolean {
        return items.remove(item)
    }

*/
}