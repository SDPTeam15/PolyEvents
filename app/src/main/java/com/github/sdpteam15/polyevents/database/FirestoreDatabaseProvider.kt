package com.github.sdpteam15.polyevents.database

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.database.DatabaseConstant.EVENT_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_DISPLAY_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_EMAIL
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_UID
import com.github.sdpteam15.polyevents.database.observe.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import com.github.sdpteam15.polyevents.util.EventAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@RequiresApi(Build.VERSION_CODES.O)
object FirestoreDatabaseProvider : DatabaseInterface {
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore


    /**
     * Map used in the firstConnection method. It's public to be able to use it in tests
     */
    val firstConnectionMap = HashMap<String, String>()

    override val currentUser: DatabaseUserInterface?
        get() =
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseUserAdapter(FirebaseAuth.getInstance().currentUser!!)
            } else {
                null
            }

    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> {
        return FakeDatabase.getListProfile(uid,user)
    }

    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean {
        return FakeDatabase.addProfile(profile,uid,user)
    }

    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean {
        return FakeDatabase.removeProfile(profile,uid,user)
    }

    override fun updateProfile(profile: ProfileInterface, user: UserInterface): Boolean {
        return FakeDatabase.updateProfile(profile,user)
    }


    override fun createItem(item: Item, profile: ProfileInterface): Observable<Boolean> {
        return FakeDatabase.createItem(item, profile)
    }

    override fun removeItem(item: Item, profile: ProfileInterface): Observable<Boolean> {
        return FakeDatabase.removeItem(item, profile)
    }

    override fun updateItem(item: Item, profile: ProfileInterface): Observable<Boolean> {
        return FakeDatabase.updateItem(item, profile)
    }

    override fun getItemsList(
        itemList: ObservableList<Item>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        return FakeDatabase.getItemsList(itemList, profile)
    }

    override fun getAvailableItems(
        itemList: ObservableList<Pair<Item,Int>>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        return FakeDatabase.getAvailableItems(itemList, profile)
    }

    override fun createEvent(event: Event, profile: ProfileInterface): Observable<Boolean> {
        return FakeDatabase.createEvent(event, profile)
    }

    override fun updateEvents(event: Event, profile: ProfileInterface): Observable<Boolean> {
        return FakeDatabase.updateEvents(event, profile)
    }

    override fun getEventFromId(
        id: String,
        returnEvent: Observable<Event>,
        profile: ProfileInterface
    ): Observable<Boolean> {
        return FakeDatabase.getEventFromId(id, returnEvent, profile)
    }


    /*
        override fun getListEvent(matcher: String?, number: Int?, eventList: ObservableList<Event>, profile: ProfileInterface): Observable<Boolean> {
            return FakeDatabase.getListEvent(matcher, number, eventList, profile)
        }

        override fun getListEvent(
                matcher: String?,
                number: Int?,
                profile: ProfileInterface
        ): List<Event> {
            return ArrayList<Event>()/*TODO*/
        }
    */

    override fun getListEvent(
        matcher: Matcher?,
        number: Long?,
        eventList: ObservableList<Event>,
        profile: ProfileInterface
    ): Observable<Boolean> {

        val end = Observable<Boolean>()
        val task = firestore!!.collection(EVENT_COLLECTION)
        val query = matcher?.match(task)
        val v = if (query != null) {
            if (number != null) query.limit(number).get() else query.get()
        } else {
            if (number != null) task.limit(number).get() else task.get()
        }
        v.addOnSuccessListener {
            for (d in it!!.documents) {
                val data = d.data
                if (data != null) {
                    val e: Event = EventAdapter.toEventEntity(data)
                    eventList.add(e)
                }
            }
            end.postValue(true)
        }.addOnFailureListener {
            Log.d("FirestoreDatabaseProvider ", it.message!!)
            end.postValue(false)
        }
        return end
        //return FakeDatabase.getListEvent(matcher,number,eventList,profile)
    }
    //Up to here delete


    //Method used to get listener in the test set to mock and test the database
    var lastGetSuccessListener: OnSuccessListener<QuerySnapshot>? = null
    var lastSetSuccessListener: OnSuccessListener<Void>? = null
    var lastFailureListener: OnFailureListener? = null
    var lastMultGetSuccessListener: OnSuccessListener<DocumentSnapshot>? = null

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
        lastGetSuccessListener = OnSuccessListener<QuerySnapshot> {
            onSuccessListener(it)
            ended.postValue(true)
        }

        lastFailureListener = OnFailureListener { ended.postValue(false) }
        task.addOnSuccessListener(lastGetSuccessListener!!)
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
        lastMultGetSuccessListener = OnSuccessListener<DocumentSnapshot> {
            onSuccessListener(it)
            ended.postValue(true)
        }
        lastFailureListener = OnFailureListener { ended.postValue(false) }
        task.addOnSuccessListener(lastMultGetSuccessListener!!)
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

        lastSetSuccessListener = OnSuccessListener<Void> { ended.postValue(true) }
        lastFailureListener = OnFailureListener { ended.postValue(false) }
        task.addOnSuccessListener(lastSetSuccessListener!!)
            .addOnFailureListener(lastFailureListener!!)

        return ended
    }


    override fun updateUserInformation(
        newValues: java.util.HashMap<String, String>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> = thenDoSet(
        firestore!!.collection(USER_COLLECTION)
            .document(uid)
            .update(newValues as Map<String, Any>)
    )


    override fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface
    ): Observable<Boolean> {
        firstConnectionMap[USER_UID] = user.uid
        firstConnectionMap[USER_DISPLAY_NAME] = user.name
        firstConnectionMap[USER_EMAIL] = user.email

        return thenDoSet(
            firestore!!.collection(USER_COLLECTION)
                .document(user.uid)
                .set(firstConnectionMap)
        )
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> = thenDoGet(
        firestore!!.collection(USER_COLLECTION)
            .whereEqualTo(USER_UID, uid)
            .limit(1)
            .get()
    ) { doc: QuerySnapshot ->
        if (doc.documents.size == 1) {
            isInDb.postValue(true)
        } else {
            isInDb.postValue(false)
        }
    }

    override fun getUserInformation(
        user: Observable<UserInterface>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> = thenDoMultGet(
        firestore!!.collection(USER_COLLECTION)
            .document(uid)
            .get()
    ) {
        //TODO once the data class User is created, set the user with the correct value
        user.postValue(userAccess)
    }
    /*
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getItemsList(): MutableList<String> {
        //TODO adapt to firebase
        return FakeDatabase.getItemsList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addItem(item: String): Boolean {
        //TODO adapt to firebase
        return FakeDatabase.addItem(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun removeItem(item: String): Boolean {
        //TODO adapt to firebase
        return FakeDatabase.removeItem(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAvailableItems(): Map<String, Int> {
        //TODO adapt to firebase
        return FakeDatabase.getAvailableItems()
    }

     */
}
