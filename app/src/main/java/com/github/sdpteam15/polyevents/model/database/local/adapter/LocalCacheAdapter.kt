package com.github.sdpteam15.polyevents.model.database.local.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
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
                            adapter.toDocumentWithoutNull(element),
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
                                adapter.toDocumentWithoutNull(value.second),
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
                                adapter.toDocumentWithoutNull(element),
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
                                adapter.toDocumentWithoutNull(value.second.second!!),
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
        updateLocal(collection, adapter).observeOnce {
            if (it.value) {
                // retrieve the data from local cash
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    PolyEventsApplication.application.localDatabase.genericEntityDao()
                        .get(id, collection.value).apply({ ge ->
                            adapter.fromDocument(LocalAdapter.fromDocument(ge).first, ge.id)
                                .apply({ e ->
                                    element.postValue(e, it.value)
                                    ended.postValue(true, it.value)
                                }, lazy {
                                    ended.postValue(false, it.value)
                                })
                        }, lazy {
                            ended.postValue(false, it.value)
                        })
                }
            } else
                ended.postValue(false, it.value)
        }
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
        updateLocal(collection, adapter).observeOnce {
            if (it.value) {
                ids.apply({ ids ->
                    val mutableList = MutableList<Pair<String?, T?>?>(ids.size) { null }

                    //The check to notify the end of all the process
                    val checkIfDone = { index: Int, data: Map<String, Any?>?, id: String? ->
                        synchronized(this) {
                            mutableList[index] = Pair(
                                id,
                                id.apply { id ->
                                    data.apply { data ->
                                        adapter.fromDocument(
                                            data,
                                            id
                                        )
                                    }
                                })
                            //Check that all ids are initialized
                            if (mutableList.fold(true) { a, p -> a && p != null }) {
                                val map = mutableMapOf<String, T>()
                                mutableList.forEach { p ->
                                    p.apply { pair ->
                                        pair.first.apply { id ->
                                            pair.second.apply { element ->
                                                map[id] = element
                                            }
                                        }
                                    }
                                }
                                elements.updateAll(map, this)
                                ended.postValue(
                                    mutableList.fold(true) { a, p -> a && p?.second != null },
                                    this
                                )
                            }
                        }
                    }

                    // get all elements from local cache
                    for (idWithIndex in ids.withIndex()) {
                        PolyEventsApplication.application.applicationScope.launch(
                            Dispatchers.IO
                        ) {
                            PolyEventsApplication.application.localDatabase.genericEntityDao()
                                .get(idWithIndex.value, collection.value).apply({ ge ->
                                    val pair = LocalAdapter.fromDocument(ge)
                                    checkIfDone(idWithIndex.index, pair.first, pair.second)
                                }, lazy {
                                    checkIfDone(idWithIndex.index, null, null)
                                })
                        }
                    }
                }, lazy {
                    PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                        // get all elements from local cache and transform it from GenericEntity
                        val query = CodeQuery.CodeQueryFromIterator(
                            PolyEventsApplication.application.localDatabase.genericEntityDao()
                                .getAll(collection.value)
                                .iterator()
                        ) { ge ->
                            QueryDocumentSnapshot(
                                LocalAdapter.fromDocument(ge).first,
                                ge.id
                            )
                        }

                        // apply the matcher
                        matcher.apply(query) { matcher -> matcher.match(query) }.get()
                            .observeOnce { oqs ->
                                oqs.value.first.apply { qs ->
                                    // add all elements that satisfies the matcher to the ObservableMap
                                    val map = mutableMapOf<String, T>()
                                    qs.forEach { e ->
                                        adapter.fromDocument(e.data, e.id)
                                            .apply { value -> map[e.id] = value }
                                    }
                                    elements.updateAll(map, db)
                                    ended.postValue(true, db)
                                }
                            }
                    }
                })
            } else
                ended.postValue(it.value, it.sender)
        }
        return ended
    }

    /**
     * update the local cache by taking the new value on the remote one
     * @param collection collection
     * @param adapter adapter for the collection
     */
    private fun <T : Any> updateLocal(
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
            val date = PolyEventsApplication.application.localDatabase.genericEntityDao()
                .lastUpdateDate(collection.value)
            val temp = ObservableMap<String, T>()
            db.getMapEntity(
                temp,
                null,
                date.apply { date -> { it.whereGreaterThan(LogAdapter.LAST_UPDATE, date) } },
                collection,
                LogAdapterFromDocument(adapter)
            ).observeOnce {
                if (it.value) {
                    PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                        try
                        {
                                temp.forEach { element ->
                                    PolyEventsApplication.application.localDatabase.genericEntityDao()
                                        .insert(
                                            LocalAdapter.toDocument(
                                                (collection.adapter as AdapterToDocumentInterface<T>).toDocumentWithoutNull(
                                                    element.value
                                                ),
                                                element.key,
                                                collection.value
                                            )
                                        )
                                }
                                ended.postValue(true, it.sender)
                        }
                        catch (e : ClassCastException){
                            ended.postValue(true, it.sender)
                        }
                    }
                } else
                    ended.postValue(false, it.sender)
            }
        }
        return ended
    }
}