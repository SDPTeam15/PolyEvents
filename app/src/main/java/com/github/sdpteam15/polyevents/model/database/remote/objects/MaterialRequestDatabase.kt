package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.MaterialRequestAdapter
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class MaterialRequestDatabase(private val db: DatabaseInterface) :
    MaterialRequestDatabaseInterface {
    override fun updateMaterialRequest(
        id: String,
        materialRequest: MaterialRequest
    ): Observable<Boolean> =
        db.setEntity(materialRequest, id, MATERIAL_REQUEST_COLLECTION, MaterialRequestAdapter)


    override fun getMaterialRequestList(
        materialList: ObservableList<MaterialRequest>,
        matcher: Matcher?
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
        userId: String
    ): Observable<Boolean> =
        getMaterialRequestList(
            materialList
        ) {
            it.whereEqualTo(
                DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_USER_ID.value,
                userId
            )
        }

    override fun deleteMaterialRequest(
        materialRequestId: String
    ): Observable<Boolean> =
        db.deleteEntity(materialRequestId, MATERIAL_REQUEST_COLLECTION)


    override fun createMaterialRequest(request: MaterialRequest) =
        db.addEntity(request, MATERIAL_REQUEST_COLLECTION, MaterialRequestAdapter)

    override fun getMaterialRequestById(
        materialRequest: Observable<MaterialRequest>,
        requestId: String
    ): Observable<Boolean> =
        db.getEntity(
            materialRequest,
            requestId,
            MATERIAL_REQUEST_COLLECTION,
            MaterialRequestAdapter
        )

}