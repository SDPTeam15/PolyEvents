package com.github.sdpteam15.polyevents.model.database.remote

import android.annotation.SuppressLint
import android.util.Log
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreDatabaseProvider : DatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override var itemDatabase: ItemDatabaseInterface? = null
        get() {
            field = field ?: ItemDatabaseFirestore
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
            field = field ?: RouteDatabase(this)
            return field
        }
    override var userSettingsDatabase: UserSettingsDatabaseInterface? = null
        get() {
            field = field ?: UserSettingsDatabase(this)
            return field
        }

    override val currentUserObservable = Observable<UserEntity>()
    var loadSuccess: Boolean? = false
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
            currentUserObservable.value = value
        }

    //TODO change once the current profile has been developed
    override var currentProfile: UserProfile? = UserProfile(userRole = UserRole.ADMIN)

    //Method used to get listener in the test set to mock and test the database
    var lastQuerySuccessListener: OnSuccessListener<QuerySnapshot>? = null
    var lastSetSuccessListener: OnSuccessListener<Void>? = null
    var lastFailureListener: OnFailureListener? = null
    var lastGetSuccessListener: OnSuccessListener<DocumentSnapshot>? = null
    var lastAddSuccessListener: OnSuccessListener<DocumentReference>? = null

    /**
     * After an add request, add on success and on failure listener (and set them into the corresponding variable to be able to test)
     * @param task: The query that will get document from Firestore
     * @return An observable that will be true if no problem during the request false otherwise
     */
    fun thenDoAdd(
        task: Task<DocumentReference>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        lastAddSuccessListener =
            OnSuccessListener<DocumentReference> { ended.postValue(true, this) }
        lastFailureListener = OnFailureListener {
            it.message?.let { it1 -> Log.d(this::class.qualifiedName, it1) }
            ended.postValue(false, this)
        }
        task.addOnSuccessListener(lastAddSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)

        return ended
    }

    /**
     * After a get request, add on success and on failure listener (and set them into the corresponding variable to be able to test)
     * @param task: The query that will get document from Firestore
     * @param onSuccessListener The listener that will be executed if no problem during the request
     * @return An observable that will be true if no problem during the request false otherwise
     */
    fun thenDoGet(
        task: Task<QuerySnapshot>,
        onSuccessListener: (QuerySnapshot) -> Unit
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        lastQuerySuccessListener = OnSuccessListener<QuerySnapshot> {
            onSuccessListener(it)
            ended.postValue(true, this)
        }

        lastFailureListener = OnFailureListener {
            it.message?.let { it1 -> Log.d(this::class.qualifiedName, it1) }
            ended.postValue(false, this)
        }
        task.addOnSuccessListener(lastQuerySuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)
        return ended
    }

    /**
     * After a get that can have multiple document, add on success and on failure listener (and set them into the corresponding variable to be able to test)
     * @param task: The query that will get documents from Firestore
     * @param onSuccessListener The listener that will be executed if no problem during the request
     * @return An observable that will be true if no problem during the request false otherwise
     */
    fun thenDoMultGet(
        task: Task<DocumentSnapshot>,
        onSuccessListener: (DocumentSnapshot) -> Unit
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        lastGetSuccessListener = OnSuccessListener<DocumentSnapshot> {
            onSuccessListener(it)
            ended.postValue(true, this)
        }
        lastFailureListener = OnFailureListener {
            if (it.message != null)
                Log.d(this::class.qualifiedName, it.message!!)
            ended.postValue(false, this)
        }
        task.addOnSuccessListener(lastGetSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)
        return ended
    }

    /**
     * After a request that modify an online document, add on success and on failure listener (and set them into the corresponding variable to be able to test)
     * @param task: The query that will get documents from Firestore
     * @return An observable that will be true if no problem during the request false otherwise
     */
    fun thenDoSet(
        task: Task<Void>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        lastSetSuccessListener = OnSuccessListener<Void> { ended.postValue(true, this) }
        lastFailureListener = OnFailureListener {
            if (it.message != null)
                Log.d(this::class.qualifiedName, it.message!!)
            ended.postValue(false, this)
        }
        task.addOnSuccessListener(lastSetSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)

        return ended
    }

    override fun <T : Any> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<String> {
        val ended = Observable<String>()
        lastAddSuccessListener =
            OnSuccessListener<DocumentReference> { ended.postValue(it.id, this) }
        lastFailureListener = OnFailureListener {
            if (it.message != null)
                Log.d(this::class.qualifiedName, it.message!!)
            ended.postValue("", this)
        }
        firestore!!
            .collection(collection.value)
            .add(adapter.toDocument(element))
            .addOnSuccessListener(lastAddSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)
        return ended
    }

    override fun <T : Any> addEntity(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Boolean> =
        addEntityAndGetId(element, collection, adapter).mapOnce { it != "" }.then

    override fun <T : Any> addListEntity(
        elements: List<T>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<String>?>> {
        if (elements.isEmpty())
            return Observable(Pair(true, listOf()))
        val ended = Observable<Pair<Boolean, List<String>?>>()
        val mutableList = mutableListOf<String?>()
        for (elementWithIndex in elements.withIndex()) {
            mutableList.add(null)
            firestore!!
                .collection(collection.value)
                .add(adapter.toDocument(elementWithIndex.value))
                .addOnSuccessListener {
                    synchronized(this) {
                        mutableList[elementWithIndex.index] = it.id
                        if (mutableList.fold(true) { a, p -> a && p != null }) {
                            val list = mutableListOf<String>()
                            for (e in mutableList)
                                list.add(e!!)
                            ended.postValue(Pair(true, list), this)
                        }
                    }
                }
                .addOnFailureListener { ended.postValue(Pair(false, null), this) }
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

        lastSetSuccessListener = OnSuccessListener<Void> { ended.postValue(true, this) }
        lastFailureListener = OnFailureListener {
            if (it.message != null)
                Log.d(this::class.qualifiedName, it.message!!)
            ended.postValue(false, this)
        }
        val document = firestore!!
            .collection(collection.value)
            .document(id)
        (if (element == null) document.delete()
        else document.set(adapter.toDocument(element)))
            .addOnSuccessListener(lastSetSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)
        return ended
    }

    override fun <T : Any> setListEntity(
        elements: List<Pair<String, T?>>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Boolean> {
        if (elements.isEmpty())
            return Observable(true)
        val ended = Observable<Boolean>()
        val mutableList = mutableListOf<Boolean>()
        for (elementWithIndex in elements.withIndex()) {
            mutableList.add(false)
            val document = firestore!!
                .collection(collection.value)
                .document(elementWithIndex.value.first)
            val task = if (elementWithIndex.value.second != null)
                document.set(adapter.toDocument(elementWithIndex.value.second!!))
            else
                document.delete()
            task.addOnSuccessListener {
                synchronized(this) {
                    mutableList[elementWithIndex.index] = true
                    if (mutableList.reduce { a, b -> a && b })
                        ended.postValue(true, this)
                }
            }
                .addOnFailureListener { ended.postValue(false, this) }
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
        lastGetSuccessListener = OnSuccessListener {
            if (it.data != null) {
                element.postValue(adapter.fromDocument(it.data!!, it.id), this)
                ended.postValue(true, this)
            } else
                ended.postValue(false, this)
        }
        lastFailureListener = OnFailureListener {
            if (it.message != null)
                Log.d(this::class.qualifiedName, it.message!!)
            ended.postValue(false, this)
        }
        firestore!!.collection(collection.value)
            .document(id)
            .get()
            .addOnFailureListener(lastFailureListener!!)
            .addOnSuccessListener(lastGetSuccessListener!!)
        return ended
    }

    override fun <T : Any> getListEntity(
        elements: ObservableList<T>,
        ids: List<String>?,
        matcher: Matcher?,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        val lastFailureListener = OnFailureListener {
            if (it.message != null)
                Log.d(this::class.qualifiedName, it.message!!)
            ended.postValue(false, this)
        }
        val fsCollection = firestore!!.collection(collection.value)
        if (ids != null) {
            val mutableList = mutableListOf<T?>()
            for (id in ids) {
                mutableList.add(null)
                fsCollection.document(id)
                    .get()
                    .addOnSuccessListener {
                        if (it.data != null) {
                            synchronized(this) {
                                val index = ids.indexOf(it.id)
                                mutableList[index] = adapter.fromDocument(it.data!!, it.id)
                                if (mutableList.fold(true) { a, p -> a && p != null }) {
                                    elements.clear(this)
                                    val list = mutableListOf<T>()
                                    for (e in mutableList)
                                        list.add(e!!)
                                    elements.addAll(list, this)
                                    ended.postValue(true, this)
                                }
                            }
                        } else
                            ended.postValue(false, this)
                    }
                    .addOnFailureListener(lastFailureListener)
            }
        } else {
            val task = matcher?.match(fsCollection)?.get() ?: fsCollection.get()
            task.addOnSuccessListener {
                elements.clear(this)
                val list = mutableListOf<T>()
                for (e in it)
                    list.add(adapter.fromDocument(e.data, e.id))
                elements.addAll(list, this)
                ended.postValue(true, this)
            }
                .addOnFailureListener(lastFailureListener)
        }
        return ended
    }
}
