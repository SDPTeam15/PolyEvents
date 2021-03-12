package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

object FirestoreDatabaseProvider : DatabaseInterface {
    val firestore = Firebase.firestore
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

    override fun removeProfile(
        profile: ProfileInterface,
        success: MutableLiveData<Boolean>,
        uid: String,
        user: UserInterface
    ) {
       //TODO
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
        newValues: HashMap<String, String>,
        success: MutableLiveData<Boolean>,
        uid: String,
        userAccess: UserInterface
    ) {
        firestore.collection("users")
            .document(uid)
            .update(newValues as Map<String, Any>)
            .addOnSuccessListener { _ ->
                success.postValue(true)
            }
    }

    override fun getUserInformation(
        listener: MutableLiveData<User>,
        uid: String,
        userAccess: UserInterface
    ): MutableLiveData<Boolean> {
        val ending = MutableLiveData<Boolean>()
        firestore.collection("users")
            .document("Alessio")
            .get()
            .addOnSuccessListener { document ->
                //listener.postValue(document)
                ending.postValue(true)
            }
            .addOnFailureListener { _ ->
                ending.postValue(false)
            }
        return ending
    }
}
