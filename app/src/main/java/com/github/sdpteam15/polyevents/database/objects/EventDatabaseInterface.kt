package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

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
     * @param number maximum of result
     * @param eventList the list of event that will be set when the DB returns the information
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getListEvent(
        matcher: Matcher? = null,
        number: Long? = null,
        eventList: ObservableList<Event>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

}