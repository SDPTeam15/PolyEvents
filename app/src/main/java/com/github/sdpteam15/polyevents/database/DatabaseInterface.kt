package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.objects.*
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.*
import com.github.sdpteam15.polyevents.util.AdapterInterface
import java.util.*

const val NUMBER_UPCOMING_EVENTS = 3

/**
 * Database interface
 */
interface DatabaseInterface {

    /**
     * The current user observable of the database
      */
    val currentUserObservable: Observable<UserEntity>

    /**
     * The current user of the database
     */
    var currentUser: UserEntity?

    /**
     * The current profile of the database
     */
    var currentProfile: UserProfile?

    /**
     * The database used to handle query about items
     */
    var itemDatabase: ItemDatabaseInterface?

    /**
     * The database used to handle query about zones
     */
    var zoneDatabase: ZoneDatabaseInterface?

    /**
     * The database used to handle query about users
     */
    var userDatabase: UserDatabaseInterface?

    /**
     * The database used to handle query about heatmap
     */
    var heatmapDatabase: HeatmapDatabaseInterface?

    /**
     * The database used to handle query about events
     */
    var eventDatabase: EventDatabaseInterface?

    /**
     * The database used to handle query about material request
     */
    var materialRequestDatabase: MaterialRequestDatabaseInterface?

    /**
     * Add an Entity to the data base
     * @param element The element that needs to be added in the database
     * @param collection The collection to which we want to add the given element
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile? = currentProfile
    ): Observable<String>

    /**
     * Add an Entity to the data base
     * @param element The element that needs to be added in the database
     * @param collection The collection to which we want to add the given element
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T> addEntity(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Set an Entity to the data base
     * @param element the element to set or null to delete the element from the database
     * @param id The id with which we will set the element
     * @param collection The collection in which we want to set the given element
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T> setEntity(
        element: T?,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>?,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Set an Entity to the data base
     * @param id The id with which we will delete the element
     * @param collection The collection from which we want to delete the given id
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun deleteEntity(
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get an Entity from the database
     * @param element An observable in which the element will be set once retrieve from the database
     * @param id The id with which we will get the element
     * @param collection The collection from which we want to retrieve the entity
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T> getEntity(
        element: Observable<T>,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get a list Entity from the database
     * @param elements An observable list in which the elements will be set once retrieve from the database
     * @param ids The id at which we need to set the element
     * @param collection The collection from which we want to retrieve the list of entity
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T> getListEntity(
        elements: ObservableList<T>,
        ids: List<String>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>
}