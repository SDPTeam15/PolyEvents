package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface EventDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    /**
     * Update or request an update for an event
     * @param event Event to create
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createEvent(
        event: Event,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Update or request an update for an event
     * @param event Event to update
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateEvents(
        event: Event,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get event from ID
     * @param id The id of the event we want to retrieve
     * @param returnEvent Variable in which we will set the retrieve event
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of event
     * @param matcher Matcher for the search
     * @param limit Maximum number of results
     * @param eventList The list of event that will be set when the DB returns the information
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEvents(
        matcher: Matcher? = null,
        limit: Long? = null,
        eventList: ObservableList<Event>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get all the ratings for a specific events
     * @param eventId The id of the event
     * @param limit The maximum number of ratings we want to retrieve
     * @param ratingList The list of ratings that will be set when the DB returns the information
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getRatingsForEvent(
        eventId: String,
        limit: Long? = null,
        ratingList: ObservableList<Rating>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Add a rating to the database
     * @param rating The rating to add
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun addRatingToEvent(
        rating: Rating,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Remove a rating from the database
     * @param rating The rating to remove
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeRating(
        rating: Rating,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Update a rating from the database
     * @param rating The rating to remove
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateRating(
        rating: Rating,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get the mean rating score for a specific event
     * @param eventId The event we want to retrieve the ratings
     * @param mean The rating that will be set when the DB returns the information
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getMeanRatingForEvent(
        eventId: String,
        mean: Observable<Float>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get the rating from an user for an event from the database
     * @param userId The user from whom we want to retrieve the rating
     * @param eventId The event we want to retrieve the rating from
     * @param returnedRating The rating that will be set when the DB returns the information
     * @param userAccess The user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserRatingFromEvent(
        userId: String,
        eventId: String,
        returnedRating: Observable<Rating>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get events for a certain zone
     * @param zoneId the id of the zone
     * @param limit The maximum number of events we want to retrieve
     * @param events The list of events that will be set when the DB returns the information
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventsByZoneId(
        zoneId: String,
        limit: Long?,
        events: ObservableList<Event>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

}