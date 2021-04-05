package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.android.gms.maps.model.LatLng
import java.util.*

const val NUMBER_UPCOMING_EVENTS = 3

/**
 * Database interface
 */
interface DatabaseInterface {

    /**
     * Current user of this database
     */
    val currentUser: UserEntity?
    val currentProfile: UserProfile?

    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param user user for database access
     * @return list of profile of a user uid
     */
    //@Deprecated(message = "Use the asynchronous method")
    fun getProfilesList(
        uid: String,
        user: UserEntity? = currentUser
    ): List<UserProfile>

    /**
     * Add profile to a user
     * @param profile profile to add
     * @param uid uid
     * @param user user for database access
     * @return if the operation succeed
     */
    //@Deprecated(message = "Use the asynchronous method")
    fun addProfile(
        profile: UserProfile, uid: String,
        user: UserEntity? = currentUser
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
        profile: UserProfile, uid: String? = currentUser?.uid,
        user: UserEntity? = currentUser
    ): Boolean

    /**
     * Update profile in database
     * @param profile profile to update
     * @param user profile for database access
     * @return if the operation succeed
     */
    //@Deprecated(message = "Use the asynchronous method")
    fun updateProfile(
        profile: UserProfile,
        user: UserEntity? = currentUser
    ): Boolean

    /**
     * Get list of event
     * @param matcher matcher for the recherche
     * @param number maximum of result
     * @param profile profile for database access
     * @return list of event
     */
    //@Deprecated(message = "Use the asynchronous method")
    fun getListEvent(
        matcher: String? = null, number: Int? = null,
        profile: UserProfile? = currentProfile
    ): List<Event>

    /**
     * Query the upcoming events
     * @param number : the number of events to retrieve
     * @param profile profile for database access
     * @return List of events in upcoming order (closest first)
     */
    //@Deprecated(message = "Use the asynchronous method")
    fun getUpcomingEvents(
        number: Int = NUMBER_UPCOMING_EVENTS,
        profile: UserProfile? = currentProfile
    ): List<Event>

    /**
     * Get event from ID
     * @param id ID of the event
     * @param profile profile for database access
     * @return event corresponding to the given ID
     */
    //@Deprecated(message = "Use the asynchronous method")
    fun getEventFromId(
        id: String,
        profile: UserProfile? = currentProfile
    ): Event?

    /**
     * Update or request an update for an event
     * @param Event event to update
     * @param profile profile for database access
     */
    //@Deprecated(message = "Use the asynchronous method")
    fun updateEvent(
        Event: Event,
        profile: UserProfile? = currentProfile
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
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createItem(
        item: Item,
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
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>*/

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
    /*
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
     * @param activityList: the list of event that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getListEvent(
        matcher: String? = null,
        number: Int? = null,
        activityList: Observable<List<Event>>,
        profile: ProfileInterface = CurrentProfile
    ): Observable<Boolean>

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

    // TODO: Do we need userAccess for these methods? (Might do these with security rules)
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
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Register the user in the database with its basic information (uid, email, name)
     * @param user : user with all the requested information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun firstConnexion(
        user: UserEntity,
        userAccess: UserEntity? = currentUser
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
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param user : live data that will be set with the find user value
     * @param uid : user uid we want to get the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String? = currentUser?.uid,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Returns the list of items
     * @return The current mutable list of items
     */
    fun getItemsList(): MutableList<Item>

    /**
     * Adds an Item to the Item Database
     * @param item : item to add
     * @return true if the item is successfully added to the database
     */
    fun addItem(item : Item):Boolean

    /**
     * Removes an Item from the Item Database
     * @param item : item to remove
     * @return true if the item is successfully removed from the database
     */
    fun removeItem(item: Item): Boolean

    /**
     * TODO : adapt into asynchronous method
     * Fetch the available items
     * @return (for now) map of pair : <item name, available quantity>
     */
    fun getAvailableItems(): Map<String, Int>

    /**
     * Update, or add if it was not already in the database, the current location
     * (provided by the GeoPoint) of the user in the database.
     * @param location: current location of the user
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun setUserLocation(
        location: LatLng,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Fetch the current users locations.
     * @param usersLocations: the list of users locations that will be set when
     * the DB returns the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUsersLocations(
        usersLocations: Observable<List<LatLng>>,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>
}