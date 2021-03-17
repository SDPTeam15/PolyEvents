package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object FirestoreDatabaseProvider : DatabaseInterface {
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    const val USER_DOCUMENT = "users"
    const val USER_DOCUMENT_ID = "uid"
    const val EVENT_DOCUMENT = "events"
    const val EVENT_DOCUMENT_ID = "eventId"

    override val currentUser: DatabaseUserInterface?
        get() =
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseUserAdapter(FirebaseAuth.getInstance().currentUser!!)
            } else {
                null
            }

    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> {
        return ArrayList<ProfileInterface>() //TODO
    }


    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean {
        return true//TODO
    }


    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean {
        return true//TODO
    }

    override fun updateProfile(profile: ProfileInterface, user: UserInterface): Boolean {
        return true//TODO
    }

    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: ProfileInterface
    ): List<Event> {
        return ArrayList<Event>()//TODO
    }

    override fun getUpcomingEvents(number: Int, profile: ProfileInterface): List<Event> {
        return ArrayList<Event>()//TODO
    }

    override fun getEventFromId(id: String, profile: ProfileInterface): Event? {
        return null//TODO
    }

    override fun updateEvent(Event: Event, profile: ProfileInterface): Boolean {
        return true//TODO
    }

    //Up to here delete
    override fun updateUserInformation(
        newValues: java.util.HashMap<String, String>,
        uid: String,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        val ending = MutableLiveData<Boolean>()
        firestore!!.collection(USER_DOCUMENT)
            .document(uid)
            .update(newValues as Map<String, Any>)
            .addOnSuccessListener { _ -> ending.postValue(true) }
            .addOnFailureListener { _ -> ending.postValue(false) }
        return ending
    }

    override fun firstConnexion(
        user: UserInterface,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        val ended = MutableLiveData<Boolean>()
        val map = HashMap<String, String>()
        map["uid"] = user.uid
        map["displayName"] = user.name
        map["email"] = user.email
        firestore!!.collection(USER_DOCUMENT)
            .document(user.uid)
            .set(map)
            .addOnSuccessListener {
                ended.postValue(true)
            }
            .addOnFailureListener {
                ended.postValue(false)
            }
        return ended
    }

    override fun inDatabase(
        isInDb: MutableLiveData<Boolean>,
        uid: String,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        val ended = MutableLiveData<Boolean>()
        firestore!!.collection(USER_DOCUMENT)
            .whereEqualTo(USER_DOCUMENT_ID, uid)
            .limit(1)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.documents.size == 1) {
                    isInDb.postValue(true)
                } else {
                    isInDb.postValue(false)
                }
                ended.postValue(true)
            }
            .addOnFailureListener {
                ended.postValue(false)
            }
        return ended
    }

    override fun getUserInformation(
        user: MutableLiveData<UserInterface>,
        uid: String,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        val ending = MutableLiveData<Boolean>()
        firestore!!.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                //TODO once the data class User is created, set the user with the correct value
                user.postValue(User.invoke(currentUser!!))
                ending.postValue(true)
            }
            .addOnFailureListener { _ ->
                ending.postValue(false)
            }
        return ending
    }
}
