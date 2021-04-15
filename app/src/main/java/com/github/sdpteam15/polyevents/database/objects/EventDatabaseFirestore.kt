package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object EventDatabaseFirestore: EventDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    private var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override val currentUser: UserEntity?
        get()= Database.currentDatabase.currentUser
}