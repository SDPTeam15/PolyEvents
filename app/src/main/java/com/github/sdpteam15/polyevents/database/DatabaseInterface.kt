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
     * Current user of this database
     */
    val currentUser: UserEntity?
    val currentProfile: UserProfile?
    var itemDatabase: ItemDatabaseInterface?
    var zoneDatabase: ZoneDatabaseInterface?
    var userDatabase: UserDatabaseInterface?
    var heatmapDatabase: HeatmapDatabaseInterface?
    var eventDatabase: EventDatabaseInterface?
    var materialRequestDatabase: MaterialRequestDatabaseInterface?

    /**
     * Add an Entity to the data base
     * @param element
     * @param collection
     * @param adapter
     * @return
     */
    fun <T> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile? = currentProfile
    ): Observable<String>

    /**
     * Add an Entity to the data base
     * @param element
     * @param collection
     * @param adapter
     * @return
     */
    fun <T> addEntity(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Set an Entity to the data base
     * @param element null for delete
     * @param id
     * @param collection
     * @param adapter
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
     * @param id
     * @param collection
     * @param adapter
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun deleteEntity(
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * get an Entity to the data base
     * @param element
     * @param id
     * @param collection
     * @param adapter
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
     * get a list Entity to the data base
     * @param elements
     * @param ids
     * @param collection
     * @param adapter
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