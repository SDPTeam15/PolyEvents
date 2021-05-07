package com.github.sdpteam15.polyevents.model.database.remote.objects

import android.util.Log
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

const val TAG = "EventDatabase"

class EventDatabase(private val db: DatabaseInterface) : EventDatabaseInterface {
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
        db.getListEntity(
            ratingList,
            null, {
                val query =
                    it.whereEqualTo(DatabaseConstant.RatingConstant.RATING_EVENT_ID.value, eventId)
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
        eventId: String,
        mean: Observable<Double>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val end = Observable<Boolean>()
        val rating = ObservableList<Rating>()

        getRatingsForEvent(eventId, null, rating, userAccess).observeOnce {
            if (it.value) {
                rating.observeOnce { it2 ->
                    val sum = it2.value.fold(0.0, { a, b -> a + b.rate!! })
                    mean.postValue(sum / it2.value.size)
                }
            } else {
                end.postValue(false, db)
            }
        }

        return end
    }

    override fun getUserRatingFromEvent(
        userId: String,
        eventId: String,
        returnedRating: Observable<Rating>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val end = Observable<Boolean>()
        val rating = ObservableList<Rating>()

        db.getListEntity(rating, null, {
            it.whereEqualTo(DatabaseConstant.RatingConstant.RATING_EVENT_ID.value, eventId)
                .whereEqualTo(DatabaseConstant.RatingConstant.RATING_USER_ID.value, userId)
                .limit(1)
        }, RATING_COLLECTION).observeOnce {
            if (it.value) {
                if (rating.size == 0) {
                    end.postValue(false, db)
                } else {
                    returnedRating.postValue(rating[0])
                    end.postValue(true, db)
                }
            } else {
                end.postValue(false, db)
            }
        }
        return end
    }
}