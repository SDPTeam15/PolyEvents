package com.github.sdpteam15.polyevents.model.database.local

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.database.local.room.LogAdapter
import com.github.sdpteam15.polyevents.model.database.local.room.LogAdapterToDocument
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.CodeQuery
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.matcher.QueryDocumentSnapshot
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import kotlinx.coroutines.Dispatchers

/**
 * Add a local cash for any entity called by the DatabaseInterface given
 * @param db the database
 */
@Suppress("UNCHECKED_CAST")
class LocalCacheAdapter(private val db: DatabaseInterface) : DatabaseInterface {
    override val currentUserObservable: Observable<UserEntity>
        get() = db.currentUserObservable

    override var currentUser: UserEntity?
        get() = db.currentUser
        set(value) {
            db.currentUser = value
        }

    override var itemDatabase: ItemDatabaseInterface
        get() = db.itemDatabase
        set(value) {
            db.itemDatabase = value
        }

    override var zoneDatabase: ZoneDatabaseInterface
        get() = db.zoneDatabase
        set(value) {
            db.zoneDatabase = value
        }

    override var userDatabase: UserDatabaseInterface
        get() = db.userDatabase
        set(value) {
            db.userDatabase = value
        }

    override var heatmapDatabase: HeatmapDatabaseInterface
        get() = db.heatmapDatabase
        set(value) {
            db.heatmapDatabase = value
        }

    override var eventDatabase: EventDatabaseInterface
        get() = db.eventDatabase
        set(value) {
            db.eventDatabase = value
        }

    override var materialRequestDatabase: MaterialRequestDatabaseInterface
        get() = db.materialRequestDatabase
        set(value) {
            db.materialRequestDatabase = value
        }

    override var userSettingsDatabase: UserSettingsDatabaseInterface
        get() = db.userSettingsDatabase
        set(value) {
            db.userSettingsDatabase = value
        }

    override var routeDatabase: RouteDatabaseInterface
        get() = db.routeDatabase
        set(value) {
            db.routeDatabase = value
        }

    override fun <T : Any> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<String> =
        db.addEntityAndGetId(
            element,
            collection,
            LogAdapterToDocument(adapter)
        ).observeOnce {
            //If added on remote db add it to the cache
            if (it.value != "") {
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    PolyEventsApplication.application.localDatabase.genericEntityDao().insert(
                        LocalAdapter.toDocument(
                            adapter.toDocument(element),
                            it.value,
                            collection.value
                        )
                    )
                }
            }
        }.then

    override fun <T : Any> addListEntity(
        elements: List<T>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<String>>> =
        db.addListEntity(
            elements,
            collection,
            LogAdapterToDocument(adapter)
        ).observeOnce {
            //If added on remote db add it to the cache
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                for (value in it.value.second.zip(elements))
                    if (value.first != "")
                        PolyEventsApplication.application.localDatabase.genericEntityDao().insert(
                            LocalAdapter.toDocument(
                                adapter.toDocument(value.second),
                                value.first,
                                collection.value
                            )
                        )
            }
        }.then

    override fun <T : Any> setEntity(
        element: T?,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Boolean> =
        db.setEntity(
            element,
            id,
            collection,
            LogAdapterToDocument(adapter)
        ).observeOnce {
            //If set on remote db add it to the cache
            if (it.value) {
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    if (element != null)
                        PolyEventsApplication.application.localDatabase.genericEntityDao().insert(
                            LocalAdapter.toDocument(
                                adapter.toDocument(element),
                                id,
                                collection.value
                            )
                        )
                }
            }
        }.then

    override fun <T : Any> setListEntity(
        elements: List<Pair<String, T?>>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<Boolean>>> =
        db.setListEntity(
            elements,
            collection,
            LogAdapterToDocument(adapter)
        ).observeOnce {
            //If set on remote db add it to the cache
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                for (value in it.value.second.zip(elements))
                    if (value.first && value.second.second != null)
                        PolyEventsApplication.application.localDatabase.genericEntityDao().insert(
                            LocalAdapter.toDocument(
                                adapter.toDocument(value.second.second!!),
                                value.second.first,
                                collection.value
                            )
                        )
            }
        }.then

    override fun <T : Any> getEntity(
        element: Observable<T>,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        HelperFunctions.run(Runnable {
            //Get from local cache
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                PolyEventsApplication.application.localDatabase.genericEntityDao()
                    .get(id, collection.value).apply {
                        val pair = LocalAdapter.fromDocument(it)
                        val result = adapter.fromDocument(pair.first, pair.second)
                        element.postValue(result, db)
                        if (result != null)
                            ended.postValue(true, db)
                    }

                //Update local cache and modify the result if necessary
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    update(
                        collection,
                        adapter,
                        ended,
                        element = element,
                        id = id
                    )
                }
            }
        })
        return ended
    }

    override fun <T : Any> getMapEntity(
        elements: ObservableMap<String, T>,
        ids: List<String>?,
        matcher: Matcher?,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        HelperFunctions.run(Runnable {
            //Get from local cache
            ids.apply({ ids ->
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    val map = mutableMapOf<String, T>()
                    for (id in ids) {
                        PolyEventsApplication.application.localDatabase.genericEntityDao()
                            .get(id, collection.value)
                            .apply {
                                val pair = LocalAdapter.fromDocument(it)
                                adapter.fromDocument(pair.first, pair.second).apply { map[id] = it }
                            }
                    }
                    elements.updateAll(map, db)
                    ended.postValue(true, db)
                }
            }, lazy {
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    val query = CodeQuery.CodeQueryFromIterator(
                        PolyEventsApplication.application.localDatabase.genericEntityDao()
                            .getAll(collection.value).iterator()
                    ) {
                        QueryDocumentSnapshot(
                            LocalAdapter.fromDocument(it).first,
                            it.id
                        )
                    }
                    (matcher?.match(query) ?: query).get().addOnSuccessListener {
                        val map = mutableMapOf<String, T>()
                        for (value in it) {
                            val result = adapter.fromDocument(value.data, value.id)
                            if (result != null)
                                map[value.id] = result
                        }
                        elements.updateAll(map, db)
                        ended.postValue(true, db)
                    }
                }
            })

            //Update local cache and modify the result if necessary
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                //Update local cache and modify the result if necessary
                update(
                    collection,
                    adapter,
                    ended,
                    elements = elements,
                    ids = ids,
                    matcher = matcher
                )
            }
        })
        return ended
    }

    /**
     * update a collection in local db from the remote db
     */
    suspend fun <T : Any> update(
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        HelperFunctions.run(Runnable {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                update(collection, adapter, ended, adapterToDocument = adapter)
            }
        })
        return ended
    }

    private suspend fun <T : Any> update(
        collection: DatabaseConstant.CollectionConstant,
        adapterFromDocument: AdapterFromDocumentInterface<out T>,
        ended: Observable<Boolean>,
        adapterToDocument: AdapterToDocumentInterface<in T>? = null,
        element: Observable<T>? = null,
        id: String? = null,
        elements: ObservableMap<String, T>? = null,
        ids: List<String>? = null,
        matcher: Matcher? = null,
    ) {
        //Get all elements outdated in the cache
        val date = PolyEventsApplication.application.localDatabase.genericEntityDao()
            .lastUpdateDate(collection.value)
        db.getMapEntity(
            ObservableMap<String, T>().observeOnce {
                //Update local cache
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    for (key in it.value.keys) {
                        PolyEventsApplication.application.localDatabase.genericEntityDao()
                            .insert(
                                LocalAdapter.toDocument(
                                    ((adapterToDocument ?: collection.adapter)
                                            as AdapterToDocumentInterface<in T>)
                                        .toDocument(it.value[key]!!),
                                    key,
                                    collection.value
                                )
                            )
                    }
                }

                //If from getEntity
                if (element != null && id != null) {
                    if (element.value != it.value[id]) {
                        element.postValue(it.value[id], it.sender)
                        if (element.value != null)
                            ended.postValue(true, it.sender)
                    }
                    if (element.value == null)
                        ended.postValue(false, it.sender)
                }

                //If from getMapEntity
                if (elements != null) {
                    ids.apply({ ids ->
                        val map = mutableMapOf<String, T>()
                        ids.forEach { eid ->
                            it.value[eid].apply { value ->
                                if (elements[eid] != value)
                                    map[eid] = value
                            }
                        }
                        if (map.isNotEmpty()) {
                            elements.putAll(map, it.sender)
                            ended.postValue(true, it.sender)
                        }
                    }, lazy {
                        //Create the query form the iterator of the db
                        val query =
                            CodeQuery.CodeQueryFromIterator(it.value.entries.iterator()) { entrie ->
                                ((adapterToDocument ?: collection.adapter)
                                        as AdapterToDocumentInterface<in T>)
                                    .toDocument(entrie.value).apply { data ->
                                        QueryDocumentSnapshot(
                                            data,
                                            entrie.key
                                        )
                                    }
                            }

                        //Apply the matcher and add all new elements to the result
                        (matcher?.match(query) ?: query).get().addOnSuccessListener { qs ->
                            val map = mutableMapOf<String, T>()
                            qs.forEach { value ->
                                adapterFromDocument.fromDocument(value.data, value.id)
                                    .apply { entity -> map[value.id] = entity }
                            }
                            if (map.isNotEmpty()) {
                                elements.putAll(map, it.sender)
                                ended.postValue(true, it.sender)
                            }
                        }
                    })
                }
            }.then,
            null,
            {
                date.apply(it) { date -> it.whereGreaterThan(LogAdapter.LAST_UPDATE, date) }
            },
            collection,
            adapterFromDocument
        ).observeOnce {
            if (ended.value == null)
                ended.postValue(it.value, it.sender)
        }
    }
}