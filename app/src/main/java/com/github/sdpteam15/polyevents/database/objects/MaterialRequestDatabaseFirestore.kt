package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object MaterialRequestDatabaseFirestore : MaterialRequestDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun answerMaterialRequest(
        id: String,
        answer: Boolean,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMaterialRequestList(
        materialList: Observable<MaterialRequest>,
        matcher: String?,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun createMaterialRequest(request: MaterialRequest, userAccess: UserProfile?) {
        TODO("Not yet implemented")
    }
}