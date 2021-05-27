package com.github.sdpteam15.polyevents.model.database.local.room

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
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalCacheAdapter(private val db: DatabaseInterface) : DatabaseInterface {
    override val currentUserObservable: Observable<UserEntity>
        get() = db.currentUserObservable

    override var currentUser: UserEntity?
        get() = db.currentUser
        set(value) {
            db.currentUser = value
        }

    override var currentProfile: UserProfile?
        get() = db.currentProfile
        set(value) {
            db.currentProfile = value
        }

    override var itemDatabase: ItemDatabaseInterface?
        get() = db.itemDatabase
        set(value) {
            db.itemDatabase = value
        }

    override var zoneDatabase: ZoneDatabaseInterface?
        get() = db.zoneDatabase
        set(value) {
            db.zoneDatabase = value
        }

    override var userDatabase: UserDatabaseInterface?
        get() = db.userDatabase
        set(value) {
            db.userDatabase = value
        }

    override var heatmapDatabase: HeatmapDatabaseInterface?
        get() = db.heatmapDatabase
        set(value) {
            db.heatmapDatabase = value
        }

    override var eventDatabase: EventDatabaseInterface?
        get() = db.eventDatabase
        set(value) {
            db.eventDatabase = value
        }

    override var materialRequestDatabase: MaterialRequestDatabaseInterface?
        get() = db.materialRequestDatabase
        set(value) {
            db.materialRequestDatabase = value
        }

    override var userSettingsDatabase: UserSettingsDatabaseInterface?
        get() = db.userSettingsDatabase
        set(value) {
            db.userSettingsDatabase = value
        }

    override var routeDatabase: RouteDatabaseInterface?
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
            if (it.value != "") {
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    PolyEventsApplication.application.database.genericEntityDao().insert(
                        LocalAdapterToDocument(adapter).toDocument(element, it.value)!!
                    )
                }
            }
        }.then

    override fun <T : Any> addListEntity(
        elements: List<T>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<String?>>> =
        db.addListEntity(
            elements,
            collection,
            LogAdapterToDocument(adapter)
        ).observeOnce {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                for (value in it.value.second.zip(elements))
                    if (value.first != null)
                        PolyEventsApplication.application.database.genericEntityDao().insert(
                            LocalAdapterToDocument(adapter).toDocument(
                                value.second,
                                value.first!!
                            )!!
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
            if (it.value) {
                PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                    PolyEventsApplication.application.database.genericEntityDao().insert(
                        LocalAdapterToDocument(adapter).toDocument(element, id)!!
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
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                for (value in it.value.second.zip(elements))
                    if (value.first)
                        PolyEventsApplication.application.database.genericEntityDao().insert(
                            LocalAdapterToDocument(adapter).toDocument(
                                value.second.second,
                                value.second.first
                            )!!
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
        PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
            val result = LocalAdapterFromDocument(adapter).fromDocument(
                PolyEventsApplication.application.database.genericEntityDao().get(id, collection)
            )
            element.postValue(result, db)
            if (result != null)
                ended.postValue(true, db)
            Update(collection, adapter, ended, element = element, id = id)
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
        PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
            if (ids != null) {
                val map = mutableMapOf<String, T>()
                for (id in ids) {
                    val result = LocalAdapterFromDocument(adapter).fromDocument(
                        PolyEventsApplication.application.database.genericEntityDao()
                            .get(id, collection)
                    )
                    if (result != null)
                        map[id] = result
                }
                elements.updateAll(map, db)
                ended.postValue(true, db)
            } else {
                val query = CodeQuery.CodeQueryFromIterator(
                    PolyEventsApplication.application.database.genericEntityDao()
                        .getAll(collection).iterator()
                ) {
                    QueryDocumentSnapshot(
                        LocalAdapterFromDocument.toMap(it.data!!) as Map<String, Any?>,
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
            Update(collection, adapter, ended, elements = elements, ids = ids, matcher = matcher)
        }
        return ended
    }

    fun <T : Any> Update(
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        Update(collection, adapter, ended, adapterToDocument = adapter)
        return ended
    }

    private fun <T : Any> Update(
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
        PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
            val datestr =
                PolyEventsApplication.application.database.genericEntityDao().lastUpdate(collection)
            val date = if (datestr != null) LocalAdapterFromDocument.toDate(datestr) else null
            db.getMapEntity(
                ObservableMap<String, T>().observeOnce {
                    PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                        for (key in it.value.keys) {
                            val v = LocalAdapterToDocument(
                                (adapterToDocument ?: collection.adapter)
                                        as AdapterToDocumentInterface<in T>
                            ).toDocument(it.value[key], key)
                            if (v != null)
                                PolyEventsApplication.application.database.genericEntityDao()
                                    .insert(v)
                        }
                    }
                    if (element != null && id != null) {
                        if (element.value != it.value[id])
                            if (it.value[id] != null) {
                                element.postValue(it.value[id], it.sender)
                                ended.postValue(true, it.sender)
                            }
                        if (element.value == null)
                            ended.postValue(false, it.sender)
                    }
                    if (elements != null) {
                        if (ids != null) {
                            val map = mutableMapOf<String, T>()
                            for (id in ids) {
                                if (elements[id] != it.value[id])
                                    if (it.value[id] != null)
                                        map[id] = it.value[id]!!
                            }
                            elements.putAll(map, it.sender)
                        } else {
                            val v = it.value.entries.iterator()
                            val query =
                                CodeQuery.CodeQueryFromIterator(it.value.entries.iterator()) {
                                    QueryDocumentSnapshot(
                                        ((adapterToDocument ?: collection.adapter)
                                                as AdapterToDocumentInterface<in T>).toDocument(it.value)!!,
                                        it.key
                                    )
                                }
                            (matcher?.match(query) ?: query).get().addOnSuccessListener {
                                val map = mutableMapOf<String, T>()
                                for (value in it) {
                                    val result =
                                        adapterFromDocument.fromDocument(value.data, value.id)
                                    if (result != null)
                                        map[value.id] = result
                                }
                                elements.updateAll(map, db)
                                ended.postValue(true, db)
                            }
                        }
                    }
                }.then, null, {
                    if (date != null) it.whereGreaterThan(LogAdapter.LAST_UPDATE, date)
                    else it
                }, collection, adapterFromDocument
            ).observeOnce {
                if (ended.value == null)
                    ended.postValue(it.value, it.sender)
            }
        }
    }
}