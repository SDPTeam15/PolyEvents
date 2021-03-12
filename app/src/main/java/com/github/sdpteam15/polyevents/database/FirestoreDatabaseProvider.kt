package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.*
import java.util.HashMap
import java.util.concurrent.Future
import kotlin.properties.ObservableProperty

object FirestoreDatabaseProvider: DatabaseInterface {
    val firestore = Firebase.firestore

    override fun getListProfile(
        profilListToUpdate: MutableLiveData<List<Profile>>,
        uid: String,
        user: UserInterface
    ) {
        TODO("Not yet implemented")
    }

    override fun addProfile(
        profile: ProfileInterface,
        success: MutableLiveData<Boolean>,
        uid: String,
        user: UserInterface
    ) {
        TODO("Not yet implemented")
    }

    override fun removeProfile(
        profile: ProfileInterface,
        success: MutableLiveData<Boolean>,
        uid: String,
        user: UserInterface
    ) {
        TODO("Not yet implemented")
    }

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
    ) {
        println("Commencement")
        firestore.collection("users").document("Alessio2").update("section","info baaaaaaah2")
        firestore.collection("users")
            .document("Alessio")
            .get()
            .addOnSuccessListener { document ->
                //listener.postValue(document)
            }
    }
}