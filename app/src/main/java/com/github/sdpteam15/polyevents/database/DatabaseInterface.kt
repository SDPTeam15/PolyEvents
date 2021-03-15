package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.Profile.Companion.CurrentProfile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import java.util.*

const val NUMBER_UPCOMING_EVENTS = 3

/**
 * Database interface
 */
interface DatabaseInterface {

    /**
     * Current user of this database
     */
    val currentUser: DatabaseUserInterface?

    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param user user for database access
     * @return list of profile of a user uid
     */
    fun getListProfile(
        uid: String,
        user: UserInterface = currentUser as UserInterface
    ): List<ProfileInterface>

    /**
     * Add profile to a user
     * @param profile profile to add
     * @param uid uid
     * @param user user for database access
     * @return if the operation succeed
     */
    fun addProfile(
        profile: ProfileInterface, uid: String,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Remove profile from a user
     * @param profile profile to remove
     * @param uid uid
     * @param user user for database access
     * @return if the operation succeed
     */
    fun removeProfile(
        profile: ProfileInterface, uid: String = (currentUser as UserInterface).uid,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Update profile
     * @param event event to update
     * @param profile profile for database access
     * @return if the operation succeed
     */
    fun updateProfile(
        profile: ProfileInterface,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Get list of event
     * @param matcher matcher for the recherche
     * @param number maximum of result
     * @param profile profile for database access
     * @return list of event
     */
    fun getListEvent(
        matcher: String? = null, number: Int? = null,
        profile: ProfileInterface = CurrentProfile
    ): List<Event>

    /**
     * Query the upcoming events
     * @param number : the number of events to retrieve
     * @param profile profile for database access
     * @return List of events in upcoming order (closest first)
     */
    fun getUpcomingEvents(
        number: Int = NUMBER_UPCOMING_EVENTS,
        profile: ProfileInterface = CurrentProfile
    ): List<Event>

    /**
     * Get event from ID
     * @param id ID of the event
     * @param profile profile for database access
     * @return event corresponding to the given ID
     */
    fun getEventFromId(
        id: String,
        profile: ProfileInterface = CurrentProfile
    ): Event?

    /**
     * Update or request an update for an event
     * @param Event event to update
     * @param profile profile for database access
     */
    fun updateEvent(
        Event: Event,
        profile: ProfileInterface = CurrentProfile
    ): Boolean

    fun removeProfile(
        profile: ProfileInterface,
        success: MutableLiveData<Boolean>,
        uid: String = (User.currentUser as UserInterface).uid,
        user: UserInterface = User.currentUser as UserInterface
    )
    /*
    fun acceptMaterialReservation()

    fun deleteMaterialReservation()

    fun getAllMaterialReservationRequests()

    fun createMaterialReservation()

    fun createItem()

    fun updateEvent(userAccess: UserInterface = User.CurrentUser as UserInterface)

    fun createEvent(userAccess: UserInterface = User.CurrentUser as UserInterface)

    fun getEventList(activityListToModify: MutableLiveData<List<Activity>>)

    fun getUpcomingEvents(activityListToModify: MutableLiveData<List<Activity>>)
    */



    /**
     * Update the user information in the database
     * @param newValues : a map with the new value to set in the database
     * @param uid : the uid of the user from which we want to query the informations
     * @param userAccess: the user object to use its permission
     * @return A mutable live data that will be set to true if the query was successfully performed or false otherwise
     */
    fun updateUserInformation(
        newValues: HashMap<String, String>,
        uid: String,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): MutableLiveData<Boolean>

    /**
     * Register the user in the database with its basic informations (uid, email, name)
     * @param user : user with all the requested informations
     * @param userAccess: the user object to use its permission
     * @return A mutable live data that will be set to true if the query was successfully performed or false otherwise
     */
    fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): MutableLiveData<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @oaram isInDb : Will be set to true if in Database or to false otherwise
     * @param uid : user uid we want to check the existence
     * @param userAccess: the user object to use its permission
     * @return A mutable live data that will be set to true if the query was successfully performed or false otherwise
     */
    fun inDatabase(
        isInDb: MutableLiveData<Boolean>,
        uid: String,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): MutableLiveData<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @oaram user : live data that will be set with the find user value
     * @param uid : user uid we want to get the informations
     * @param userAccess: the user object to use its permission
     * @return A mutable live data that will be set to true if the query was successfully performed or false otherwise
     */
    fun getUserInformation(
        user: MutableLiveData<UserInterface>,
        uid: String = (User.currentUser as UserInterface).uid,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): MutableLiveData<Boolean>
}