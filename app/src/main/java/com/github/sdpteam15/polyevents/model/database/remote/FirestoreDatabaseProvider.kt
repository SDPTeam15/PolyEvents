package com.github.sdpteam15.polyevents.model.database.remote

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.database.local.adapter.LocalCacheAdapter
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.matcher.FirestoreQuery
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreDatabaseProvider : DatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    private val localCache by lazy { LocalCacheAdapter(this) }

    override var itemDatabase: ItemDatabaseInterface =
        ItemDatabase(localCache)
    override var zoneDatabase: ZoneDatabaseInterface =
        ZoneDatabase(localCache)
    override var userDatabase: UserDatabaseInterface =
        UserDatabase(localCache)
    override var heatmapDatabase: HeatmapDatabaseInterface =
        HeatmapDatabase(localCache)
    override var eventDatabase: EventDatabaseInterface =
        EventDatabase(localCache)
    override var materialRequestDatabase: MaterialRequestDatabaseInterface =
        MaterialRequestDatabase(localCache)
    override var routeDatabase: RouteDatabaseInterface =
        RouteDatabase(localCache)
    override var userSettingsDatabase: UserSettingsDatabaseInterface =
        UserSettingsDatabase(localCache)

    override val currentUserObservable = Observable<UserEntity>()
    private var loadSuccess: Boolean? = false
    override var currentUser: UserEntity?
        get() {
            if (UserLogin.currentUserLogin.isConnected()) {
                if (loadSuccess == false) {
                    loadSuccess = null
                    currentUserObservable.postValue(
                        UserLogin.currentUserLogin.getCurrentUser()!!,
                        this
                    )
                    firestore!!.collection(USER_COLLECTION.value)
                        .document(currentUserObservable.value!!.uid)
                        .get()
                        .addOnSuccessListener {
                            loadSuccess = it.data != null
                            if (loadSuccess!!)
                                currentUserObservable.postValue(
                                    UserAdapter.fromDocument(
                                        it.data!!,
                                        it.id
                                    ), this
                                )
                        }
                        .addOnFailureListener {
                            loadSuccess = false
                        }
                }
                return currentUserObservable.value
            } else {
                loadSuccess = false
                return null
            }
        }
        set(value) {
            loadSuccess = value != null
            currentUserObservable.postValue(value, this)
        }


    override fun <T : Any> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<String> {
        val ended = Observable<String>()
        firestore!!
            .collection(collection.value)
            .add(adapter.toDocumentWithoutNull(element))
            .addOnSuccessListener { ended.postValue(it.id, this) }
            .addOnFailureListener { ended.postValue("", this) }
        return ended
    }

    override fun <T : Any> addListEntity(
        elements: List<T>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<String>>> {
        if (elements.isEmpty())
            return Observable(Pair(true, listOf()))

        val ended = Observable<Pair<Boolean, List<String>>>()
        val mutableList = MutableList<String?>(elements.size) { null }

        //The check to notify the end of all the process
        val checkIfDone = { index: Int, id: String? ->
            synchronized(this) {
                mutableList[index] = id ?: ""
                //Check that all ids are initialized
                if (mutableList.fold(true) { a, s -> a && s != null })
                    ended.postValue(
                        Pair(
                            mutableList.fold(true) { a, s -> a && s != "" },
                            mutableList.map { it ?: "" }
                        ), this
                    )
            }
        }

        //For each elements send a request to firestore to add a the element
        for (elementWithIndex in elements.withIndex()) {
            firestore!!
                .collection(collection.value)
                .add(adapter.toDocumentWithoutNull(elementWithIndex.value))
                .addOnSuccessListener {
                    checkIfDone(elementWithIndex.index, it.id)
                }
                .addOnFailureListener {
                    checkIfDone(elementWithIndex.index, null)
                }
        }
        return ended
    }

    override fun <T : Any> setEntity(
        element: T?,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        val document = firestore!!
            .collection(collection.value)
            .document(id)

        adapter.toDocument(element).apply({
            document.set(it)
        }, lazy { document.delete() })
            .addOnSuccessListener { ended.postValue(true, this) }
            .addOnFailureListener { ended.postValue(false, this) }
        return ended
    }

    override fun <T : Any> setListEntity(
        elements: List<Pair<String, T?>>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<Boolean>>> {
        if (elements.isEmpty())
            return Observable(Pair(true, listOf()))
        val ended = Observable<Pair<Boolean, List<Boolean>>>()
        val mutableList = MutableList<Boolean?>(elements.size) { null }
        //The check to notify the end of all the process
        val checkIfDone = { index: Int, bool: Boolean ->
            synchronized(this) {
                mutableList[index] = bool
                //Check that all ids are initialized
                if (mutableList.fold(true) { a, b -> a && b != null })
                    ended.postValue(
                        Pair(
                            mutableList.fold(true) { a, b -> a && b ?: false },
                            mutableList.map { it ?: false }
                        ), this
                    )
            }
        }

        for (elementWithIndex in elements.withIndex()) {
            val document = firestore!!
                .collection(collection.value)
                .document(elementWithIndex.value.first)
            adapter.toDocument(elementWithIndex.value.second)
                .apply(document.delete()) { document.set(it) }
                .addOnSuccessListener {
                    checkIfDone(elementWithIndex.index, true)
                }
                .addOnFailureListener {
                    checkIfDone(elementWithIndex.index, false)
                }
        }
        return ended
    }

    override fun <T : Any> getEntity(
        element: Observable<T>,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        firestore!!.collection(collection.value)
            .document(id)
            .get()
            .addOnSuccessListener {
                // if the data can be transform to a entity
                // return it in element and true in ended
                // else return false in ended
                if (it.data != null) {
                    val result = adapter.fromDocument(it.data!!, it.id)
                    if (result != null) {
                        element.postValue(result, this)
                        ended.postValue(true, this)
                    } else
                        ended.postValue(false, this)
                } else
                    ended.postValue(false, this)
            }
            .addOnFailureListener {
                ended.postValue(false, this)
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
        val fsCollection = firestore!!.collection(collection.value)
        return ids.apply({
            getMapEntity(elements, it, fsCollection, adapter)
        }, lazy {
            getMapEntity(elements, matcher, fsCollection, adapter)
        })
    }

    private fun <T : Any> getMapEntity(
        elements: ObservableMap<String, T>,
        ids: List<String>,
        fsCollection: CollectionReference,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        if (ids.isEmpty())
            ended.postValue(true, this)
        val mutableList = MutableList<Pair<String?, T?>?>(ids.size) { null }

        //The check to notify the end of all the process
        val checkIfDone = { index: Int, data: Map<String, Any>?, id: String? ->
            synchronized(this) {
                mutableList[index] = Pair(
                    id,
                    id.apply { id -> data.apply { data -> adapter.fromDocument(data, id) } })
                //Check that all ids are initialized
                if (mutableList.fold(true) { a, p -> a && p != null }) {
                    val map = mutableMapOf<String, T>()
                    mutableList.forEach {
                        it.apply { pair ->
                            pair.first.apply { id ->
                                pair.second.apply { element ->
                                    map[id] = element
                                }
                            }
                        }
                    }
                    elements.updateAll(map, this)
                    ended.postValue(mutableList.fold(true) { a, p -> a && p?.second != null }, this)
                }
            }
        }

        // for each ids start to retrieve the data from database
        for (idWithIndex in ids.withIndex()) {
            fsCollection.document(idWithIndex.value)
                .get()
                .addOnSuccessListener {
                    checkIfDone(idWithIndex.index, it.data, idWithIndex.value)
                }
                .addOnFailureListener {
                    checkIfDone(idWithIndex.index, null, idWithIndex.value)
                }
        }
        return ended
    }

    private fun <T : Any> getMapEntity(
        elements: ObservableMap<String, T>,
        matcher: Matcher?,
        fsCollection: CollectionReference,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        // apply the matcher
        (matcher?.match(FirestoreQuery(fsCollection)) ?: FirestoreQuery(fsCollection))
            .get()
            .observeOnce {
                it.value.first.apply { qs ->
                    val map = mutableMapOf<String, T>()
                    // add all elements that satisfies the matcher to the ObservableMap
                    qs.forEach { e ->
                        adapter.fromDocument(e.data, e.id)
                            .apply { value -> map[e.id] = value }
                    }
                    elements.updateAll(map, this)
                    ended.postValue(true, this)
                }
                it.value.second.apply { ended.postValue(false, this) }
            }
        return ended
    }
}
