package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

object FakeDatabaseItem:ItemDatabaseInterface {
    init {
        initItems()
    }

    private fun initItems() {
        FakeDatabase.items = mutableMapOf()
        FakeDatabase.items["item1"] = Pair(Item("item1", "230V Plug", ItemType.PLUG), 20)
        FakeDatabase.items["item2"] = Pair(Item("item2", "Cord rewinder (50m)", ItemType.PLUG), 10)
        FakeDatabase.items["item3"] = Pair(Item("item3", "Microphone", ItemType.MICROPHONE), 1)
        FakeDatabase.items["item4"] = Pair(Item("item4", "Cooking plate", ItemType.OTHER), 5)
        FakeDatabase.items["item5"] = Pair(Item("item5", "Cord rewinder (100m)", ItemType.PLUG), 1)
        FakeDatabase.items["item6"] = Pair(Item("item6", "Cord rewinder (10m)", ItemType.PLUG), 30)
        FakeDatabase.items["item7"] = Pair(Item("item7", "Fridge(large)", ItemType.OTHER), 2)

    }

    override val currentUser: UserEntity?
        get() = TODO("Not yet implemented")
    override val currentProfile: UserProfile?
        get() = TODO("Not yet implemented")

    override fun createItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> {
        // generate random document ID like in firebase
        val itemId = FakeDatabase.generateRandomKey()
        val b = FakeDatabase.items.put(itemId, Pair(Item(itemId, item.itemName, item.itemType), count)) == null
        return Observable(b, this)
    }

    override fun removeItem(itemId: String, profile: UserProfile?): Observable<Boolean> {
        val b = FakeDatabase.items.remove(itemId) != null
        return Observable(b, this)
    }

    override fun updateItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (item.itemId == null) return createItem(item, count, profile)
        FakeDatabase.items[item.itemId!!] = Pair(item, count)
        return Observable(true, this)
    }

    override fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile?
    ): Observable<Boolean> {
        itemList.clear(this)
        for (item in FakeDatabase.items) {
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
        for (item in FakeDatabase.items)
            list.add(item.value)
        itemList.addAll(list, this)
        return Observable(true, this)
    }
}