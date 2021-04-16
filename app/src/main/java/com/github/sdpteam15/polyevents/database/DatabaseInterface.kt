package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.*
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
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * @param itemId id of the item we want to remove from the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeItem(
        itemId: String,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * @param item item we want to update in the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateItem(
        item: Item,
        count: Int,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of items
     * @param itemList: the list of items that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of available items
     * @param itemList: the list of items that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile? = currentProfile
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
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Update or request an update for an event
     * @param event: event to update
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateEvents(
        event: Event,
        profile: UserProfile? = currentProfile
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
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of event
     * @param matcher: matcher for the search
     * @param limit: maximum number of results
     * @param eventList: the list of event that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEvents(
        matcher: Matcher? = null,
        limit: Long? = null,
        eventList: ObservableList<Event>,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of events sorted by their start times.
     *
     * @param matcher a custom matcher for the search
     * @param limit maximum number of resukts
     * @param eventList list of events that will be set when the DB returns the info
     * @param profile profile of the user for database access
     * @return an observer that will be set to true if the communication with the DB is over and no error
     */
    fun getEventsSortedByStartTime(
        matcher: Matcher? = null,
        limit: Long? = null,
        eventList: ObservableList<Event>,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>


    /**
     * All accessor and modifier methods for users and profiles
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
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Update profile
     * @param newValues : a map with the new value to set in the database
     * @param pid : the uid of the profile from which we want to query the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateProfile(
        newValues: Map<String, String>,
        pid: String,
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
     * @param user : live data that will be set with the user information from the database
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
     * Look in the database if the user already exists or not
     * @param profile : live data that will be set with the find profile value
     * @param pid : profile id we want to get
     * @param userAccess : the profile object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

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
     * @param usersLocations: the list of users locations that will be set when the DB returns the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUsersLocations(
        usersLocations: Observable<List<LatLng>>,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Store the newly created zone information in the database
     * @param zone: the zone information we should insert
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createZone(
        zone: Zone,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Get the zone information from the database
     * @param zoneId: The id of the zone we want to get the information
     * @param zone:  live data that will be set with the zone information from the database
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Update the zone information in the databae
     * @param zoneId: The id of the zone we want to get the information
     * @param newZone: The updated zone information we should store in the database
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateZoneInformation(
        zoneId: String,
        newZone: Zone,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>
}