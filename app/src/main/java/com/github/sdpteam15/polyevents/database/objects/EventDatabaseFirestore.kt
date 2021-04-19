package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import android.util.Log
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.EventAttendee
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.EventAdapter
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

    override fun getEventAttendeesByEventId(
        eventId: String,
        matcher: Matcher?,
        limit: Long?,
        eventAttendees: ObservableList<EventAttendee>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }



    override fun addEventAttendee(eventId: String, userUid: String, userAccess: UserProfile?) =
        FirestoreDatabaseProvider.thenDoAdd(
            FirestoreDatabaseProvider.firestore!!.collection(
                DatabaseConstant.CollectionConstant.EVENT_ATTENDEES_COLLECTION.value
            ).add(EventAttendee(currentUser!!.uid, eventId))
        )

    override fun getEventAttendeeByIds(
        eventId: String,
        userUid: String,
        eventAttendee: Observable<EventAttendee?>,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        FirestoreDatabaseProvider.thenDoGet(
            FirestoreDatabaseProvider.firestore!!.collection(
                DatabaseConstant.CollectionConstant.EVENT_ATTENDEES_COLLECTION.value
            ).whereEqualTo(DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value, eventId)
                .whereEqualTo("userUid", userUid).get()
        ) {
            if (it.documents.isEmpty()) {
                Log.d(TAG, "No event attendee found with event id $eventId and user id $userUid")
                eventAttendee.postValue(null, this)
            } else {
                val numberOfDocumentsRetrieved = it.documents.size
                val documentRetrieved = it.documents.get(0)
                Log.d(TAG, "$numberOfDocumentsRetrieved event attendees retrieved")
                eventAttendee.postValue(EventAttendee(
                    documentRetrieved[DatabaseConstant.UserConstants.USER_UID.value] as String?,
                    documentRetrieved[DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value] as String?
                ))
            }
        }

}