package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.MaterialRequestAdapter
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class MaterialRequestDatabase(private val db: DatabaseInterface) :
    MaterialRequestDatabaseInterface {
    override fun updateMaterialRequest(
        id: String,
        materialRequest: MaterialRequest,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.setEntity(materialRequest, id, MATERIAL_REQUEST_COLLECTION, MaterialRequestAdapter)


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

    override fun getMaterialRequestListByUser(
        materialList: ObservableList<MaterialRequest>,
        userId: String,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        getMaterialRequestList(
            materialList,
            {
                it.whereEqualTo(
                    DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_USER_ID.value,
                    userId
                )
            },
            userAccess
        )

    override fun getMaterialRequestById(
        materialRequest: Observable<MaterialRequest>,
        requestId: String,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.getEntity(
            materialRequest,
            requestId,
            MATERIAL_REQUEST_COLLECTION,
            MaterialRequestAdapter
        )


    override fun createMaterialRequest(request: MaterialRequest, userAccess: UserProfile?) =
        db.addEntity(request, MATERIAL_REQUEST_COLLECTION, MaterialRequestAdapter)


}