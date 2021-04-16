package com.github.sdpteam15.polyevents.database

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.objects.*
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.AdapterInterface
import com.github.sdpteam15.polyevents.util.UserAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
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

    override val currentProfile: UserProfile?
        get() = null

    override var itemDatabase: ItemDatabaseInterface? = null
        get() = field ?: ItemDatabaseFirestore
    override var zoneDatabase: ZoneDatabaseInterface? = null
        get() = field ?: ZoneDatabaseFirestore
    override var userDatabase: UserDatabaseInterface? = null
        get() = field ?: UserDatabaseFirestore
    override var heatmapDatabase: HeatmapDatabaseInterface? = null
        get() = field ?: HeatmapDatabaseFirestore
    override var eventDatabase: EventDatabaseInterface? = null
        get() = field ?: EventDatabaseFirestore
    override var materialRequestDatabase: MaterialRequestDatabaseInterface? = null
        get() = field ?: MaterialRequestDatabaseFirestore

    override val currentUserObservable = Observable<UserEntity>()
    private var loadSuccess = false
    override var currentUser: UserEntity?
        get() {
            if(UserLogin.currentUserLogin.isConnected()){
                if(currentUserObservable.value==null || !loadSuccess) {
                    currentUserObservable.postValue(UserLogin.currentUserLogin.getCurrentUser()!!,this)
                    firestore!!.collection(USER_COLLECTION.toString())
                        .document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .get()
                        .addOnSuccessListener {
                            if(it.data!=null){
                                currentUserObservable.postValue(UserAdapter.fromDocument(it.data!!, it.id),this)
                                loadSuccess = true
                            }
                        }
                }
                return currentUserObservable.value
            } else {
                return null
            }

        }
        set(value){
            currentUserObservable.value = value
        }


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
        lastFailureListener = OnFailureListener { ended.postValue(false, this) }
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

        lastFailureListener = OnFailureListener { ended.postValue(false, this) }
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
        lastFailureListener = OnFailureListener { ended.postValue(false, this) }
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
        lastFailureListener = OnFailureListener { ended.postValue(false, this) }
        task.addOnSuccessListener(lastSetSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)

        return ended
    }


    override fun <T> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile?
    ): Observable<String> {
        val ended = Observable<String>()
        val task = firestore!!
            .collection(collection.toString())
            .add(adapter.toDocument(element))

        lastAddSuccessListener =
            OnSuccessListener<DocumentReference> { ended.postValue(it.id, this) }
        lastFailureListener = OnFailureListener { ended.postValue("", this) }

        task.addOnSuccessListener(lastAddSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)
        return ended
    }

    override fun <T> addEntity(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        val task = firestore!!
            .collection(collection.toString())
            .add(adapter.toDocument(element))

        lastAddSuccessListener =
            OnSuccessListener<DocumentReference> { ended.postValue(true, this) }
        lastFailureListener = OnFailureListener { ended.postValue(false, this) }

        task.addOnSuccessListener(lastAddSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)
        return ended
    }

    override fun <T> setEntity(
        element: T?,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>?,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        val document = firestore!!
            .collection(collection.toString())
            .document(id)

        val task = if (element == null || adapter == null) {
            document.delete()
        } else {
            document.set(adapter.toDocument(element))
        }

        lastSetSuccessListener = OnSuccessListener<Void> { ended.postValue(true, this) }
        lastFailureListener = OnFailureListener { ended.postValue(false, this) }

        task.addOnSuccessListener(lastSetSuccessListener!!)
        task.addOnFailureListener(lastFailureListener!!)

        return ended
    }

    override fun deleteEntity(
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        userAccess: UserProfile?
    ): Observable<Boolean> = setEntity<Void>(null, id, collection, null)

    override fun <T> getEntity(
        element: Observable<T>,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        lastGetSuccessListener = OnSuccessListener {
            if (it.data != null) {
                element.postValue(adapter.fromDocument(it.data!!, it.id), this)
                ended.postValue(true, this)
            }
            ended.postValue(false, this)
        }

        lastFailureListener = OnFailureListener {
            ended.postValue(false, this)
        }

        val task = firestore!!.collection(collection.toString())
            .document(id)
            .get()

        task.addOnFailureListener(lastFailureListener!!)
        task.addOnSuccessListener(lastGetSuccessListener!!)
        return ended
    }

    override fun <T> getListEntity(
        elements: ObservableList<T>,
        ids: List<String>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterInterface<T>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        val lastFailureListener = OnFailureListener { ended.postValue(false, this) }
        val mutableList = mutableListOf<T?>()
        val fsCollection = firestore!!.collection(collection.toString())
        for (id in ids) {
            mutableList.add(null)
            fsCollection.document(id)
                .get()
                .addOnSuccessListener {
                    val index = ids.indexOf(it.id)
                    mutableList[index] = adapter.fromDocument(it.data!!, it.id)
                    var b = true
                    for (p in mutableList)
                        if (p == null) {
                            b = false
                            break
                        }
                    if (b) {
                        elements.clear(this)
                        for (p in mutableList)
                            elements.add(p!!, this)
                        ended.postValue(true, this)
                    }
                }
                .addOnFailureListener(lastFailureListener)
        }
        return ended
    }
}
