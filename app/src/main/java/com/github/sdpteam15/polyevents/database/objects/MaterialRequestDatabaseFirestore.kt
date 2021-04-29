package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
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

    override fun getMaterialRequestList(
        materialList: ObservableList<MaterialRequest>,
        matcher: String?,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getListEntity(
            materialList,
            null,
            null,
            MATERIAL_REQUEST_COLLECTION,
            MaterialRequestAdapter
        )


    override fun createMaterialRequest(request: MaterialRequest, userAccess: UserProfile?) =
        db.addEntity(request, MATERIAL_REQUEST_COLLECTION, MaterialRequestAdapter)

}