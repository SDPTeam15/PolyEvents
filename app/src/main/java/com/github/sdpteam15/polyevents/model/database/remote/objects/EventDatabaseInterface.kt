package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface EventDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser

    /**
     * Create an event
     * @param event Event to create
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createEvent(
        event: Event
    ): Observable<Boolean>

    /**
     * Update or request an update for an event
     * @param event Event to update
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateEvent(
        event: Event
    ): Observable<Boolean>

    /**
     * Remove an event from database
     * @param eventId The id of the event we want to remove
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeEvent(
        eventId: String
    ): Observable<Boolean>


    /**
     * Get event from ID
     * @param id The id of the event we want to retrieve
     * @param returnEvent Variable in which we will set the retrieve event
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>
    ): Observable<Boolean>

    /**
     * Get list of event
     * @param matcher Matcher for the search
     * @param limit Maximum number of results
     * @param eventList The list of event that will be set when the DB returns the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEvents(
        matcher: Matcher? = null,
        limit: Long? = null,
        eventList: ObservableList<Event>
    ): Observable<Boolean>


    /**
     * Create an event edit
     * @param event Event to create
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createEventEdit(
        event: Event
    ): Observable<Boolean>

    /**
     * Update or request an update for an event
     * @param event Event to update
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateEventEdit(
        event: Event
    ): Observable<Boolean>

    /**
     * Remove an event edit from database
     * @param eventId The id of the event edit we want to remove
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeEventEdit(
        eventId: String
    ): Observable<Boolean>


    /**
     * Get event Edit from ID
     * @param id The id of the event we want to retrieve
     * @param returnEvent Variable in which we will set the retrieve event
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventEditFromId(
        id: String,
        returnEvent: Observable<Event>
    ): Observable<Boolean>

    /**
     * Get list of event edits
     * @param matcher Matcher for the search
     * @param eventList The list of event that will be set when the DB returns the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventEdits(
        matcher: Matcher? = null,
        eventList: ObservableList<Event>
    ): Observable<Boolean>


    /**
     * Get all the ratings for a specific events
     * @param eventId The id of the event
     * @param limit The maximum number of ratings we want to retrieve
     * @param ratingList The list of ratings that will be set when the DB returns the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getRatingsForEvent(
        eventId: String,
        limit: Long? = null,
        ratingList: ObservableList<Rating>
    ): Observable<Boolean>

    /**
     * Add a rating to the database
     * @param rating The rating to add
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun addRatingToEvent(
        rating: Rating
    ): Observable<Boolean>

    /**
     * Remove a rating from the database
     * @param rating The rating to remove
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeRating(
        rating: Rating
    ): Observable<Boolean>

    /**
     * Update a rating from the database
     * @param rating The rating to remove
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateRating(
        rating: Rating
    ): Observable<Boolean>

    /**
     * Get the mean rating score for a specific event
     * @param eventId The event we want to retrieve the ratings
     * @param mean The rating that will be set when the DB returns the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getMeanRatingForEvent(
        eventId: String,
        mean: Observable<Float>
    ): Observable<Boolean>

    /**
     * Get the rating from an user for an event from the database
     * @param userId The user from whom we want to retrieve the rating
     * @param eventId The event we want to retrieve the rating from
     * @param returnedRating The rating that will be set when the DB returns the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserRatingFromEvent(
        userId: String,
        eventId: String,
        returnedRating: Observable<Rating>
    ): Observable<Boolean>

    /**
     * Get events for a certain zone
     * @param zoneId the id of the zone
     * @param limit The maximum number of events we want to retrieve
     * @param events The list of events that will be set when the DB returns the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventsByZoneId(
        zoneId: String,
        limit: Long? = null,
        events: ObservableList<Event>
    ): Observable<Boolean>
}