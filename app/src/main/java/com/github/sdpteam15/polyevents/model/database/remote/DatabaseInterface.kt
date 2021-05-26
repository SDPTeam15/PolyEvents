package com.github.sdpteam15.polyevents.model.database.remote

import com.github.sdpteam15.polyevents.model.*
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

const val NUMBER_UPCOMING_EVENTS = 3

/**
 * Database interface
 */
@Suppress("UNCHECKED_CAST")
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
    @Deprecated("to remove")
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
     * The database used to handle queries about the current user's settings
     */
    var userSettingsDatabase: UserSettingsDatabaseInterface?

    /**
     * The database used to handle query about to route
     */
    var routeDatabase: RouteDatabaseInterface?

    /**
     * Add an Entity to the data base
     * @param element The element that needs to be added in the database
     * @param collection The collection to which we want to add the given element
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @return An observer that will be set to the id if the communication with the DB is over and no error
     */
    fun <T : Any> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T> = collection.adapter as AdapterToDocumentInterface<T>
    ): Observable<String>

    /**
     * Add an Entity to the data base
     * @param element The element that needs to be added in the database
     * @param collection The collection to which we want to add the given element
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T : Any> addEntity(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T> = collection.adapter as AdapterToDocumentInterface<T>
    ): Observable<Boolean> = addEntityAndGetId(element, collection, adapter).mapOnce { it != "" }.then

    /**
     * Add a list Entity from the database
     * @param elements The elements that needs to be added in the database
     * @param collection The collection from which we want to retrieve the list of entity
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @return An observer that will be set to the ids if the communication with the DB is over and no error
     */
    fun <T : Any> addListEntity(
        elements: List<T>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T> = collection.adapter as AdapterToDocumentInterface<T>
    ): Observable<Pair<Boolean, List<String>?>>

    /**
     * Set an Entity to the data base
     * @param element The element to set or null to delete the element from the database
     * @param id The id with which we will set the element
     * @param collection The collection in which we want to set the given element
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T : Any> setEntity(
        element: T?,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T> = collection.adapter as AdapterToDocumentInterface<T>
    ): Observable<Boolean>

    /**
     * Set a list Entity to the data base
     * @param element The id and element to set or null to delete the element from the database
     * @param collection The collection in which we want to set the given element
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T : Any> setListEntity(
        elements: List<Pair<String, T?>>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T> = collection.adapter as AdapterToDocumentInterface<T>
    ): Observable<Boolean>

    /**
     * Delete an Entity to the data base
     * @param id The id with which we will delete the element
     * @param collection The collection from which we want to delete the given id
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun deleteEntity(
        id: String,
        collection: DatabaseConstant.CollectionConstant
    ): Observable<Boolean> = setEntity(null, id, collection)

    /**
     * Delete a list Entity to the data base
     * @param ids The id with which we will delete the element
     * @param collection The collection from which we want to delete the given id
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun deleteListEntity(
        ids: List<String>,
        collection: DatabaseConstant.CollectionConstant
    ): Observable<Boolean> = setListEntity(ids.map { Pair(it, null) }, collection)

    /**
     * Get an Entity from the database
     * @param element An observable in which the element will be set once retrieve from the database
     * @param id The id with which we will get the element
     * @param collection The collection from which we want to retrieve the entity
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T : Any> getEntity(
        element: Observable<T>,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T> = collection.adapter as AdapterFromDocumentInterface<T>
    ): Observable<Boolean>

    /**
     * Get a list Entity from the database
     * @param elements An observable list in which the elements will be set once retrieve from the database
     * @param ids The ids at which we need to get the element, if null get all
     * @param matcher To filter the elements
     * @param collection The collection from which we want to retrieve the list of entity
     * @param adapter The adapter converting the element into a HashMap recognised by the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun <T : Any> getListEntity(
        elements: ObservableList<T>,
        ids: List<String>? = null,
        matcher: Matcher?,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T> = collection.adapter as AdapterFromDocumentInterface<T>
    ): Observable<Boolean>
}