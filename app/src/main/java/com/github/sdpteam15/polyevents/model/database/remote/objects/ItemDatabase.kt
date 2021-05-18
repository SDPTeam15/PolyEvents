package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.ITEM_COUNT
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemTypeAdapter
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class ItemDatabase(private val db: DatabaseInterface) : ItemDatabaseInterface {
    override fun createItem(
        item: Item,
        count: Int,
        userAccess: UserProfile?
    ): Observable<Boolean> = db.addEntity(Pair(item, count), ITEM_COLLECTION, ItemEntityAdapter)

    override fun removeItem(itemId: String, userAccess: UserProfile?): Observable<Boolean> =
        db.deleteEntity(itemId, ITEM_COLLECTION)

    override fun updateItem(
        item: Item,
        count: Int,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.setEntity(Pair(item, count), item.itemId!!, ITEM_COLLECTION, ItemEntityAdapter)

    override fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getListEntity(itemList, null, null, ITEM_COLLECTION, ItemEntityAdapter)

    override fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        userAccess: UserProfile?
    ): Observable<Boolean> = db.getListEntity(itemList, null, {
        it.whereGreaterThan(ITEM_COUNT.value, 0)
    }, ITEM_TYPE_COLLECTION, ItemEntityAdapter)

    override fun createItemType(
        itemType: String,
        userAccess: UserProfile?
    ): Observable<Boolean> = db.addEntity(
        itemType, ITEM_TYPE_COLLECTION,
        ItemTypeAdapter
    )

    override fun getItemTypes(
        itemTypeList: ObservableList<String>,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getListEntity(itemTypeList, null, null, ITEM_TYPE_COLLECTION, ItemTypeAdapter)
}