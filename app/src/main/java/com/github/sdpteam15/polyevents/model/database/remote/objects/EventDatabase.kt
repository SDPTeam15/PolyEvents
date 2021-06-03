package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.RatingAdapter
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

const val TAG = "EventDatabase"

class EventDatabase(private val db: DatabaseInterface) : EventDatabaseInterface {
    override fun createEvent(event: Event): Observable<Boolean> =
        db.addEntity(event, EVENT_COLLECTION, EventAdapter)

    override fun updateEvent(event: Event): Observable<Boolean> =
        db.setEntity(event, event.eventId!!, EVENT_COLLECTION)

    override fun removeEvent(eventId: String): Observable<Boolean> =
        db.deleteEntity(eventId, EVENT_COLLECTION)

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>
    ): Observable<Boolean> =
        db.getEntity(returnEvent, id, EVENT_COLLECTION, EventAdapter)

    override fun getEvents(
        matcher: Matcher?,
        limit: Long?,
        eventList: ObservableList<Event>
    ): Observable<Boolean> =
        db.getListEntity(
            eventList,
            null,
            {
                var query = matcher?.match(it) ?: it

                if (limit != null) query = query.limit(limit)
                query
            },
            EVENT_COLLECTION
        )

    override fun createEventEdit(event: Event): Observable<Boolean> =
        db.addEntity(event, EVENT_EDIT_COLLECTION)

    override fun updateEventEdit(event: Event): Observable<Boolean> =
        db.setEntity(event, event.eventEditId!!, EVENT_EDIT_COLLECTION)

    override fun removeEventEdit(eventId: String): Observable<Boolean> =
        db.deleteEntity(eventId, EVENT_EDIT_COLLECTION)

    override fun getEventEditFromId(
        id: String,
        returnEvent: Observable<Event>
    ): Observable<Boolean> = db.getEntity(returnEvent, id, EVENT_EDIT_COLLECTION)

    override fun getEventEdits(
        matcher: Matcher?,
        eventList: ObservableList<Event>
    ): Observable<Boolean> =
        db.getListEntity(
            eventList,
            null,
            matcher,
            EVENT_EDIT_COLLECTION
        )

    override fun getRatingsForEvent(
        eventId: String,
        limit: Long?,
        ratingList: ObservableList<Rating>
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

    override fun addRatingToEvent(rating: Rating): Observable<Boolean> =
        db.addEntity(rating, RATING_COLLECTION, RatingAdapter)

    override fun removeRating(rating: Rating): Observable<Boolean> =
        db.deleteEntity(rating.ratingId!!, RATING_COLLECTION)

    override fun updateRating(rating: Rating): Observable<Boolean> =
        db.setEntity(rating, rating.ratingId!!, RATING_COLLECTION, RatingAdapter)


    override fun getMeanRatingForEvent(
        eventId: String,
        mean: Observable<Float>
    ): Observable<Boolean> {
        val end = Observable<Boolean>()
        val rating = ObservableList<Rating>()

        getRatingsForEvent(eventId, null, rating).observeOnce {
            if (it.value) {
                val m = rating.fold(
                    Pair(0.0F, 0),
                    { a, b ->
                        Pair(
                            (a.first * a.second + b.rate!!) / (a.second + 1),
                            a.second + 1
                        )
                    })
                mean.postValue(m.first, it.sender)
                end.postValue(true, it.sender)
            } else {
                end.postValue(false, it.sender)
            }
        }

        return end
    }

    override fun getUserRatingFromEvent(
        userId: String,
        eventId: String,
        returnedRating: Observable<Rating>
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

    override fun getEventsByZoneId(
        zoneId: String,
        limit: Long?,
        events: ObservableList<Event>
    ): Observable<Boolean> =
        db.getListEntity(
            events,
            null,
            {
                val query =
                    it.whereEqualTo(DatabaseConstant.EventConstant.EVENT_ZONE_ID.value, zoneId)
                if (limit != null) query.limit(limit)
                query
            },
            EVENT_COLLECTION
        )

}
