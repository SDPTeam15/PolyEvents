package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ItemConstants.ITEM_COUNT
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.ItemEntityAdapter
import com.github.sdpteam15.polyevents.util.ItemTypeAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ItemDatabaseFirestore : ItemDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun createItem(
        item: Item,
        count: Int,
        userAccess: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoAdd(
        FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value)
            .add(ItemEntityAdapter.toItemDocument(item, count))
    )


    override fun removeItem(itemId: String, userAccess: UserProfile?): Observable<Boolean> =
        FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!.collection(ITEM_COLLECTION.value)
                .document(itemId).delete()
        )

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
    ): Observable<Boolean> = FirestoreDatabaseProvider.addEntity(itemType,ITEM_TYPE_COLLECTION,ItemTypeAdapter)

    override fun getItemTypes(
        itemTypeList: ObservableList<String>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(ITEM_TYPE_COLLECTION.value).get()
        ) { querySnapshot ->
            itemTypeList.clear(this)
            val itemsTypes = querySnapshot.documents.map {
                ItemTypeAdapter.fromDocument(it.data!!,it.id)
            }
            itemTypeList.addAll(itemsTypes, this)
        }
    }


}