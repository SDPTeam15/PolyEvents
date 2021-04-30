package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile

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
}