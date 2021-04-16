package com.github.sdpteam15.polyevents.database

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ITEM_COUNT
import com.github.sdpteam15.polyevents.database.DatabaseConstant.LOCATIONS_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.LOCATIONS_POINT
import com.github.sdpteam15.polyevents.database.DatabaseConstant.PROFILE_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_UID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_COLLECTION
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.model.*
import com.github.sdpteam15.polyevents.util.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.selects.whileSelect
import kotlin.concurrent.thread

object FirestoreDatabaseProvider : DatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    /**
     * Map used in the firstConnection method. It's public to be able to use it in tests
     */
    var firstConnectionUser: UserEntity = UserEntity(uid = "DEFAULT")

    override var currentUser: UserEntity? = null
        get() {
            if (FirebaseAuth.getInstance().currentUser != null) {
                firestore!!.collection(USER_COLLECTION)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .get()
                    .addOnSuccessListener {field = UserAdapter.fromDocument(it.data!!, it.id)}
            }
            // UserLogin.currentUserLogin.getCurrentUser()
            return field
        }

    var profiles: MutableList<UserProfile> = mutableListOf()

    override val currentProfile: UserProfile?
        get() = null // TODO("Not yet implemented")

    override fun getProfilesList(uid: String, user: UserEntity?): List<UserProfile> {

        return profiles // TODO : Not yet Implemented
    }

    override fun addProfile(profile: UserProfile, uid: String, user: UserEntity?): Boolean =
        profiles.add(profile)// TODO : Not yet Implemented

    override fun createItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> = thenDoAdd(
        firestore!!.collection(ITEM_COLLECTION).add(ItemEntityAdapter.toItemDocument(item, count))
    )


    override fun removeItem(itemId: String, profile: UserProfile?): Observable<Boolean> = thenDoSet(
        firestore!!.collection(ITEM_COLLECTION)
            .document(itemId).delete()
    )

    override fun updateItem(
        item: Item,
        count: Int,
        profile: UserProfile?
    ): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (item.itemId == null) return createItem(item, count, profile)
        return thenDoSet(
            firestore!!
                .collection(ITEM_COLLECTION)
                .document(item.itemId!!)
                .set(ItemEntityAdapter.toItemDocument(item, count))
        )
    }

    override fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile?
    ): Observable<Boolean> {
        return thenDoGet(
            firestore!!.collection(ITEM_COLLECTION).get()
        ) { querySnapshot ->
            itemList.clear(this)
            val items = querySnapshot.documents.map {
                ItemEntityAdapter.toItemEntity(it.data!!, it.id)
            }
            itemList.addAll(items, this)
        }
    }

    override fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile?
    ): Observable<Boolean> {
        return thenDoGet(
            firestore!!.collection(ITEM_COLLECTION).whereGreaterThan(ITEM_COUNT, 0).get()
        ) { querySnapshot ->
            itemList.clear(this)
            val items = querySnapshot.documents.map {
                ItemEntityAdapter.toItemEntity(it.data!!, it.id)
            }
            itemList.addAll(items, this)
        }
    }

    override fun createEvent(event: Event, profile: UserProfile?): Observable<Boolean> =
        thenDoAdd(firestore!!.collection(EVENT_COLLECTION).add(EventAdapter.toDocument(event)))


    override fun updateEvents(event: Event, profile: UserProfile?): Observable<Boolean> {
        // TODO should update add item if non existent in database ?
        // if (event.eventId == null) return createEvent(event, profile)
        return thenDoSet(
            firestore!!.collection(EVENT_COLLECTION).document(event.eventId!!).set(EventAdapter.toDocument(event))
        )
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        profile: UserProfile?
    ): Observable<Boolean> = thenDoMultGet(
        firestore!!.collection(EVENT_COLLECTION)
            .document(id).get()
    ) {
        returnEvent.postValue(
            EventAdapter.fromDocument(it.data!!, it.id), this
        )
    }

    override fun getListEvent(
        matcher: Matcher?,
        number: Long?,
        eventList: ObservableList<Event>,
        profile: UserProfile?
    ): Observable<Boolean> {

        val task = firestore!!.collection(EVENT_COLLECTION)
        val query = matcher?.match(task)
        val v = if (query != null) {
            if (number != null) query.limit(number).get() else query.get()
        } else {
            if (number != null) task.limit(number).get() else task.get()
        }
        return thenDoGet(v) {
            eventList.clear(this)
            for (d in it.documents) {
                val data = d.data
                if (data != null) {
                    val e: Event = EventAdapter.fromDocument(data, d.id)
                    eventList.add(e)
                }
            }
        }
    }


    //Method used to get listener in the test set to mock and test the database
    var lastQuerySuccessListener: OnSuccessListener<QuerySnapshot>? = null
    var lastSetSuccessListener: OnSuccessListener<Void>? = null
    var lastFailureListener: OnFailureListener? = null
    var lastGetSuccessListener: OnSuccessListener<DocumentSnapshot>? = null
    var lastAddSuccessListener: OnSuccessListener<DocumentReference>?=null

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


    override fun updateUserInformation(
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> = thenDoSet(
        firestore!!.collection(USER_COLLECTION)
            .document(uid)
            .update(newValues as Map<String, Any>)
    )

    override fun firstConnexion(
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        firstConnectionUser = user

        return thenDoSet(
            firestore!!.collection(USER_COLLECTION)
                .document(user.uid)
                .set(firstConnectionUser)
        )
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> = thenDoGet(
        firestore!!.collection(USER_COLLECTION)
            .whereEqualTo(USER_UID, uid)
            .limit(1)
            .get()
    ) { doc: QuerySnapshot ->
        if (doc.documents.size == 1) {
            isInDb.postValue(true, this)
        } else {
            isInDb.postValue(false, this)
        }
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String?,
        userAccess: UserEntity?
    ): Observable<Boolean> = thenDoMultGet(
        firestore!!.collection(USER_COLLECTION)
            .document(uid!!)
            .get()
    ) {
        user.postValue(UserAdapter.fromDocument(it.data!!, it.id), this)
    }

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getItemsList(): MutableList<String> {



    override fun getItemsList(): MutableList<Item> {
        //TODO adapt to firebase
        return FakeDatabase.getItemsList()
    }

    override fun addItem(item: Item): Boolean {
        //TODO adapt to firebase
        return FakeDatabase.addItem(item)
    }

    override fun removeItem(item: Item): Boolean {
        //TODO adapt to firebase
        return FakeDatabase.removeItem(item)
    }

    override fun getAvailableItems(): Map<String, Int> {
        //TODO adapt to firebase
        return FakeDatabase.getAvailableItems()
    }

     */

    override fun setUserLocation(
        location: LatLng,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return thenDoSet(
            firestore!!.collection(LOCATIONS_COLLECTION)
                .document(userAccess!!.uid)
                .set(
                    hashMapOf(LOCATIONS_POINT to GeoPoint(location.latitude, location.longitude)),
                    SetOptions.merge()
                )
        )
    }

    override fun getUsersLocations(
        usersLocations: Observable<List<LatLng>>,
        userAccess: UserEntity?
    ): Observable<Boolean> = thenDoGet(
        firestore!!.collection(LOCATIONS_COLLECTION)
            .get()
    ) { querySnapshot ->
        val locations = querySnapshot.documents.map {
            val geoPoint = it.data!![LOCATIONS_POINT] as GeoPoint
            LatLng(geoPoint.latitude, geoPoint.longitude)
        }
        usersLocations.postValue(locations, this)
    }

    override fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        val lastFailureListener = OnFailureListener { ended.postValue(false, this) }

        val update: () -> Unit = {
            user.addNewProfile(profile)

            firestore!!.collection(USER_COLLECTION)
                .document(user.uid)
                .set(UserAdapter.toDocument(user))
                .addOnSuccessListener {
                    firestore!!.collection(PROFILE_COLLECTION)
                        .document(profile.pid!!)
                        .set(ProfileAdapter.toDocument(profile))
                        .addOnSuccessListener { ended.postValue(true, this) }
                        .addOnFailureListener(lastFailureListener)
                }
                .addOnFailureListener(lastFailureListener)
        }

        if (profile.pid == null)
            firestore!!.collection(PROFILE_COLLECTION)
                .add(ProfileAdapter.toDocument(profile))
                .addOnSuccessListener {
                    profile.pid = it.id
                    update()
                }
                .addOnFailureListener(lastFailureListener)
        else
            update()

        return ended
    }

    override fun updateProfile(profile: UserProfile, userAccess: UserEntity?): Observable<Boolean> =
        setEntity(
            profile,
            profile.pid!!,
            PROFILE_COLLECTION,
            ProfileAdapter
        )

    override fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        getListEntity(
            profiles,
            user.profiles,
            PROFILE_COLLECTION,
            ProfileAdapter
        )

    override fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        getListEntity(
            users,
            profile.users,
            USER_COLLECTION,
            UserAdapter
        )

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> = getEntity(
        profile,
        pid,
        PROFILE_COLLECTION,
        ProfileAdapter
    )

    override fun removeProfile(profile: UserProfile, user: UserEntity?) = deleteEntity(
        profile.pid!!,
        PROFILE_COLLECTION
    )


    override fun <T> addEntity(
        element: T,
        collection: String,
        adapter: AdapterInterface<T>
    ): Observable<String> {
        val ended = Observable<String>()
        firestore!!.collection(collection)
            .add(adapter.toDocument(element))
            .addOnSuccessListener { ended.postValue(it.id, this) }
            .addOnFailureListener { ended.postValue("", this) }
        return ended
    }

    override fun <T> setEntity(
        element: T?,
        id: String,
        collection: String,
        adapter: AdapterInterface<T>?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        val document = firestore!!.collection(collection).document(id)
        (if (element == null || adapter == null) document.delete()
        else document.set(adapter.toDocument(element)))
            .addOnSuccessListener { ended.postValue(true, this) }
            .addOnFailureListener { ended.postValue(false, this) }
        return ended
    }

    override fun deleteEntity(
        id: String,
        collection: String
    ): Observable<Boolean> = setEntity<Void>(null, id, collection, null)

    override fun <T> getEntity(
        element: Observable<T>,
        id: String,
        collection: String,
        adapter: AdapterInterface<T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()
        firestore!!.collection(collection).document(id)
            .get()
            .addOnSuccessListener {
                if (it.data != null) {
                    element.postValue(adapter.fromDocument(it.data!!, it.id), this)
                    ended.postValue(true, this)
                }
                ended.postValue(false, this)
            }
            .addOnFailureListener { ended.postValue(false, this) }
        return ended
    }

    override fun <T> getListEntity(
        elements: ObservableList<T>,
        ids: List<String>,
        collection: String,
        adapter: AdapterInterface<T>
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        val lastFailureListener = OnFailureListener { ended.postValue(false, this) }
        val mutableList = mutableListOf<T?>()
        val fsCollection = firestore!!.collection(collection)
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

    /*
     * Zone related methods
     */
    override fun createZone(zone: Zone, userAccess: UserEntity?): Observable<Boolean> {
        return thenDoAdd(
            firestore!!
                .collection(ZONE_COLLECTION)
                .add(ZoneAdapter.toDocument(zone))
        )
    }


    override fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return thenDoMultGet(
            firestore!!
                .collection(ZONE_COLLECTION)
                .document(zoneId)
                .get()
        ) {
            zone.postValue(ZoneAdapter.fromDocument(it.data!!, it.id), this)
        }
    }

    override fun updateZoneInformation(
        zoneId: String,
        newZone: Zone,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return thenDoSet(
            firestore!!
                .collection(ZONE_COLLECTION)
                .document(zoneId)
                .update(ZoneAdapter.toDocument(newZone))
        )
    }
}
