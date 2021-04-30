package com.github.sdpteam15.polyevents.model.database.remote.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.database.remote.adapter.MaterialRequestAdapter
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
        matcher: Matcher?,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getListEntity(
            materialList,
            null,
            matcher,
            MATERIAL_REQUEST_COLLECTION,
            MaterialRequestAdapter
        )


    override fun createMaterialRequest(request: MaterialRequest, userAccess: UserProfile?) =
        db.addEntity(request, MATERIAL_REQUEST_COLLECTION, MaterialRequestAdapter)

}