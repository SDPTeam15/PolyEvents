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
     * @param event event to create
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createEvent(
            event: Event,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Update or request an update for an event
     * @param event event to update
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateEvents(
            event: Event,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get event from ID
     * @param id The id of the event we want to retrieve
     * @param returnEvent variable in which we will set the retrieve event
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventFromId(
            id: String,
            returnEvent: Observable<Event>,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of event
     * @param matcher matcher for the search
     * @param limit maximum number of results
     * @param eventList the list of event that will be set when the DB returns the information
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEvents(
            matcher: Matcher? = null,
            limit: Long? = null,
            eventList: ObservableList<Event>,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    fun getRatingsForEvent(
            id: String,
            limit: Long? = null,
            ratingList: ObservableList<Rating>,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    fun addRatingToEvent(
            rating: Rating,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    fun removeRating(
            rating: Rating,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    fun updateRating(
            rating: Rating,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    fun getMeanRatingForEvent(
            id: String,
            mean: Observable<Double>,
            userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>


}