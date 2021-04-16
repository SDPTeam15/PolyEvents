package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.ItemEntityAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ItemDatabaseFirestore: ItemDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun createItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoAdd(
        FirestoreDatabaseProvider.firestore!!.collection(DatabaseConstant.ITEM_COLLECTION)
            .add(ItemEntityAdapter.toItemDocument(item, count))
    )


    override fun removeItem(itemId: String, profile: UserProfile?): Observable<Boolean> =
        FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!.collection(DatabaseConstant.ITEM_COLLECTION)
                .document(itemId).delete()
        )

    override fun updateItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (item.itemId == null) return createItem(item, count, profile)
        return FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!
                .collection(DatabaseConstant.ITEM_COLLECTION)
                .document(item.itemId!!)
                .set(ItemEntityAdapter.toItemDocument(item, count))
        )
    }

    override fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(DatabaseConstant.ITEM_COLLECTION).get()
        ) { querySnapshot ->
            itemList.clear(this)
            val items = querySnapshot.documents.map {
                ItemEntityAdapter.toItemEntity(it.data!!, it.id)
            }
            itemList.addAll(items, this)
        }
    }

    override fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(DatabaseConstant.ITEM_COLLECTION)
                .whereGreaterThan(DatabaseConstant.ITEM_COUNT, 0).get()
        ) { querySnapshot ->
            itemList.clear(this)
            val items = querySnapshot.documents.map {
                ItemEntityAdapter.toItemEntity(it.data!!, it.id)
            }
            itemList.addAll(items, this)
        }
    }
}