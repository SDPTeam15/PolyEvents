package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object FirestoreDatabaseProvider : DatabaseInterface {
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    const val USER_DOCUMENT = "users"
    const val USER_DOCUMENT_ID = "uid"
    const val EVENT_DOCUMENT = "events"
    const val EVENT_DOCUMENT_ID = "eventId"
    val firstConnectionMap = HashMap<String, String>()

    override val currentUser: DatabaseUserInterface?
        get() =
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseUserAdapter(FirebaseAuth.getInstance().currentUser!!)
            } else {
                null
            }

    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> {
        return ArrayList<ProfileInterface>()/*TODO*/
    }

    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean {
        return true/*TODO*/
    }

    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean {
        return true/*TODO*/
    }

    override fun updateProfile(profile: ProfileInterface, user: UserInterface): Boolean {
        return true/*TODO*/
    }

    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: ProfileInterface
    ): List<Event> {
        return ArrayList<Event>()/*TODO*/
    }

    override fun getUpcomingEvents(number: Int, profile: ProfileInterface): List<Event> {
        return ArrayList<Event>()/*TODO*/
    }

    override fun getEventFromId(id: String, profile: ProfileInterface): Event? {
        return null/*TODO*/
    }

    override fun updateEvent(Event: Event, profile: ProfileInterface): Boolean {
        return true/*TODO*/
    }
    //Up to here delete


    //Method used to get listener in the task to mock and test the database
    var lastGetSuccessListener: OnSuccessListener<QuerySnapshot>? = null
    var lastSetSuccessListener: OnSuccessListener<Void>? = null
    var lastFailureListener: OnFailureListener? = null
    var lastMultGetSuccessListener: OnSuccessListener<DocumentSnapshot>? = null


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
        firestore!!.collection(USER_DOCUMENT)
            .document(uid)
            .update(newValues as Map<String, Any>)
    )


    override fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface
    ): Observable<Boolean> {
        firstConnectionMap["uid"] = user.uid
        firstConnectionMap["displayName"] = user.name
        firstConnectionMap["email"] = user.email

        return thenDoSet(
            firestore!!.collection(USER_DOCUMENT)
                .document(user.uid)
                .set(firstConnectionMap)
        )
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserInterface
    ): Observable<Boolean> = thenDoGet(
        firestore!!.collection(USER_DOCUMENT)
            .whereEqualTo(USER_DOCUMENT_ID, uid)
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
        firestore!!.collection("users")
            .document(uid)
            .get()
    ) { document ->
        //TODO once the data class User is created, set the user with the correct value
        user.postValue(userAccess)
    }
}
