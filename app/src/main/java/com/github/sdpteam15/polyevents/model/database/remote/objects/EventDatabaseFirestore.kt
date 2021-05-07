package com.github.sdpteam15.polyevents.model.database.remote.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.RATING_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.RatingAdapter
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val TAG = "EventDatabaseFirestore"

class EventDatabaseFirestore(private val db: DatabaseInterface) : EventDatabaseInterface {
    override fun createEvent(event: Event, userAccess: UserProfile?): Observable<Boolean> =
        db.addEntity(event, EVENT_COLLECTION, EventAdapter)

    override fun updateEvents(event: Event, userAccess: UserProfile?): Observable<Boolean> =
        db.setEntity(event, event.eventId!!, EVENT_COLLECTION)

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getEntity(returnEvent, id, EVENT_COLLECTION, EventAdapter)

    override fun getEvents(
        matcher: Matcher?,
        limit: Long?,
        eventList: ObservableList<Event>,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getListEntity(
            eventList,
            null,
            {
                if (matcher != null) {
                    if (limit != null)
                        matcher.match(it).limit(limit)
                    else
                        matcher.match(it)
                } else {
                    if (limit != null)
                        it.limit(limit)
                    else
                        it
                }
            },
            EVENT_COLLECTION
        )

    override fun getRatingsForEvent(
        eventId: String,
        limit: Long?,
        ratingList: ObservableList<Rating>,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getListEntity(ratingList,
            null, {
                val query = it.whereEqualTo(DatabaseConstant.RatingConstant.RATING_EVENT_ID.value, eventId)
                if (limit != null) {
                    query.limit(limit)
                } else {
                    query
                }
            }, RATING_COLLECTION
        )

    override fun addRatingToEvent(rating: Rating, userAccess: UserProfile?): Observable<Boolean> =
        db.addEntity(rating, RATING_COLLECTION, RatingAdapter)

    override fun removeRating(rating: Rating, userAccess: UserProfile?): Observable<Boolean> =
        db.deleteEntity(rating.ratingId!!, RATING_COLLECTION)

    override fun updateRating(rating: Rating, userAccess: UserProfile?): Observable<Boolean> =
        db.setEntity(rating, rating.ratingId!!, RATING_COLLECTION, RatingAdapter)

    override fun getMeanRatingForEvent(
        id: String,
        mean: Observable<Double>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val end = Observable<Boolean>()

        val rating = ObservableList<Rating>()

        getRatingsForEvent(id, null, rating, userAccess).observeOnce {
            if(it.value){
                rating.observeOnce {it2->
                    val sum = it2.value.fold(0.0, {a,b->a+b.rate!!})
                    mean.postValue(sum/it2.value.size)
                }
            }else{
                end.postValue(false,db)
            }
        }

        return end
    }
}