package com.github.sdpteam15.polyevents.database

import android.content.ClipData
import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.database.observe.Observable
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

    // Temporary methods
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
     * Update profile in database
     * @param profile profile to update
     * @param user profile for database access
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

    //All the methods above need to be deleted before the end of the project

    // Methods that we should use to have asynchronous communication
    /**
     * Items modifier and accessor methods
     */

    /*
    /**
     * @param item item we want to add in the database
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun createItem(
        item: Item,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * @param item item we want to remove from the database
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun removeItem(
        item: Item,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * @param item item we want to update in the database
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun updateItem(
        item: Item,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>*/

    /**
     * Material request modifier and accessor methods
     */
    /*
    /**
     * Answer a material request
     * @param id: id of the items
     * @param answer true or false depending if we accept the request or not
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun answerMaterialRequest(
        id:String,
        answer:Boolean,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * Get the list of all material request
     * @param materialList list in which the list of all material request will be set after retrieving from database
     * @param matcher: matcher for the search
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun getMaterialRequestList(
        materialList: MutableLiveData<MaterialRequest>,
        matcher: String? = null,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * @param the request we want to add in the database
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun createMaterialRequest(
        request: MaterialRequest,
        profile: ProfileInterface = CurrentProfile
    )*/

    /**
     * Event modifier and accessor methods
     */
    /*
    /**
     * Update or request an update for an event
     * @param event: event to create
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun createEvent(
        event: Event,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * Update or request an update for an event
     * @param event: event to update
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun updateEvents(
        event: Event,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * Get event from ID
     * @param id: The id of the event we want to retrieve
     * @param returnEvent : variable in which we will set the retrieve event
     * @param profile: The profile we want to remove
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun getEventFromId(
        id: String,
        returnEvent: MutableLiveData<Event>,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * Get list of event
     * @param matcher: matcher for the search
     * @param number: maximum of result
     * @param activityList: the list of event that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun getListEvent(
        matcher: String? = null,
        number: Int? = null,
        activityList: MutableLiveData<List<Event>>,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>

    /**
     * Query the upcoming events (the closest first)
     * @param number : the number of events to retrieve
     * @param activityList: the list of event that will be set when the DB returns the information
     * @param profile : profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun getUpcomingEvents(
        number: Int = NUMBER_UPCOMING_EVENTS,
        activityList: MutableLiveData<List<Event>>,
        profile: ProfileInterface = CurrentProfile
    ): MutableLiveData<Boolean>
    */

    /**
     * All accessor and modifier methods for users and profiles
     */
    /*
    /**
     * Add profile to a user
     * @param profile profile to add
     * @param uid uid
     * @param user user for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun addProfile(
        profile: ProfileInterface, uid: String,
        user: UserInterface = currentUser as UserInterface
    ): MutableLiveData<Boolean>

    /**
     * Remove the profile from the user in database
     * @param profile: The profile we want to remove
     * @param uid : the uid of the user from which we want to query the information
     * @param userAccess: the user object to use its permission
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun removeProfile(
        profile: ProfileInterface,
        uid: String = (User.currentUser as UserInterface).uid,
        user: UserInterface = User.currentUser as UserInterface
    ): MutableLiveData<Boolean>

    /**
     * Update profile
     * @param event event to update
     * @param profile profile for database access
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun updateProfile(
        profile: ProfileInterface,
        user: UserInterface = currentUser as UserInterface
    ): MutableLiveData<Boolean>

    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param profileList mutable live data in which the list of profile will be set
     * @param user user for database access
     * @return list of profile of a user uid
     */
    fun getListProfile(
        uid: String,
        profileList:MutableLiveData<List<ProfileInterface>>,
        user: UserInterface = currentUser as UserInterface
    ): MutableLiveData<Boolean>
        */

    /**
     * Update the user information in the database
     * @param newValues : a map with the new value to set in the database
     * @param uid : the uid of the user from which we want to query the informations
     * @param userAccess: the user object to use its permission
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
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
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
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
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @oaram user : live data that will be set with the find user value
     * @param uid : user uid we want to get the informations
     * @param userAccess: the user object to use its permission
     * @return A mutable live data that will be set to true if the communication with the DB is over and no error
     */
    fun getUserInformation(
        user: MutableLiveData<UserInterface>,
        uid: String = (User.currentUser as UserInterface).uid,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): MutableLiveData<Boolean>
}