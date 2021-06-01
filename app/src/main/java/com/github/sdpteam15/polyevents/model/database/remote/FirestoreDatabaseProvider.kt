package com.github.sdpteam15.polyevents.model.database.remote

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.helper.HelperFunctions.thenReturn
import com.github.sdpteam15.polyevents.model.database.local.room.LocalCacheAdapter
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.matcher.FirestoreQuery
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalTime

object FirestoreDatabaseProvider : DatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override var itemDatabase: ItemDatabaseInterface? = null
        get() {
            field = field ?: ItemDatabase(this)
            return field
        }
    override var zoneDatabase: ZoneDatabaseInterface? = null
        get() {
            field = field ?: ZoneDatabase(this)
            return field
        }
    override var userDatabase: UserDatabaseInterface? = null
        get() {
            field = field ?: UserDatabase(this)
            return field
        }
    override var heatmapDatabase: HeatmapDatabaseInterface? = null
        get() {
            field = field ?: HeatmapDatabase(this)
            return field
        }
    override var eventDatabase: EventDatabaseInterface? = null
        get() {
            field = field ?: EventDatabase(this)
            return field
        }
    override var materialRequestDatabase: MaterialRequestDatabaseInterface? = null
        get() {
            field = field ?: MaterialRequestDatabase(this)
            return field
        }
    override var routeDatabase: RouteDatabaseInterface? = null
        get() {
            field = field ?: RouteDatabase(LocalCacheAdapter(this))
            return field
        }
    override var userSettingsDatabase: UserSettingsDatabaseInterface? = null
        get() {
            field = field ?: UserSettingsDatabase(this)
            return field
        }

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

    //TODO change once the current profile has been developed
    override var currentProfile: UserProfile? = UserProfile(userRole = UserRole.ADMIN)


    override fun <T : Any> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<String> {
        val ended = Observable<String>()
        val successListener =
            OnSuccessListener<DocumentReference> { ended.postValue(it.id, this) }
        val failureListener = OnFailureListener {
            ended.postValue("", this)
        }
        firestore!!
            .collection(collection.value)
            .add(adapter.toDocument(element)!!)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
        return ended
    }

    override fun <T : Any> addListEntity(
        elements: List<T>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<String?>>> {
        if (elements.isEmpty())
            return Observable(Pair(true, listOf()))
        val ended = Observable<Pair<Boolean, List<String?>>>()
        val mutableList = mutableListOf<Pair<Boolean, String?>?>()
        for (elementWithIndex in elements.withIndex()) {
            mutableList.add(null)
            firestore!!
                .collection(collection.value)
                .add(adapter.toDocument(elementWithIndex.value)!!)
                .addOnSuccessListener {
                    synchronized(this) {
                        mutableList[elementWithIndex.index] = Pair(true, it.id)
                        if (mutableList.fold(true) { a, p -> a && p != null }) {
                            val list = mutableListOf<String?>()
                            for (e in mutableList)
                                list.add(e!!.second)
                            ended.postValue(
                                Pair(
                                    mutableList.fold(true) { a, p -> a && p!!.second != null },
                                    list
                                ), this
                            )
                        }
                    }
                }
                .addOnFailureListener {
                    synchronized(this) {
                        mutableList[elementWithIndex.index] = Pair(true, null)
                        if (mutableList.fold(true) { a, p -> a && p != null }) {
                            val list = mutableListOf<String?>()
                            for (e in mutableList)
                                list.add(e!!.second)
                            ended.postValue(Pair(false, list), this)
                        }
                    }
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

        val successListener = OnSuccessListener<Void> { ended.postValue(true, this) }
        val failureListener = OnFailureListener {
            ended.postValue(false, this)
        }
        val document = firestore!!
            .collection(collection.value)
            .document(id)
        val result = element.thenReturn { adapter.toDocument(it) }
        (if (result == null) document.delete()
        else document.set(result))
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
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
        val mutableList = mutableListOf<Boolean?>()
        for (elementWithIndex in elements.withIndex()) {
            mutableList.add(null)
            val document = firestore!!
                .collection(collection.value)
                .document(elementWithIndex.value.first)
            val result = elementWithIndex.value.second.thenReturn { adapter.toDocument(it) }
            val task = if (result != null)
                document.set(result)
            else
                document.delete()
            task.addOnSuccessListener {
                synchronized(this) {
                    mutableList[elementWithIndex.index] = true
                    if (mutableList.fold(true) { a, b -> a && b != null })
                        ended.postValue(
                            Pair(
                                mutableList.fold(true) { a, b -> a && b!! },
                                mutableList.map { it!! }.toList()
                            ), this
                        )
                }
            }
                .addOnFailureListener {
                    synchronized(this) {
                        mutableList[elementWithIndex.index] = false
                        if (mutableList.fold(true) { a, b -> a && b != null })
                            ended.postValue(
                                Pair(
                                    false,
                                    mutableList.map { it!! }.toList()
                                ), this
                            )
                    }
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
        val successListener = OnSuccessListener<DocumentSnapshot> {
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
        val failureListener = OnFailureListener {
            ended.postValue(false, this)
        }
        firestore!!.collection(collection.value)
            .document(id)
            .get()
            .addOnFailureListener(failureListener)
            .addOnSuccessListener(successListener)
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

        val lastFailureListener = OnFailureListener {
            ended.postValue(false, this)
        }
        val fsCollection = firestore!!.collection(collection.value)
        if (ids != null) {
            val mutableList = mutableListOf<Triple<Boolean, String?, T?>?>()
            if (ids.isEmpty())
                ended.postValue(true, this)
            for (id in ids) {
                mutableList.add(null)
                fsCollection.document(id)
                    .get()
                    .addOnSuccessListener {
                        if (it.data != null) {
                            synchronized(this) {
                                val index = ids.indexOf(it.id)
                                val result = adapter.fromDocument(it.data!!, it.id)
                                mutableList[index] = Triple(result != null, it.id, result)
                                if (mutableList.fold(true) { a, p -> a && p != null }) {
                                    val map = mutableMapOf<String, T>()
                                    for (e in mutableList)
                                        if (e!!.first)
                                            map[e.second!!] = e.third!!
                                    elements.updateAll(map, this)
                                    ended.postValue(true, this)
                                }
                            }
                        } else
                            ended.postValue(false, this)
                    }
                    .addOnFailureListener(lastFailureListener)
            }
        } else {
            (matcher?.match(FirestoreQuery(fsCollection)) ?: FirestoreQuery(fsCollection))
                .get()
                .addOnSuccessListener {
                    val map = mutableMapOf<String, T>()
                    for (e in it) {
                        val result = adapter.fromDocument(e.data, e.id)
                        if (result != null)
                            map[e.id] = result
                    }
                    elements.updateAll(map, this)
                    ended.postValue(true, this)
                }
                .addOnFailureListener(lastFailureListener)
        }
        return ended
    }
}
