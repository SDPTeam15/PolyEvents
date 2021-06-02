package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.ITEM_REMAINING
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemTypeAdapter
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class ItemDatabase(private val db: DatabaseInterface) : ItemDatabaseInterface {
    override fun createItem(
        item: Item,
        total: Int
    ): Observable<String> = db.addEntityAndGetId(Triple(item, total,total), ITEM_COLLECTION, ItemEntityAdapter)

    override fun removeItem(itemId: String): Observable<Boolean> =
        db.deleteEntity(itemId, ITEM_COLLECTION)

    override fun updateItem(
        item: Item,
        total: Int,
        remaining: Int
    ): Observable<Boolean> =
        db.setEntity(Triple(item,total,remaining), item.itemId!!, ITEM_COLLECTION, ItemEntityAdapter)

    override fun getItemsList(
        itemList: ObservableList<Triple<Item, Int, Int>>,
        matcher : Matcher?,
        ids : List<String>?
    ): Observable<Boolean> =
        db.getListEntity(itemList, ids, matcher, ITEM_COLLECTION, ItemEntityAdapter)

    override fun getAvailableItems(
        itemList: ObservableList<Triple<Item, Int, Int>>
    ): Observable<Boolean> = db.getListEntity(itemList, null, {
        it.whereGreaterThan(ITEM_REMAINING.value, 0)
    }, ITEM_COLLECTION, ItemEntityAdapter)

    override fun createItemType(
        itemType: String
    ): Observable<Boolean> = db.addEntity(
        itemType, ITEM_TYPE_COLLECTION,
        ItemTypeAdapter
    )

    override fun getItemTypes(
        itemTypeList: ObservableList<String>
    ): Observable<Boolean> =
        db.getListEntity(itemTypeList, null, null, ITEM_TYPE_COLLECTION, ItemTypeAdapter)
}