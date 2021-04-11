package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
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
    //@Deprecated(message = "Use the asynchronous method")
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
    //@Deprecated(message = "Use the asynchronous method")
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
    //@Deprecated(message = "Use the asynchronous method")
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
    //@Deprecated(message = "Use the asynchronous method")
    fun updateProfile(
        profile: ProfileInterface,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    // Methods that we should use to have asynchronous communication
    /**
     * Items modifier and accessor methods
     */

    /**
     * @param item item we want to add in the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createItem(
        item: Item,
        count: Int,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * @param item item we want to remove from the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeItem(
        item: Item,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * @param item item we want to update in the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateItem(
        item: Item,
        count: Int,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * Get list of items
     * @param itemList: the list of items that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * Get list of available items
     * @param itemList: the list of items that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * Material request modifier and accessor methods
     */
    /*
    /**
     * Answer a material request
     * @param id: id of the items
     * @param answer true or false depending if we accept the request or not
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun answerMaterialRequest(
        id:String,
        answer:Boolean,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * Get the list of all material request
     * @param materialList list in which the list of all material request will be set after retrieving from database
     * @param matcher: matcher for the search
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getMaterialRequestList(
        materialList: Observable<MaterialRequest>,
        matcher: String? = null,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * @param the request we want to add in the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createMaterialRequest(
        request: MaterialRequest,
        profile: ProfileInterface = CurrentProfile
    )*/

    /**
     * Event modifier and accessor methods
     */

    /**
     * Update or request an update for an event
     * @param event: event to create
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createEvent(
        event: Event,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * Update or request an update for an event
     * @param event: event to update
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateEvents(
        event: Event,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * Get event from ID
     * @param id: The id of the event we want to retrieve
     * @param returnEvent : variable in which we will set the retrieve event
     * @param profile: The profile we want to remove
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

    /**
     * Get list of event
     * @param matcher: matcher for the search
     * @param number: maximum of result
     * @param eventList: the list of event that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getListEvent(
        matcher: Matcher? = null,
        number: Long? = null,
        eventList: ObservableList<Event>,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>


/*
    /**
     * Query the upcoming events (the closest first)
     * @param number : the number of events to retrieve
     * @param activityList: the list of event that will be set when the DB returns the information
     * @param profile : profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUpcomingEvents(
        number: Int = NUMBER_UPCOMING_EVENTS,
        activityList: Observable<List<Event>>,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>
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
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun addProfile(
        profile: ProfileInterface, uid: String,
        user: UserInterface = currentUser as UserInterface
    ): Observable<Boolean>

    /**
     * Remove the profile from the user in database
     * @param profile: The profile we want to remove
     * @param uid : the uid of the user from which we want to query the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeProfile(
        profile: ProfileInterface,
        uid: String = (User.currentUser as UserInterface).uid,
        user: UserInterface = User.currentUser as UserInterface
    ): Observable<Boolean>

    /**
     * Update profile
     * @param event event to update
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateProfile(
        profile: ProfileInterface,
        user: UserInterface = currentUser as UserInterface
    ): Observable<Boolean>

    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param profileList mutable live data in which the list of profile will be set
     * @param user user for database access
     * @return list of profile of a user uid
     */
    fun getListProfile(
        uid: String,
        profileList:Observable<List<ProfileInterface>>,
        user: UserInterface = currentUser as UserInterface
    ): Observable<Boolean>
        */

    /**
     * Update the user information in the database
     * @param newValues : a map with the new value to set in the database
     * @param uid : the uid of the user from which we want to query the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateUserInformation(
        newValues: HashMap<String, String>,
        uid: String,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): Observable<Boolean>

    /**
     * Register the user in the database with its basic information (uid, email, name)
     * @param user : user with all the requested information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param isInDb : Will be set to true if in Database or to false otherwise
     * @param uid : user uid we want to check the existence
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param user : live data that will be set with the find user value
     * @param uid : user uid we want to get the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserInformation(
        user: Observable<UserInterface>,
        uid: String = (User.currentUser as UserInterface).uid,
        userAccess: UserInterface = User.currentUser as UserInterface
    ): Observable<Boolean>

    /*
    /**
     * Returns the list of items
     * @return The current mutable list of items
     */
    fun getItemsList(): MutableList<String>

    /**
     * Adds an Item to the Item Database
     * @param item : item to add
     * @return true if the item is successfully added to the database
     */
    fun addItem(item: String): Boolean

    /**
     * Removes an Item from the Item Database
     * @param item : item to remove
     * @return true if the item is successfully removed from the database
     */
    fun removeItem(item: String): Boolean

    /**
     * TODO : adapt into asynchronous method
     * Fetch the available items
     * @return (for now) map of pair : <item name, available quantity>
     */
    fun getAvailableItems(): Map<String, Int>

     */
}