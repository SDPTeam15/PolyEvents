package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.ITEM_COUNT
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemTypeAdapter
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class ItemDatabaseFirestore(private val db: DatabaseInterface) : ItemDatabaseInterface {
    override fun createItem(
        item: Item,
        count: Int,
        userAccess: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoAdd(
        FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value)
            .add(ItemEntityAdapter.toItemDocument(item, count))
    )


    override fun removeItem(itemId: String, userAccess: UserProfile?): Observable<Boolean> =
        db.deleteEntity(itemId, ITEM_COLLECTION)

    override fun updateItem(
        item: Item,
        count: Int,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (item.itemId == null) return createItem(item, count, profile)
        return FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!
                .collection(ITEM_COLLECTION.value)
                .document(item.itemId!!)
                .set(ItemEntityAdapter.toItemDocument(item, count))
        )
    }

    override fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value).get()
        ) { querySnapshot ->
            itemList.clear(this)
            val items = querySnapshot.documents.map {
                ItemEntityAdapter.fromDocument(it.data!!, it.id)
            }
            itemList.addAll(items, this)
        }
    }

    override fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value)
                .whereGreaterThan(ITEM_COUNT.value, 0).get()
        ) { querySnapshot ->
            itemList.clear(this)
            val items = querySnapshot.documents.map {
                ItemEntityAdapter.fromDocument(it.data!!, it.id)
            }
            itemList.addAll(items, this)
        }
    }

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
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(ITEM_TYPE_COLLECTION.value).get()
        ) { querySnapshot ->
            itemTypeList.clear(this)
            val itemsTypes = querySnapshot.documents.map {
                ItemTypeAdapter.fromDocument(it.data!!, it.id)
            }
            itemTypeList.addAll(itemsTypes, this)
        }
    }


}