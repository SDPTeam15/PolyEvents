package com.github.sdpteam15.polyevents.model.database.remote.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val TAG = "EventDatabaseFirestore"

object EventDatabaseFirestore : EventDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun createEvent(event: Event, userAccess: UserProfile?): Observable<Boolean> =
        FirestoreDatabaseProvider.thenDoAdd(
            FirestoreDatabaseProvider.firestore!!.collection(
                EVENT_COLLECTION.value
            ).add(EventAdapter.toDocument(event))
        )

    override fun updateEvents(event: Event, userAccess: UserProfile?): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (event.eventId == null) return createEvent(event, profile)
        return FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!.collection(EVENT_COLLECTION.value)
                .document(event.eventId!!).set(EventAdapter.toDocument(event))
        )
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        userAccess: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoMultGet(
        FirestoreDatabaseProvider.firestore!!.collection(EVENT_COLLECTION.value)
            .document(id).get()
    ) {
        returnEvent.postValue(
            EventAdapter.fromDocument(it.data!!, it.id), this
        )
    }

    override fun getEvents(
        matcher: Matcher?,
        limit: Long?,
        eventList: ObservableList<Event>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val task = FirestoreDatabaseProvider.firestore!!.collection(EVENT_COLLECTION.value)
        val query = matcher?.match(task)
        val v = if (query != null) {
            if (limit != null) query.limit(limit).get() else query.get()
        } else {
            if (limit != null) task.limit(limit).get() else task.get()
        }
        return FirestoreDatabaseProvider.thenDoGet(v) {
            eventList.clear(this)
            for (d in it.documents) {
                val data = d.data
                if (data != null) {
                    val e: Event = EventAdapter.fromDocument(data, d.id)
                    eventList.add(e)
                }
            }
        }
    }
}