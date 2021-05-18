package com.github.sdpteam15.polyevents.model.database.remote.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.ITEM_TOTAL
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemTypeAdapter
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ItemDatabaseFirestore : ItemDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun createItem(
        item: Item,
        total: Int,
        userAccess: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoAdd(
        FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value)
            .add(ItemEntityAdapter.toItemDocument(item, total, total))
    )


    override fun removeItem(itemId: String, userAccess: UserProfile?): Observable<Boolean> =
        FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value)
                .document(itemId).delete()
        )

    override fun updateItem(
        item: Item,
        total: Int,
        remaining: Int,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!
                .collection(ITEM_COLLECTION.value)
                .document(item.itemId!!)
                .set(ItemEntityAdapter.toItemDocument(item, total, remaining))
        )
    }

    override fun getItemsList(
        itemList: ObservableList<Triple<Item, Int, Int>>,
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
        itemList: ObservableList<Triple<Item, Int, Int>>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value)
                .whereGreaterThan(ITEM_TOTAL.value, 0).get()
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
    ): Observable<Boolean> = FirestoreDatabaseProvider.addEntity(
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