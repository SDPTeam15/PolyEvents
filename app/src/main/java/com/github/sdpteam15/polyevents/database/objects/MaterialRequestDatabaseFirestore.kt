package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object MaterialRequestDatabaseFirestore:MaterialRequestDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override val currentUser: UserEntity?
        get()= Database.currentDatabase.currentUser

    override val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    override fun answerMaterialRequest(
        id: String,
        answer: Boolean,
        profile: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMaterialRequestList(
        materialList: Observable<MaterialRequest>,
        matcher: String?,
        profile: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun createMaterialRequest(request: MaterialRequest, profile: UserProfile?) {
        TODO("Not yet implemented")
    }
}