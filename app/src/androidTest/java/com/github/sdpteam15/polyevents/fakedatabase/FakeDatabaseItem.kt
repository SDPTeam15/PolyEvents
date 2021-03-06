package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseItem : ItemDatabaseInterface {
    lateinit var items: MutableMap<String, Triple<Item, Int,Int>>
    lateinit var itemTypes: MutableList<String>

    init {
        initItems()
        initItemTypes()
    }

    private fun initItems() {
        items = mutableMapOf()
        items["item1"] = Triple(Item("item1", "230V Plug", "PLUG"), 20,18)
        items["item2"] = Triple(Item("item2", "Cord rewinder (50m)", "PLUG"), 10,5)
        items["item3"] = Triple(Item("item3", "Microphone", "MICROPHONE"), 1,1)
        items["item4"] = Triple(Item("item4", "Cooking plate", "OTHER"), 5,5)
        items["item5"] = Triple(Item("item5", "Cord rewinder (100m)", "PLUG"), 1,0)
        items["item6"] = Triple(Item("item6", "Cord rewinder (10m)", "PLUG"), 30,10)
        items["item7"] = Triple(Item("item7", "Fridge(large)", "OTHER"), 2,2)
    }

    private fun initItemTypes() {
        itemTypes = mutableListOf(
            "PLUG",
            "MICROPHONE",
            "OTHER"
        )
    }

    override fun createItem(
        item: Item,
        total: Int
    ): Observable<String> {
        // generate random document ID like in firebase
        val itemId = FakeDatabase.generateRandomKey()
        items[itemId] = Triple(Item(itemId, item.itemName, item.itemType), total, total)
        return Observable(itemId, FakeDatabase)
    }

    override fun removeItem(itemId: String): Observable<Boolean> {
        val b = items.remove(itemId) != null
        return Observable(b, FakeDatabase)
    }

    override fun updateItem(
        item: Item,
        total: Int,
        remaining: Int
    ): Observable<Boolean> {
        items[item.itemId!!] = Triple(item, total,remaining)
        return Observable(true, FakeDatabase)
    }


    override fun getItemsList(
        itemList: ObservableList<Triple<Item, Int, Int>>,
        matcher: Matcher?,
        ids: List<String>?
    ): Observable<Boolean> {
        itemList.clear(this)
        for (item in items) {
            itemList.add(item.value, FakeDatabase)
        }
        return Observable(true, FakeDatabase)
    }

    override fun getAvailableItems(
        itemList: ObservableList<Triple<Item, Int, Int>>
    ): Observable<Boolean> {
        itemList.clear(this)
        val list = mutableListOf<Triple<Item, Int,Int>>()
        for (item in items)
            list.add(item.value)
        itemList.addAll(list, FakeDatabase)
        return Observable(true, FakeDatabase)
    }

    override fun createItemType(
        itemType: String
    ): Observable<Boolean> {
        itemTypes.add(itemType)
        return Observable(true, FakeDatabase)
    }

    override fun getItemTypes(
        itemTypeList: ObservableList<String>
    ): Observable<Boolean> {
        itemTypeList.clear()
        itemTypeList.addAll(itemTypes)
        return Observable(true, FakeDatabase)
    }
}