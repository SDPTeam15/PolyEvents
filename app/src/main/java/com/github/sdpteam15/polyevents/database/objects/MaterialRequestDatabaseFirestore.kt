package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.MaterialRequestAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MaterialRequestDatabaseFirestore(private val db: DatabaseInterface) :
    MaterialRequestDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun createMaterialRequest(request: MaterialRequest, userAccess: UserProfile?) {
        db.addEntity(request, MATERIAL_REQUEST_COLLECTION, MaterialRequestAdapter)
        MaterialRequestAdapter.toDocument(request)
    }
}